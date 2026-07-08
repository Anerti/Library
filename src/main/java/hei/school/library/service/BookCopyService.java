package hei.school.library.service;

import hei.school.library.dto.*;
import hei.school.library.entity.BookCopy;
import hei.school.library.entity.enums.BookCopyFormat;
import hei.school.library.entity.enums.BookCopyStatus;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.BookCopyMapper;
import hei.school.library.mapper.PaginationMapper;
import hei.school.library.repository.dao.BookCopyRepository;
import hei.school.library.repository.dao.BookRepository;
import hei.school.library.repository.dao.LibraryRepository;
import hei.school.library.validator.BookCopyValidator;
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
  private final BookCopyValidator bookCopyValidator;
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
        bookCopyRepository.findByFilters(
            libraryId,
            status != null ? status.name() : null,
            format != null ? format.name() : null,
            bookId != null ? bookId.toString() : null,
            pageable);
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
    bookCopyValidator.validateCreate(request);

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

  public BookCopyResponse update(UUID libraryId, UUID copyId, BookCopyUpdateRequest request) {
    bookCopyValidator.validateUpdate(request);

    BookCopy bookCopy =
        bookCopyRepository
            .findById(copyId)
            .orElseThrow(() -> new NotFoundException("BookCopy with id " + copyId + " not found"));

    if (request.getPrice() != null) bookCopy.setPrice(request.getPrice());
    if (request.getFormat() != null) bookCopy.setFormat(request.getFormat());
    if (request.getStatus() != null) bookCopy.setStatus(request.getStatus());
    if (request.getPageNumber() != null) bookCopy.setPageNumber(request.getPageNumber());

    return bookCopyMapper.toResponse(bookCopyRepository.save(bookCopy));
  }

  public void delete(UUID libraryId, UUID copyId) {
    if (!libraryRepository.existsById(libraryId)) {
      throw new NotFoundException("Library with id " + libraryId + " not found");
    }
    if (!bookCopyRepository.existsById(copyId)) {
      throw new NotFoundException("BookCopy with id " + copyId + " not found");
    }
    bookCopyRepository.deleteById(copyId);
  }
}
