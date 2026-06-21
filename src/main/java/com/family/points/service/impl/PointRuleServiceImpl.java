package com.family.points.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.points.common.Constants;
import com.family.points.entity.PointRule;
import com.family.points.mapper.PointRuleMapper;
import com.family.points.service.PointRuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 积分规则服务实现类
 */
@Service
public class PointRuleServiceImpl extends ServiceImpl<PointRuleMapper, PointRule> implements PointRuleService {

    @Override
    public List<PointRule> listEnabled() {
        LambdaQueryWrapper<PointRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PointRule::getStatus, Constants.STATUS_ENABLED)
                .orderByDesc(PointRule::getCreateTime);
        return list(queryWrapper);
    }

    @Override
    public List<PointRule> listEnabledByType(String ruleType) {
        LambdaQueryWrapper<PointRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PointRule::getStatus, Constants.STATUS_ENABLED)
                .eq(PointRule::getRuleType, ruleType)
                .orderByDesc(PointRule::getCreateTime);
        return list(queryWrapper);
    }

    @Override
    public List<PointRule> listByMemberType(String memberType, String ruleType) {
        LambdaQueryWrapper<PointRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PointRule::getStatus, Constants.STATUS_ENABLED)
                .eq(PointRule::getRuleType, ruleType)
                .and(wrapper -> wrapper
                        .eq(PointRule::getApplyTo, memberType)
                        .or()
                        .eq(PointRule::getApplyTo, Constants.APPLY_TO_ALL))
                .orderByDesc(PointRule::getCreateTime);
        return list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void increaseUseCount(Long ruleId) {
        PointRule rule = getById(ruleId);
        if (rule != null) {
            rule.setUseCount(rule.getUseCount() + 1);
            updateById(rule);
        }
    }
}
