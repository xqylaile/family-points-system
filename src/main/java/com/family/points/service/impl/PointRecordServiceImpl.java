package com.family.points.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.points.common.Constants;
import com.family.points.entity.FamilyMember;
import com.family.points.entity.PointRecord;
import com.family.points.entity.PointRule;
import com.family.points.mapper.PointRecordMapper;
import com.family.points.service.FamilyMemberService;
import com.family.points.service.PointRecordService;
import com.family.points.service.PointRuleService;
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
 * 积分变更记录服务实现类
 */
@Service
public class PointRecordServiceImpl extends ServiceImpl<PointRecordMapper, PointRecord> implements PointRecordService {

    @Autowired
    private FamilyMemberService familyMemberService;

    @Autowired
    private PointRuleService pointRuleService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPointRecord(PointRecord record) {
        FamilyMember member = familyMemberService.getById(record.getMemberId());
        if (member == null) {
            throw new RuntimeException("成员不存在");
        }

        PointRule rule = pointRuleService.getById(record.getRuleId());
        if (rule == null) {
            throw new RuntimeException("规则不存在");
        }

        // 设置变更前后积分
        record.setBeforePoints(member.getCurrentPoints());

        if (Constants.RULE_TYPE_ADD.equals(record.getChangeType())) {
            record.setAfterPoints(member.getCurrentPoints() + record.getPointValue());
        } else {
            record.setAfterPoints(member.getCurrentPoints() - record.getPointValue());
        }

        record.setStatus(1);
        save(record);

        // 更新成员积分
        familyMemberService.updatePoints(record.getMemberId(), record.getPointValue(), record.getChangeType());

        // 增加规则使用次数
        pointRuleService.increaseUseCount(record.getRuleId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelRecord(Long recordId) {
        PointRecord record = getById(recordId);
        if (record == null) {
            throw new RuntimeException("记录不存在");
        }

        if (record.getStatus() == 0) {
            throw new RuntimeException("该记录已撤销");
        }

        // 撤销记录状态
        record.setStatus(0);
        updateById(record);

        // 恢复成员积分
        FamilyMember member = familyMemberService.getById(record.getMemberId());
        if (Constants.RULE_TYPE_ADD.equals(record.getChangeType())) {
            member.setCurrentPoints(member.getCurrentPoints() - record.getPointValue());
            member.setTotalEarnedPoints(member.getTotalEarnedPoints() - record.getPointValue());
        } else {
            member.setCurrentPoints(member.getCurrentPoints() + record.getPointValue());
        }
        familyMemberService.updateById(member);
    }

    @Override
    public List<PointRecord> listByCondition(Long memberId, Long ruleId, String changeType, String startDate, String endDate) {
        LambdaQueryWrapper<PointRecord> queryWrapper = new LambdaQueryWrapper<>();

        if (memberId != null) {
            queryWrapper.eq(PointRecord::getMemberId, memberId);
        }
        if (ruleId != null) {
            queryWrapper.eq(PointRecord::getRuleId, ruleId);
        }
        if (changeType != null && !changeType.isEmpty()) {
            queryWrapper.eq(PointRecord::getChangeType, changeType);
        }
        if (startDate != null && !startDate.isEmpty()) {
            queryWrapper.ge(PointRecord::getCreateTime, LocalDateTime.parse(startDate + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        if (endDate != null && !endDate.isEmpty()) {
            queryWrapper.le(PointRecord::getCreateTime, LocalDateTime.parse(endDate + " 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        queryWrapper.orderByDesc(PointRecord::getCreateTime);
        return list(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> listRecordsWithDetails(Long memberId, String startDate, String endDate) {
        List<PointRecord> records = listByCondition(memberId, null, null, startDate, endDate);
        List<Map<String, Object>> result = new ArrayList<>();

        for (PointRecord record : records) {
            Map<String, Object> map = new HashMap<>();
            map.put("record", record);

            FamilyMember member = familyMemberService.getById(record.getMemberId());
            map.put("member", member);

            PointRule rule = pointRuleService.getById(record.getRuleId());
            map.put("rule", rule);

            result.add(map);
        }

        return result;
    }
}
