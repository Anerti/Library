package hei.school.library.mapper;

import hei.school.library.dto.ArrivalResponse;
import hei.school.library.entity.Arrival;

public class ArrivalMapper {
  public ArrivalResponse toResponse(Arrival arrival) {
    return ArrivalResponse.builder()
        .id(arrival.getId())
        .createdAt(arrival.getCreatedAt())
        .arrivalDate(arrival.getArrivalDate())
        .build();
  }
}
