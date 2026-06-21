package com.family.points.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.family.points.entity.PointRule;
import java.util.List;

/**
 * 积分规则服务接口
 */
public interface PointRuleService extends IService<PointRule> {

    /**
     * 获取所有启用的规则
     */
    List<PointRule> listEnabled();

    /**
     * 根据类型获取启用的规则
     */
    List<PointRule> listEnabledByType(String ruleType);

    /**
     * 根据成员类型获取适用的规则
     */
    List<PointRule> listByMemberType(String memberType, String ruleType);

    /**
     * 增加规则使用次数
     */
    void increaseUseCount(Long ruleId);
}
