package com.family.points.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 月度结算批次
 */
@Data
@TableName("monthly_settlement")
public class MonthlySettlement {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String settlementMonth;

    private LocalDateTime createTime;

    @TableField(exist = false)
    private List<SettlementDetail> details;
}
