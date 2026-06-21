package com.family.points.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 积分规则实体类
 */
@Data
@TableName("point_rule")
public class PointRule {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String ruleName;

    private String ruleDesc;

    private String ruleType;

    private Integer pointValue;

    private String category;

    private String applyTo;

    private Integer status;

    private Integer useCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
