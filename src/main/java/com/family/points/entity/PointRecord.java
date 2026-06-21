package com.family.points.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 积分变更记录实体类
 */
@Data
@TableName("point_record")
public class PointRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long memberId;

    private Long ruleId;

    private String changeType;

    private Integer pointValue;

    private Integer beforePoints;

    private Integer afterPoints;

    private String remark;

    private String imagePath;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
