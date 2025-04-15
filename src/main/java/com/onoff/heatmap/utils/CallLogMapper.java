package com.onoff.heatmap.utils;

import com.onoff.heatmap.config.MapStructConfig;
import com.onoff.heatmap.models.CallLog;
import com.onoff.heatmap.models.CallLogDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface CallLogMapper {

    CallLogMapper INSTANCE = Mappers.getMapper(CallLogMapper.class);

    CallLogDto toDto(CallLog callLog);

    CallLog toEntity(CallLogDto callLogDto);

    List<CallLogDto> toDtoList(List<CallLog> callLogs);

    List<CallLog> toEntityList(List<CallLogDto> callLogDtos);
}
