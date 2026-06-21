package com.family.points.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 奖品实体类
 */
@Data
@TableName("reward_item")
public class RewardItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String itemName;

    private String itemDesc;

    private String imagePath;

    private Integer requiredPoints;

    private Integer stock;

    private String category;

    private Integer status;

    private Integer exchangeCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
