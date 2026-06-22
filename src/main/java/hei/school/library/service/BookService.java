package hei.school.library.service;

import hei.school.library.dto.BookRequest;
import hei.school.library.dto.BookResponse;
import hei.school.library.dto.BookUpdateRequest;
import hei.school.library.dto.PageResponse;
import hei.school.library.dto.PaginationDto;
import hei.school.library.entity.Book;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.BookMapper;
import hei.school.library.mapper.PaginationMapper;
import hei.school.library.repository.dao.BookRepository;
import hei.school.library.validator.DataValidator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
  private final PaginationMapper paginationMapper;

  public BookResponse createBook(BookRequest request) {
    dataValidator.validateBook(request);

    return bookRepository
        .create(
            request.getTitle(),
            request.getSummary(),
            request.getIsbn(),
            request.getPublisher(),
            request.getPublishedAt())
        .map(bookMapper::toResponse)
        .orElseThrow(
            () -> new ConflictException("Book with ISBN " + request.getIsbn() + " already exists"));
  }

  @Transactional(readOnly = true)
  public BookResponse getBookById(UUID id) {
    return bookRepository
        .findById(id)
        .map(bookMapper::toResponse)
        .orElseThrow(() -> new NotFoundException("Book not found"));
  }

  public BookResponse updateBook(UUID id, BookUpdateRequest request) {
    Book book =
        bookRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Book " + id + " not found"));

    dataValidator.validatePatchBook(request, book);
    return bookMapper.toResponse(bookRepository.save(book));
  }

  public void deleteBook(UUID id) {
    bookRepository
        .deleteByIdAndReturn(id)
        .orElseThrow(() -> new NotFoundException(String.format("Book %s not found", id)));
  }

  @Transactional(readOnly = true)
  public PageResponse<BookResponse> listBooks(
      String search, String isbn, String authorLastName, String genreName, int page, int size) {
    if (search != null) {
      dataValidator.validateString("search", search);
    }

    Pageable pageable = PageRequest.of(page - 1, size);

    Page<Book> bookPage =
        bookRepository.searchBooks(search, isbn, authorLastName, genreName, pageable);

    List<BookResponse> books = bookPage.getContent().stream().map(bookMapper::toResponse).toList();

    PaginationDto pagination = paginationMapper.toPaginationDto(bookPage, page, size);

    return PageResponse.<BookResponse>builder().data(books).pagination(pagination).build();
  }
}
