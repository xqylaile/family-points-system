package com.family.points.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.family.points.entity.PointRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 积分变更记录 Mapper 接口
 */
@Mapper
public interface PointRecordMapper extends BaseMapper<PointRecord> {

    @Select("SELECT * FROM point_record WHERE id = #{id} AND deleted = 0 FOR UPDATE")
    PointRecord selectByIdForUpdate(@Param("id") Long id);

    @Update("UPDATE point_record SET settlement_id = #{settlementId} " +
            "WHERE member_id = #{memberId} AND status = 1 AND deleted = 0 AND settlement_id IS NULL")
    int markSettled(@Param("memberId") Long memberId, @Param("settlementId") Long settlementId);
}
