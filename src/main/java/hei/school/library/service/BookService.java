package hei.school.library.service;

import hei.school.library.dto.BookRequest;
import hei.school.library.dto.BookResponse;
import hei.school.library.entity.Book;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.BookMapper;
import hei.school.library.repository.dao.BookRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  public BookResponse createBook(BookRequest request) {
    Book book =
        Book.builder()
            .title(request.getTitle())
            .summary(request.getSummary())
            .isbn(request.getIsbn())
            .publisher(request.getPublisher())
            .publishedAt(request.getPublishedAt())
            .build();

    Book savedBook = bookRepository.save(book);
    return bookMapper.toResponse(savedBook);
  }

  @Transactional(readOnly = true)
  public List<BookResponse> getAllBooks() {
    return bookRepository.findAll().stream().map(bookMapper::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public BookResponse getBookById(UUID id) {
    return bookRepository.findById(id)
        .map(bookMapper::toResponse)
        .orElseThrow(() -> new NotFoundException("Book not found"));
  }

  public BookResponse updateBook(UUID id, BookRequest request) {
    Book book =
        bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));

    book.setTitle(request.getTitle());
    book.setSummary(request.getSummary());
    book.setIsbn(request.getIsbn());
    book.setPublisher(request.getPublisher());
    book.setPublishedAt(request.getPublishedAt());

    return bookMapper.toResponse(bookRepository.save(book));
  }

  public void deleteBook(UUID id) {
    if (!bookRepository.existsById(id)) {
      throw new RuntimeException("Book not found");
    }
    bookRepository.deleteById(id);
  }

  @Transactional(readOnly = true)
  public Map<String, Object> listBooks(
      String search, String isbn, UUID authorId, int page, int size) {
    Pageable pageable = PageRequest.of(page - 1, size);

    Page<Book> bookPage = bookRepository.searchBooks(search, isbn, authorId, pageable);

    List<BookResponse> dtos = bookPage.getContent().stream().map(bookMapper::toResponse).toList();

    Map<String, Object> response = new HashMap<>();
    response.put("data", dtos);
    response.put("pagination", buildPagination(bookPage, page, size));

    return response;
  }

  private Map<String, Object> buildPagination(Page<Book> page, int currentPage, int size) {
    Map<String, Object> pagination = new HashMap<>();
    pagination.put("page", currentPage);
    pagination.put("size", size);
    pagination.put("total", page.getTotalElements());
    return pagination;
  }
}
