package com.family.points.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.points.common.Constants;
import com.family.points.entity.FamilyMember;
import com.family.points.mapper.FamilyMemberMapper;
import com.family.points.service.FamilyMemberService;
import com.family.points.util.PointRankingUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
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
        return PointRankingUtil.rank(listEnabled());
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public FamilyMember getByIdForUpdate(Long memberId) {
        FamilyMember member = baseMapper.selectByIdForUpdate(memberId);
        if (member == null) {
            throw new RuntimeException("成员不存在");
        }
        if (member.getCurrentPoints() == null) {
            member.setCurrentPoints(0);
        }
        if (member.getTotalEarnedPoints() == null) {
            member.setTotalEarnedPoints(0);
        }
        if (member.getTotalSpentPoints() == null) {
            member.setTotalSpentPoints(0);
        }
        return member;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveProfile(FamilyMember input) {
        FamilyMember member;
        if (input.getId() == null) {
            member = new FamilyMember();
            member.setCurrentPoints(0);
            member.setTotalEarnedPoints(0);
            member.setTotalSpentPoints(0);
            member.setJoinDate(LocalDateTime.now());
            member.setStatus(Constants.STATUS_ENABLED);
        } else {
            member = getByIdForUpdate(input.getId());
        }
        member.setName(input.getName());
        member.setRole(input.getRole());
        member.setAge(input.getAge());
        member.setMemberType(input.getMemberType());
        if (input.getAvatar() != null) {
            member.setAvatar(input.getAvatar());
        }
        if (!saveOrUpdate(member)) {
            throw new RuntimeException("保存成员失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long memberId, Integer status) {
        if (!Integer.valueOf(0).equals(status) && !Integer.valueOf(1).equals(status)) {
            throw new RuntimeException("成员状态无效");
        }
        if (!update(new LambdaUpdateWrapper<FamilyMember>()
                .eq(FamilyMember::getId, memberId).set(FamilyMember::getStatus, status))) {
            throw new RuntimeException("成员不存在");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePoints(Long memberId, Integer pointChange, String changeType) {
        FamilyMember member = getByIdForUpdate(memberId);
        Integer newPoints;
        if (Constants.RULE_TYPE_ADD.equals(changeType)) {
            newPoints = member.getCurrentPoints() + pointChange;
            member.setTotalEarnedPoints(member.getTotalEarnedPoints() + pointChange);
        } else {
            newPoints = member.getCurrentPoints() - pointChange;
        }
        member.setCurrentPoints(newPoints);
        if (!updateById(member)) {
            throw new RuntimeException("更新成员积分失败");
        }
    }
}
