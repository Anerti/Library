package hei.school.library.service.client;

import hei.school.library.dto.VerifyBookResponse;
import hei.school.library.dto.client.OpenLibraryAuthor;
import hei.school.library.dto.client.OpenLibraryBookResponse;
import hei.school.library.dto.client.OpenLibrarySubject;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class OpenLibraryClient {

  private final RestClient restClient;

  public VerifyBookResponse verify(String isbn) {

    String bibKey = "ISBN:" + isbn;

    Map<String, OpenLibraryBookResponse> response =
        restClient
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .scheme("https")
                        .host("openlibrary.org")
                        .path("/api/books")
                        .queryParam("bibkeys", bibKey)
                        .queryParam("format", "json")
                        .queryParam("jscmd", "data")
                        .build())
            .retrieve()
            .body(new ParameterizedTypeReference<>() {});

    OpenLibraryBookResponse book = response.get(bibKey);

    if (book == null) {
      return VerifyBookResponse.builder().exists(false).isbn(isbn).build();
    }

    return VerifyBookResponse.builder()
        .exists(true)
        .isbn(isbn)
        .title(book.getTitle())
        .publisher(
            book.getPublishers().isEmpty() ? null : book.getPublishers().getFirst().getName())
        .publishDate(book.getPublishDate())
        .numberOfPages(book.getNumberOfPages())
        .authors(book.getAuthors().stream().map(OpenLibraryAuthor::getName).toList())
        .subjects(book.getSubjects().stream().map(OpenLibrarySubject::getName).toList())
        .cover(book.getCover())
        .build();
  }
}
