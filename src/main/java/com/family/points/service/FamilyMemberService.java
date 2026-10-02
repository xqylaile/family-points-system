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
     * 在现有事务内锁定成员，所有积分写入必须先取得该锁
     */
    FamilyMember getByIdForUpdate(Long memberId);

    /**
     * 保存成员资料，不接受客户端修改积分字段
     */
    void saveProfile(FamilyMember member);

    void updateStatus(Long memberId, Integer status);

    /**
     * 更新成员积分
     */
    void updatePoints(Long memberId, Integer pointChange, String changeType);
}
