package com.family.points.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.family.points.common.Constants;
import com.family.points.entity.FamilyMember;
import com.family.points.entity.MonthlySettlement;
import com.family.points.entity.SettlementDetail;
import com.family.points.mapper.ExchangeRecordMapper;
import com.family.points.mapper.FamilyMemberMapper;
import com.family.points.mapper.MonthlySettlementMapper;
import com.family.points.mapper.PointRecordMapper;
import com.family.points.mapper.SettlementDetailMapper;
import com.family.points.service.MonthlySettlementService;
import com.family.points.util.PointRankingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 月度结算服务实现类
 */
@Service
public class MonthlySettlementServiceImpl implements MonthlySettlementService {

    @Autowired
    private MonthlySettlementMapper settlementMapper;

    @Autowired
    private SettlementDetailMapper detailMapper;

    @Autowired
    private FamilyMemberMapper memberMapper;

    @Autowired
    private PointRecordMapper pointRecordMapper;

    @Autowired
    private ExchangeRecordMapper exchangeRecordMapper;

    @Autowired
    private Clock settlementClock;

    @Override
    public String getSettlementMonth() {
        return YearMonth.now(settlementClock).minusMonths(1).toString();
    }

    @Override
    public boolean isSettled(String month) {
        return settlementMapper.selectCount(new LambdaQueryWrapper<MonthlySettlement>()
                .eq(MonthlySettlement::getSettlementMonth, month)) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MonthlySettlement settle(String expectedMonth) {
        // 所有积分操作均先锁成员；按 ID 顺序锁定，防止记分与清零相互覆盖。
        List<FamilyMember> members = memberMapper.selectAllForUpdate().stream()
                .filter(member -> Integer.valueOf(Constants.STATUS_ENABLED).equals(member.getStatus()))
                .collect(Collectors.toList());
        LocalDateTime now = LocalDateTime.now(settlementClock);
        String month = YearMonth.from(now).minusMonths(1).toString();
        if (!month.equals(expectedMonth)) {
            throw new RuntimeException("结算月份已变化，请刷新页面后重试");
        }
        if (isSettled(month)) {
            throw new RuntimeException(month + " 已结算，请勿重复操作");
        }
        if (members.isEmpty()) {
            throw new RuntimeException("没有启用的成员，无法结算");
        }

        MonthlySettlement settlement = new MonthlySettlement();
        settlement.setSettlementMonth(month);
        settlement.setCreateTime(now);
        try {
            if (settlementMapper.insert(settlement) != 1) {
                throw new RuntimeException("保存结算失败");
            }
        } catch (DuplicateKeyException e) {
            throw new RuntimeException(month + " 已结算，请勿重复操作", e);
        }

        List<SettlementDetail> details = new ArrayList<>();
        for (FamilyMember member : PointRankingUtil.rank(members)) {
            int originalPoints = PointRankingUtil.pointsOf(member);
            BigDecimal settledPoints = BigDecimal.valueOf(originalPoints);
            if (member.getRankNo() == 3) {
                settledPoints = settledPoints.divide(BigDecimal.valueOf(2));
            }

            SettlementDetail detail = new SettlementDetail();
            detail.setSettlementId(settlement.getId());
            detail.setMemberId(member.getId());
            detail.setMemberName(member.getName());
            detail.setRankNo(member.getRankNo());
            detail.setOriginalPoints(originalPoints);
            detail.setSettledPoints(settledPoints);
            if (detailMapper.insert(detail) != 1) {
                throw new RuntimeException("保存结算明细失败");
            }
            details.add(detail);

            // 冻结旧记录的撤销操作，不删除历史记录，也不改累计获得/消费。
            pointRecordMapper.markSettled(member.getId(), settlement.getId());
            exchangeRecordMapper.markSettled(member.getId(), settlement.getId());
            if (memberMapper.update(null, new LambdaUpdateWrapper<FamilyMember>()
                    .eq(FamilyMember::getId, member.getId())
                    .set(FamilyMember::getCurrentPoints, 0)
                    .set(FamilyMember::getUpdateTime, now)) != 1) {
                throw new RuntimeException("清零成员积分失败");
            }
        }
        settlement.setDetails(details);
        return settlement;
    }

    @Override
    public List<MonthlySettlement> listSettlements(String month) {
        LambdaQueryWrapper<MonthlySettlement> query = new LambdaQueryWrapper<>();
        if (month != null && !month.isEmpty()) {
            query.eq(MonthlySettlement::getSettlementMonth, month);
        }
        query.orderByDesc(MonthlySettlement::getSettlementMonth);
        List<MonthlySettlement> settlements = settlementMapper.selectList(query);
        if (settlements.isEmpty()) {
            return settlements;
        }
        List<Long> ids = settlements.stream().map(MonthlySettlement::getId).collect(Collectors.toList());
        Map<Long, List<SettlementDetail>> details = detailMapper.selectList(
                new LambdaQueryWrapper<SettlementDetail>()
                        .in(SettlementDetail::getSettlementId, ids)
                        .orderByAsc(SettlementDetail::getRankNo, SettlementDetail::getMemberId))
                .stream().collect(Collectors.groupingBy(SettlementDetail::getSettlementId));
        for (MonthlySettlement settlement : settlements) {
            settlement.setDetails(details.getOrDefault(settlement.getId(), Collections.emptyList()));
        }
        return settlements;
    }
}
