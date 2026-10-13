package com.family.points.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.family.points.entity.PointRecord;
import com.family.points.entity.PointRule;
import java.util.List;
import java.util.Map;

/**
 * 积分变更记录服务接口
 */
public interface PointRecordService extends IService<PointRecord> {

    /**
     * 添加积分变更记录
     *
     * @param record     积分变更记录，选择已有规则时需携带 ruleId
     * @param customRule 自定义规则（名称/描述/分类/适用对象），为空表示使用已有规则
     * @param saveAsRule 自定义规则是否加入规则列表，加入后可在以后的变更中直接选择
     */
    void addPointRecord(PointRecord record, PointRule customRule, boolean saveAsRule);

    /**
     * 撤销积分记录
     */
    void cancelRecord(Long recordId);

    /**
     * 根据条件查询记录
     */
    List<PointRecord> listByCondition(Long memberId, Long ruleId, String changeType, String startDate, String endDate);

    /**
     * 获取记录详情（包含成员和规则信息）
     */
    List<Map<String, Object>> listRecordsWithDetails(Long memberId, String startDate, String endDate);
}
