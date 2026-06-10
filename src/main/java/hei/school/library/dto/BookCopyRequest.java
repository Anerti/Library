package hei.school.library.dto;

import hei.school.library.entity.enums.BookCopyFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BookCopyRequest {
    private Double price;
    private BookCopyFormat format;
    private UUID bookId;
    private Integer pageNumber;
}
