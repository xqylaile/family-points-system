package com.family.points.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.family.points.entity.FamilyMember;
import java.util.List;

/**
 * 家庭成员服务接口
 */
public interface FamilyMemberService extends IService<FamilyMember> {

    /**
     * 获取所有启用的成员
     */
    List<FamilyMember> listEnabled();

    /**
     * 获取积分排行榜
     */
    List<FamilyMember> getRanking();

    /**
     * 更新成员积分
     */
    void updatePoints(Long memberId, Integer pointChange, String changeType);
}
