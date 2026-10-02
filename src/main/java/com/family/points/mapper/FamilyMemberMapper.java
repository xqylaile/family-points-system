package com.family.points.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.family.points.entity.FamilyMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 家庭成员 Mapper 接口
 */
@Mapper
public interface FamilyMemberMapper extends BaseMapper<FamilyMember> {

    @Select("SELECT * FROM family_member WHERE id = #{id} AND deleted = 0 FOR UPDATE")
    FamilyMember selectByIdForUpdate(@Param("id") Long id);

    @Select("SELECT * FROM family_member WHERE deleted = 0 ORDER BY id FOR UPDATE")
    List<FamilyMember> selectAllForUpdate();
}
