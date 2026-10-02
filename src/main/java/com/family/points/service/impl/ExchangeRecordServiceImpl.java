package com.family.points.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.points.common.Constants;
import com.family.points.entity.ExchangeRecord;
import com.family.points.entity.FamilyMember;
import com.family.points.entity.RewardItem;
import com.family.points.mapper.ExchangeRecordMapper;
import com.family.points.service.ExchangeRecordService;
import com.family.points.service.FamilyMemberService;
import com.family.points.service.RewardItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 兑换记录服务实现类
 */
@Service
public class ExchangeRecordServiceImpl extends ServiceImpl<ExchangeRecordMapper, ExchangeRecord> implements ExchangeRecordService {

    @Autowired
    private FamilyMemberService familyMemberService;

    @Autowired
    private RewardItemService rewardItemService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void exchangeReward(Long memberId, Long itemId, Integer quantity) {
        FamilyMember member = familyMemberService.getByIdForUpdate(memberId);

        RewardItem item = rewardItemService.getById(itemId);
        if (item == null) {
            throw new RuntimeException("奖品不存在");
        }

        if (item.getStatus() != Constants.STATUS_ENABLED) {
            throw new RuntimeException("该奖品已下架");
        }

        if (item.getStock() < quantity) {
            throw new RuntimeException("奖品库存不足");
        }

        Integer totalPoints = item.getRequiredPoints() * quantity;
        if (member.getCurrentPoints() < totalPoints) {
            throw new RuntimeException("积分不足，无法兑换");
        }

        // 创建兑换记录
        ExchangeRecord record = new ExchangeRecord();
        record.setMemberId(memberId);
        record.setItemId(itemId);
        record.setItemName(item.getItemName());
        record.setQuantity(quantity);
        record.setTotalPoints(totalPoints);
        record.setExchangeStatus(Constants.EXCHANGE_STATUS_EXCHANGED);
        if (!save(record)) {
            throw new RuntimeException("保存兑换记录失败");
        }

        // 扣减积分
        member.setCurrentPoints(member.getCurrentPoints() - totalPoints);
        member.setTotalSpentPoints(member.getTotalSpentPoints() + totalPoints);
        if (!familyMemberService.updateById(member)) {
            throw new RuntimeException("扣减成员积分失败");
        }

        // 减少库存
        rewardItemService.decreaseStock(itemId, quantity);

        // 增加兑换次数
        rewardItemService.increaseExchangeCount(itemId, quantity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelExchange(Long recordId) {
        ExchangeRecord record = getById(recordId);
        if (record == null) {
            throw new RuntimeException("兑换记录不存在");
        }

        // 与结算保持相同锁顺序，并使用锁定后读到的最新记录。
        FamilyMember member = familyMemberService.getByIdForUpdate(record.getMemberId());
        record = baseMapper.selectByIdForUpdate(recordId);
        if (record == null) {
            throw new RuntimeException("兑换记录不存在或已撤销");
        }
        if (record.getSettlementId() != null) {
            throw new RuntimeException("该兑换已参与月度结算，不能撤销");
        }

        if (!removeById(recordId)) {
            throw new RuntimeException("撤销兑换失败");
        }

        // 恢复积分
        member.setCurrentPoints(member.getCurrentPoints() + record.getTotalPoints());
        member.setTotalSpentPoints(member.getTotalSpentPoints() - record.getTotalPoints());
        if (!familyMemberService.updateById(member)) {
            throw new RuntimeException("恢复成员积分失败");
        }

        // 恢复库存
        rewardItemService.restoreStock(record.getItemId(), record.getQuantity());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateExchangeStatus(Long recordId, String status) {
        ExchangeRecord record = getById(recordId);
        if (record == null) {
            throw new RuntimeException("兑换记录不存在");
        }

        if (!update(new LambdaUpdateWrapper<ExchangeRecord>()
                .eq(ExchangeRecord::getId, recordId).set(ExchangeRecord::getExchangeStatus, status))) {
            throw new RuntimeException("更新兑换状态失败");
        }
    }

    @Override
    public List<Map<String, Object>> listRecordsWithDetails(Long memberId, String startDate, String endDate) {
        List<ExchangeRecord> records = listByCondition(memberId, startDate, endDate);
        List<Map<String, Object>> result = new ArrayList<>();

        for (ExchangeRecord record : records) {
            Map<String, Object> map = new HashMap<>();
            map.put("record", record);

            FamilyMember member = familyMemberService.getById(record.getMemberId());
            map.put("member", member);

            RewardItem item = rewardItemService.getById(record.getItemId());
            map.put("item", item);

            result.add(map);
        }

        return result;
    }

    @Override
    public List<ExchangeRecord> listByCondition(Long memberId, String startDate, String endDate) {
        LambdaQueryWrapper<ExchangeRecord> queryWrapper = new LambdaQueryWrapper<>();

        if (memberId != null) {
            queryWrapper.eq(ExchangeRecord::getMemberId, memberId);
        }
        if (startDate != null && !startDate.isEmpty()) {
            queryWrapper.ge(ExchangeRecord::getCreateTime, LocalDateTime.parse(startDate + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (endDate != null && !endDate.isEmpty()) {
            queryWrapper.le(ExchangeRecord::getCreateTime, LocalDateTime.parse(endDate + " 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        queryWrapper.orderByDesc(ExchangeRecord::getCreateTime);
        return list(queryWrapper);
    }
}
