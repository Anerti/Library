package hei.school.library.mapper;

import hei.school.library.dto.BookCopyResponse;
import hei.school.library.entity.BookCopy;
import org.springframework.stereotype.Component;

@Component
public class BookCopyMapper {
  public BookCopyResponse toResponse(BookCopy bookCopy) {
    return BookCopyResponse.builder()
            .id(bookCopy.getId())
            .price(bookCopy.getPrice())
            .format(bookCopy.getFormat())
            .libraryId(bookCopy.getLibrary().getId())
            .bookId(bookCopy.getBook().getId())
            .status(bookCopy.getStatus())
            .pageNumber(bookCopy.getPageNumber())
            .updatedAt(bookCopy.getUpdatedAt())
            .build();
  }
}
