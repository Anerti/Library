package hei.school.library.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenreResponse {
    private UUID id;
    private String name;
}
