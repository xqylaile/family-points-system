package com.family.points.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 兑换记录实体类
 */
@Data
@TableName("exchange_record")
public class ExchangeRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long memberId;

    private Long itemId;

    private String itemName;

    private Integer quantity;

    private Integer totalPoints;

    private String exchangeStatus;

    private String remark;

    private Long settlementId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
