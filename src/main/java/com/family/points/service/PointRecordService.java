package com.family.points.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.family.points.entity.PointRecord;
import java.util.List;
import java.util.Map;

/**
 * 积分变更记录服务接口
 */
public interface PointRecordService extends IService<PointRecord> {

    /**
     * 添加积分变更记录
     */
    void addPointRecord(PointRecord record);

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
