package com.family.points.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.family.points.entity.PointRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 积分变更记录 Mapper 接口
 */
@Mapper
public interface PointRecordMapper extends BaseMapper<PointRecord> {
}
