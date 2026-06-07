package hei.school.library.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookResponse {
    private UUID id;
    private String title;
    private String abstractText;
    private String isbn;
    private String publisher;
    private LocalDate publishedAt;
    private LocalDateTime createdAt;
}
