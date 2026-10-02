package com.family.points.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.family.points.entity.ExchangeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 兑换记录 Mapper 接口
 */
@Mapper
public interface ExchangeRecordMapper extends BaseMapper<ExchangeRecord> {

    @Select("SELECT * FROM exchange_record WHERE id = #{id} AND deleted = 0 FOR UPDATE")
    ExchangeRecord selectByIdForUpdate(@Param("id") Long id);

    @Update("UPDATE exchange_record SET settlement_id = #{settlementId} " +
            "WHERE member_id = #{memberId} AND deleted = 0 AND settlement_id IS NULL")
    int markSettled(@Param("memberId") Long memberId, @Param("settlementId") Long settlementId);
}
