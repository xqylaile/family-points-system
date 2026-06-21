package com.family.points.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.family.points.entity.ExchangeRecord;
import java.util.List;
import java.util.Map;

/**
 * 兑换记录服务接口
 */
public interface ExchangeRecordService extends IService<ExchangeRecord> {

    /**
     * 兑换奖品
     */
    void exchangeReward(Long memberId, Long itemId, Integer quantity);

    /**
     * 撤销兑换
     */
    void cancelExchange(Long recordId);

    /**
     * 更新兑换状态
     */
    void updateExchangeStatus(Long recordId, String status);

    /**
     * 获取兑换记录详情（包含成员和奖品信息）
     */
    List<Map<String, Object>> listRecordsWithDetails(Long memberId, String startDate, String endDate);

    /**
     * 根据条件查询记录
     */
    List<ExchangeRecord> listByCondition(Long memberId, String startDate, String endDate);
}
