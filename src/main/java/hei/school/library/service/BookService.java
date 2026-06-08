package hei.school.library.service;

import hei.school.library.dto.BookRequest;
import hei.school.library.dto.BookResponse;
import hei.school.library.entity.Book;
import hei.school.library.repository.dao.BookRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {
  private final BookRepository bookRepository;

  public BookResponse createBook(BookRequest request) {
    Book book =
        Book.builder()
            .title(request.getTitle())
            .abstractText(request.getAbstractText())
            .isbn(request.getIsbn())
            .publisher(request.getPublisher())
            .publishedAt(request.getPublishedAt())
            .build();

    Book savedBook = bookRepository.save(book);
    return mapToResponse(savedBook);
  }

  @Transactional(readOnly = true)
  public List<BookResponse> getAllBooks() {
    return bookRepository.findAll().stream().map(this::mapToResponse).toList();
  }

  public BookResponse getBookById(UUID id) {
    Book book =
        bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
    return mapToResponse(book);
  }

  public BookResponse updateBook(UUID id, BookRequest request) {
    Book book =
        bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));

    book.setTitle(request.getTitle());
    book.setAbstractText(request.getAbstractText());
    book.setIsbn(request.getIsbn());
    book.setPublisher(request.getPublisher());
    book.setPublishedAt(request.getPublishedAt());

    return mapToResponse(bookRepository.save(book));
  }

  public void deleteBook(UUID id) {
    if (!bookRepository.existsById(id)) {
      throw new RuntimeException("Book not found");
    }
    bookRepository.deleteById(id);
  }

  private BookResponse mapToResponse(Book book) {
    return BookResponse.builder()
        .id(book.getId())
        .title(book.getTitle())
        .abstractText(book.getAbstractText())
        .isbn(book.getIsbn())
        .publisher(book.getPublisher())
        .publishedAt(book.getPublishedAt())
        .createdAt(book.getCreatedAt())
        // .authors(...) à mapper plus tard
        // .genres(...) à mapper plus tard
        .build();
  }
}
