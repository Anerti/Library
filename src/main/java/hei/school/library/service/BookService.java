package hei.school.library.service;

import hei.school.library.dto.*;
import hei.school.library.entity.Book;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.BookMapper;
import hei.school.library.mapper.PaginationMapper;
import hei.school.library.repository.dao.BookRepository;
import hei.school.library.service.client.OpenLibraryClient;
import hei.school.library.validator.BookValidator;
import hei.school.library.validator.DataValidator;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {
  private final BookRepository bookRepository;
  private final BookMapper bookMapper;
  private final DataValidator dataValidator;
  private final BookValidator bookValidator;
  private final PaginationMapper paginationMapper;
  private static final String UNIQUE_CONSTRAINT_VIOLATION = "23505";
  private final OpenLibraryClient openLibraryClient;

  @Transactional
  public BookResponse createBook(BookRequest request) {
    bookValidator.validateCreation(request);

    return bookRepository
        .create(
            request.getTitle(),
            request.getSummary(),
            request.getIsbn(),
            request.getPublisher(),
            request.getPublishedAt())
        .map(bookMapper::toResponse)
        .orElseThrow(
            () ->
                new ConflictException(
                    String.format("Book's ISBN %s already exists.", request.getIsbn())));
  }

  @Transactional(readOnly = true)
  public BookResponse getBookById(UUID id) {
    return bookRepository
        .findById(id)
        .map(bookMapper::toResponse)
        .orElseThrow(() -> new NotFoundException(String.format("Book %s not found", id)));
  }

  @Transactional
  public BookResponse updateBook(UUID id, BookUpdateRequest request) {
    bookValidator.validateUpdate(request);
    try {
      return bookMapper.toResponse(
          bookRepository
              .updateById(
                  id,
                  request.getTitle(),
                  request.getSummary(),
                  request.getIsbn(),
                  request.getPublisher(),
                  request.getPublishedAt())
              .orElseThrow(() -> new NotFoundException(String.format("Book %s not found", id))));
    } catch (DataIntegrityViolationException e) {
      if (uniqueViolation(e)) {
        throw new ConflictException(String.format("ISBN %s already exists.", request.getIsbn()));
      }
      throw e;
    }
  }

  private static boolean uniqueViolation(DataIntegrityViolationException e) {
    return e.getRootCause() instanceof SQLException sqlEx
        && UNIQUE_CONSTRAINT_VIOLATION.equals(sqlEx.getSQLState());
  }

  @Transactional
  public void deleteBook(UUID id) {
    bookRepository
        .deleteByIdAndReturn(id)
        .orElseThrow(() -> new NotFoundException(String.format("Book %s not found", id)));
  }

  @Transactional(readOnly = true)
  public PageResponse<BookResponse> listBooks(
      String title,
      String publisher,
      String isbn,
      String authorLastName,
      String genre,
      int page,
      int size) {
    bookValidator.validateFetch(title, publisher, isbn, authorLastName, genre);

    Pageable pageable = PageRequest.of(page - 1, size);

    Page<Book> bookPage =
        bookRepository.searchBooks(title, publisher, isbn, authorLastName, genre, pageable);

    List<BookResponse> books = bookPage.getContent().stream().map(bookMapper::toResponse).toList();

    PaginationDto pagination = paginationMapper.toPaginationDto(bookPage, page, size);

    return PageResponse.<BookResponse>builder()
        .data(books.isEmpty() ? null : books)
        .pagination(pagination)
        .build();
  }

  public VerifyBookResponse verifyBook(UUID bookId) {
    Book book =
        bookRepository.findById(bookId).orElseThrow(() -> new NotFoundException("Book not found"));

    return openLibraryClient.verify(book.getIsbn());
  }
}
