package com.family.points.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.family.points.entity.RewardItem;
import java.util.List;

/**
 * 奖品服务接口
 */
public interface RewardItemService extends IService<RewardItem> {

    /**
     * 获取所有上架的奖品
     */
    List<RewardItem> listOnShelf();

    /**
     * 获取成员可兑换的奖品（积分足够且有库存）
     */
    List<RewardItem> listAvailableForMember(Long memberId);

    /**
     * 减少库存
     */
    void decreaseStock(Long itemId, Integer quantity);

    /**
     * 增加兑换次数
     */
    void increaseExchangeCount(Long itemId, Integer quantity);

    /**
     * 恢复库存（撤销兑换时）
     */
    void restoreStock(Long itemId, Integer quantity);
}
