package com.family.points.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.family.points.common.Constants;
import com.family.points.entity.FamilyMember;
import com.family.points.entity.RewardItem;
import com.family.points.mapper.RewardItemMapper;
import com.family.points.service.FamilyMemberService;
import com.family.points.service.RewardItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 奖品服务实现类
 */
@Service
public class RewardItemServiceImpl extends ServiceImpl<RewardItemMapper, RewardItem> implements RewardItemService {

    @Autowired
    private FamilyMemberService familyMemberService;

    @Override
    public List<RewardItem> listOnShelf() {
        LambdaQueryWrapper<RewardItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RewardItem::getStatus, Constants.STATUS_ENABLED)
                .orderByDesc(RewardItem::getCreateTime);
        return list(queryWrapper);
    }

    @Override
    public List<RewardItem> listAvailableForMember(Long memberId) {
        FamilyMember member = familyMemberService.getById(memberId);
        if (member == null) {
            throw new RuntimeException("成员不存在");
        }

        Integer currentPoints = member.getCurrentPoints();
        List<RewardItem> allItems = listOnShelf();

        return allItems.stream()
                .filter(item -> item.getStock() > 0 && item.getRequiredPoints() <= currentPoints)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void decreaseStock(Long itemId, Integer quantity) {
        RewardItem item = getById(itemId);
        if (item == null) {
            throw new RuntimeException("奖品不存在");
        }

        if (item.getStock() < quantity) {
            throw new RuntimeException("库存不足");
        }

        item.setStock(item.getStock() - quantity);
        updateById(item);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void increaseExchangeCount(Long itemId, Integer quantity) {
        RewardItem item = getById(itemId);
        if (item != null) {
            item.setExchangeCount(item.getExchangeCount() + quantity);
            updateById(item);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restoreStock(Long itemId, Integer quantity) {
        RewardItem item = getById(itemId);
        if (item != null) {
            item.setStock(item.getStock() + quantity);
            item.setExchangeCount(item.getExchangeCount() - quantity);
            updateById(item);
        }
    }
}
