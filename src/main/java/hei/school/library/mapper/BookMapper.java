package hei.school.library.mapper;

import hei.school.library.dto.AuthorResponse;
import hei.school.library.dto.BookResponse;
import hei.school.library.dto.GenreSummary;
import hei.school.library.entity.Author;
import hei.school.library.entity.Book;
import hei.school.library.entity.Genre;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

  public BookResponse toResponse(Book book) {
    return BookResponse.builder()
        .id(book.getId())
        .title(book.getTitle())
        .summary(book.getSummary())
        .isbn(book.getIsbn())
        .publisher(book.getPublisher())
        .publishedAt(book.getPublishedAt())
        .createdAt(book.getCreatedAt())
        .authors(mapAuthors(book))
        .genres(mapGenres(book))
        .build();
  }

  private List<AuthorResponse> mapAuthors(Book book) {
    return Optional.ofNullable(book.getAuthors())
        .orElse(Collections.emptySet())
        .stream()
        .map(this::toAuthorResponse)
        .toList();
  }

  private AuthorResponse toAuthorResponse(Author author) {
    return AuthorResponse.builder()
        .id(author.getId())
        .firstName(author.getFirstName())
        .lastName(author.getLastName())
        .build();
  }

  private List<GenreSummary> mapGenres(Book book) {
    return Optional.ofNullable(book.getGenres())
        .orElse(Collections.emptySet())
        .stream()
        .map(this::toGenreSummary)
        .toList();
  }

  private GenreSummary toGenreSummary(Genre genre) {
    return GenreSummary.builder()
        .id(genre.getId())
        .name(genre.getName())
        .build();
  }
}
