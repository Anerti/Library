package hei.school.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArrivalResponse {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime arrivalDate;
    private UUID libraryId;
}
