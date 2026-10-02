package com.family.points.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 月度结算明细，保存结算时的姓名和积分快照
 */
@Data
@TableName("settlement_detail")
public class SettlementDetail {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long settlementId;

    private Long memberId;

    private String memberName;

    private Integer rankNo;

    private Integer originalPoints;

    private BigDecimal settledPoints;
}
