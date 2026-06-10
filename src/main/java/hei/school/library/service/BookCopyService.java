package hei.school.library.service;

import hei.school.library.dto.BookCopyRequest;
import hei.school.library.dto.BookCopyResponse;
import hei.school.library.dto.PageResponse;
import hei.school.library.dto.PaginationDto;
import hei.school.library.entity.BookCopy;
import hei.school.library.entity.enums.BookCopyFormat;
import hei.school.library.entity.enums.BookCopyStatus;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.BookCopyMapper;
import hei.school.library.mapper.PaginationMapper;
import hei.school.library.repository.dao.BookCopyRepository;
import hei.school.library.repository.dao.BookRepository;
import hei.school.library.repository.dao.LibraryRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookCopyService {
  private final BookCopyRepository bookCopyRepository;
  private final BookRepository bookRepository;
  private final LibraryRepository libraryRepository;
  private final BookCopyMapper bookCopyMapper;
  private final PaginationMapper paginationMapper;

  public PageResponse<BookCopyResponse> findByFilters(
      UUID libraryId,
      BookCopyStatus status,
      BookCopyFormat format,
      UUID bookId,
      int page,
      int size) {

    if (!libraryRepository.existsById(libraryId)) {
      throw new NotFoundException("Library with id " + libraryId + " not found");
    }

    Pageable pageable = PageRequest.of(page - 1, size);
    Page<BookCopy> bookCopyPage =
        bookCopyRepository.findByFilters(libraryId, status, format, bookId, pageable);
    List<BookCopyResponse> bookCopyResponses =
        bookCopyPage.getContent().stream().map(bookCopyMapper::toResponse).toList();

    PaginationDto pagination = paginationMapper.toPaginationDto(bookCopyPage, page, size);

    return PageResponse.<BookCopyResponse>builder()
        .data(bookCopyResponses)
        .pagination(pagination)
        .build();
  }

  public BookCopyResponse findById(UUID libraryId, UUID copyId) {
    if (!libraryRepository.existsById(libraryId)) {
      throw new NotFoundException("Library with id " + libraryId + " not found");
    }
    return bookCopyRepository
        .findById(copyId)
        .map(bookCopyMapper::toResponse)
        .orElseThrow(() -> new NotFoundException("BookCopy with id " + copyId + " not found"));
  }

  public BookCopyResponse create(UUID libraryId, BookCopyRequest request) {
    if (!libraryRepository.existsById(libraryId)) {
      throw new NotFoundException("Library with id " + libraryId + " not found");
    }
    if (!bookRepository.existsById(request.getBookId())) {
      throw new NotFoundException("Book with id " + request.getBookId() + " not found");
    }

    return bookCopyRepository
        .create(
            request.getPrice(),
            request.getFormat().name(),
            libraryId,
            request.getBookId(),
            request.getPageNumber())
        .map(bookCopyMapper::toResponse)
        .orElseThrow(() -> new NotFoundException("Failed to create BookCopy"));
  }
}
