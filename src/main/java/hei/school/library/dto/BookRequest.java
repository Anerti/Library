package hei.school.library.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequest {

    private String title;
    private String abstractText;
    private String isbn;
    private String publisher;
    private LocalDate publishedAt;
}
