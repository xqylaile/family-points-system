package com.family.points.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 家庭成员实体类
 */
@Data
@TableName("family_member")
public class FamilyMember {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String role;

    private Integer age;

    private String avatar;

    private Integer currentPoints;

    @TableField(exist = false)
    private Integer rankNo;

    private Integer totalEarnedPoints;

    private Integer totalSpentPoints;

    private String memberType;

    private Integer status;

    private LocalDateTime joinDate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
