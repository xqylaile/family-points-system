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
    public void addPointRecord(PointRecord record, PointRule customRule, boolean saveAsRule) {
        if (record.getPointValue() == null || record.getPointValue() <= 0) {
            throw new RuntimeException("积分数值必须大于0");
        }

        FamilyMember member = familyMemberService.getByIdForUpdate(record.getMemberId());

        if (customRule == null) {
            // 选择已有规则
            PointRule rule = pointRuleService.getById(record.getRuleId());
            if (rule == null) {
                throw new RuntimeException("规则不存在");
            }
            record.setCustomRuleName(null);
        } else {
            // 自定义规则，按是否加入规则列表分别处理
            applyCustomRule(record, customRule, member, saveAsRule);
        }

        // 设置变更前后积分
        record.setBeforePoints(member.getCurrentPoints());

        if (Constants.RULE_TYPE_ADD.equals(record.getChangeType())) {
            record.setAfterPoints(member.getCurrentPoints() + record.getPointValue());
        } else {
            record.setAfterPoints(member.getCurrentPoints() - record.getPointValue());
        }

        record.setId(null);
        record.setSettlementId(null);
        record.setDeleted(0);
        record.setStatus(1);
        if (!save(record)) {
            throw new RuntimeException("保存积分记录失败");
        }

        // 更新成员积分
        familyMemberService.updatePoints(record.getMemberId(), record.getPointValue(), record.getChangeType());

        // 增加规则使用次数（自定义规则未加入规则列表时不参与统计）
        if (record.getRuleId() != null) {
            pointRuleService.increaseUseCount(record.getRuleId());
        }
    }

    /**
     * 处理自定义规则：加入规则列表时新建规则并关联，否则仅把规则名称记录在本次变更上
     */
    private void applyCustomRule(PointRecord record, PointRule customRule, FamilyMember member, boolean saveAsRule) {
        String ruleName = customRule.getRuleName() == null ? "" : customRule.getRuleName().trim();
        if (ruleName.isEmpty()) {
            throw new RuntimeException("请填写自定义规则名称");
        }

        if (!saveAsRule) {
            // 不加入规则列表，仅本次变更使用
            record.setRuleId(null);
            record.setCustomRuleName(ruleName);
            return;
        }

        if (pointRuleService.getByNameAndType(ruleName, record.getChangeType()) != null) {
            throw new RuntimeException("规则列表中已存在同名规则：" + ruleName + "，请直接选择该规则或换一个名称");
        }

        String applyTo = customRule.getApplyTo();
        if (applyTo == null || applyTo.isEmpty()) {
            // 未指定适用对象时，默认只适用于当前成员类型
            applyTo = member.getMemberType();
        }
        if (!Constants.APPLY_TO_PARENT.equals(applyTo)
                && !Constants.APPLY_TO_CHILD.equals(applyTo)
                && !Constants.APPLY_TO_ALL.equals(applyTo)) {
            throw new RuntimeException("适用对象不合法");
        }

        PointRule rule = new PointRule();
        rule.setRuleName(ruleName);
        rule.setRuleDesc(customRule.getRuleDesc());
        rule.setRuleType(record.getChangeType());
        rule.setPointValue(record.getPointValue());
        rule.setCategory(customRule.getCategory());
        rule.setApplyTo(applyTo);
        rule.setStatus(Constants.STATUS_ENABLED);
        rule.setUseCount(0);
        rule.setDeleted(0);
        if (!pointRuleService.save(rule)) {
            throw new RuntimeException("保存自定义规则失败");
        }

        record.setRuleId(rule.getId());
        record.setCustomRuleName(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelRecord(Long recordId) {
        PointRecord record = getById(recordId);
        if (record == null) {
            throw new RuntimeException("记录不存在");
        }

        // 先锁成员，再锁记录并重新读取状态，避免跨结算或重复撤销。
        FamilyMember member = familyMemberService.getByIdForUpdate(record.getMemberId());
        record = baseMapper.selectByIdForUpdate(recordId);
        if (record == null) {
            throw new RuntimeException("记录不存在");
        }
        if (record.getSettlementId() != null) {
            throw new RuntimeException("该记录已参与月度结算，不能撤销");
        }
        if (record.getStatus() == 0) {
            throw new RuntimeException("该记录已撤销");
        }

        record.setStatus(0);
        if (!updateById(record)) {
            throw new RuntimeException("撤销记录失败");
        }

        // 恢复成员积分
        if (Constants.RULE_TYPE_ADD.equals(record.getChangeType())) {
            member.setCurrentPoints(member.getCurrentPoints() - record.getPointValue());
            member.setTotalEarnedPoints(member.getTotalEarnedPoints() - record.getPointValue());
        } else {
            member.setCurrentPoints(member.getCurrentPoints() + record.getPointValue());
        }
        if (!familyMemberService.updateById(member)) {
            throw new RuntimeException("恢复成员积分失败");
        }
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

            // 自定义规则未加入规则列表时 ruleId 为空，规则名称取记录上的自定义名称
            PointRule rule = record.getRuleId() == null ? null : pointRuleService.getById(record.getRuleId());
            map.put("rule", rule);
            map.put("custom", record.getRuleId() == null);
            if (rule != null) {
                map.put("ruleName", rule.getRuleName());
            } else if (record.getCustomRuleName() != null && !record.getCustomRuleName().isEmpty()) {
                map.put("ruleName", record.getCustomRuleName());
            } else {
                map.put("ruleName", "-");
            }

            result.add(map);
        }

        return result;
    }
}
