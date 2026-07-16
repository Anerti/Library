package hei.school.library.service.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import hei.school.library.dto.VerifyBookResponse;
import hei.school.library.entity.Book;
import hei.school.library.exception.NotFoundException;
import hei.school.library.repository.dao.BookRepository;
import hei.school.library.service.BookService;
import hei.school.library.service.client.OpenLibraryClient;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

  @Mock private BookRepository bookRepository;

  @Mock private OpenLibraryClient openLibraryClient;

  @InjectMocks private BookService subject;

  @Test
  void should_verify_book() {
    UUID bookId = UUID.randomUUID();

    Book book = Book.builder().id(bookId).isbn("9780140328721").build();

    VerifyBookResponse expected =
        VerifyBookResponse.builder()
            .exists(true)
            .isbn("9780140328721")
            .title("Fantastic Mr. Fox")
            .build();

    when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

    when(openLibraryClient.verify("9780140328721")).thenReturn(expected);

    VerifyBookResponse actual = subject.verifyBook(bookId);

    assertEquals(expected, actual);

    verify(bookRepository).findById(bookId);
    verify(openLibraryClient).verify("9780140328721");
  }

  @Test
  void should_throw_when_book_not_found() {

    UUID id = UUID.randomUUID();

    when(bookRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> subject.verifyBook(id));

    verify(bookRepository).findById(id);
    verifyNoInteractions(openLibraryClient);
  }
}
