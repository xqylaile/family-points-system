package com.family.points.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.family.points.entity.FamilyMember;
import org.apache.ibatis.annotations.Mapper;

/**
 * 家庭成员 Mapper 接口
 */
@Mapper
public interface FamilyMemberMapper extends BaseMapper<FamilyMember> {
}
