package com.family.points.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.points.common.Constants;
import com.family.points.entity.FamilyMember;
import com.family.points.mapper.FamilyMemberMapper;
import com.family.points.service.FamilyMemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 家庭成员服务实现类
 */
@Service
public class FamilyMemberServiceImpl extends ServiceImpl<FamilyMemberMapper, FamilyMember> implements FamilyMemberService {

    @Override
    public List<FamilyMember> listEnabled() {
        LambdaQueryWrapper<FamilyMember> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FamilyMember::getStatus, Constants.STATUS_ENABLED)
                .orderByAsc(FamilyMember::getCreateTime);
        return list(queryWrapper);
    }

    @Override
    public List<FamilyMember> getRanking() {
        LambdaQueryWrapper<FamilyMember> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FamilyMember::getStatus, Constants.STATUS_ENABLED)
                .orderByDesc(FamilyMember::getCurrentPoints);
        return list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePoints(Long memberId, Integer pointChange, String changeType) {
        FamilyMember member = getById(memberId);
        if (member == null) {
            throw new RuntimeException("成员不存在");
        }

        Integer currentPoints = member.getCurrentPoints();
        Integer newPoints;

        if (Constants.RULE_TYPE_ADD.equals(changeType)) {
            newPoints = currentPoints + pointChange;
            member.setTotalEarnedPoints(member.getTotalEarnedPoints() + pointChange);
        } else {
            newPoints = currentPoints - pointChange;
            if (newPoints < 0) {
                throw new RuntimeException("积分不足，扣分失败");
            }
        }

        member.setCurrentPoints(newPoints);
        updateById(member);
    }
}
