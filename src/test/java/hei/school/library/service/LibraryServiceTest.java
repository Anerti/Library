package hei.school.library.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import hei.school.library.dto.LibraryResponse;
import hei.school.library.entity.Library;
import hei.school.library.exception.NotFoundException;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.repository.dao.LibraryRepository;
import hei.school.library.validator.DataValidator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {

  @Mock private LibraryRepository repository;

  private DataValidator dataValidator;
  private LibraryService service;

  @BeforeEach
  void setUp() {
    dataValidator = new DataValidator();
    service = new LibraryService(repository, dataValidator);
  }

  @Test
  void should_return_all_libraries_when_search_is_null() {
    Library lib = aLibrary("Lib A", "a@mail.com", "123 Street");
    Page<Library> page = new PageImpl<>(List.of(lib));

    when(repository.searchLibraries(null, PageRequest.of(0, 20)))
        .thenReturn(Optional.of(page));

    Map<String, Object> result = service.listLibraries(null, 1, 20);

    assertSuccess(result, 1, 20, 1);
    LibraryResponse dto = ((List<LibraryResponse>) result.get("data")).get(0);
    assertEquals("Lib A", dto.getName());
    assertEquals("a@mail.com", dto.getEmail());
  }

  @Test
  void should_return_all_libraries_when_search_is_empty() {
    Library lib = aLibrary("Lib A", "a@mail.com", "123 Street");
    Page<Library> page = new PageImpl<>(List.of(lib));

    when(repository.searchLibraries("", PageRequest.of(0, 20)))
        .thenReturn(Optional.of(page));

    Map<String, Object> result = service.listLibraries("", 1, 20);

    assertSuccess(result, 1, 20, 1);
  }

  @Test
  void should_return_filtered_libraries_when_search_is_valid() {
    Library lib = aLibrary("Tech Library", "tech@mail.com", "456 Avenue");
    Page<Library> page = new PageImpl<>(List.of(lib));

    when(repository.searchLibraries("tech", PageRequest.of(0, 20)))
        .thenReturn(Optional.of(page));

    Map<String, Object> result = service.listLibraries("tech", 1, 20);

    assertSuccess(result, 1, 20, 1);
  }

  @Test
  void should_allow_special_characters_in_search() {
    Library lib = aLibrary("St. Martin's", "saint@mail.com", "1' Street");
    Page<Library> page = new PageImpl<>(List.of(lib));

    when(repository.searchLibraries("St. Martin's", PageRequest.of(0, 20)))
        .thenReturn(Optional.of(page));

    Map<String, Object> result = service.listLibraries("St. Martin's", 1, 20);

    assertSuccess(result, 1, 20, 1);
  }

  @Test
  void should_throw_when_search_contains_invalid_characters() {
    UnprocessableEntityException ex = assertThrows(
        UnprocessableEntityException.class,
        () -> service.listLibraries("library!</>", 1, 20));

    assertTrue(ex.getMessage().contains("invalid characters"));
    verifyNoInteractions(repository);
  }

  @Test
  void should_use_correct_page_request_for_custom_pagination() {
    Library lib = aLibrary("Lib", "l@mail.com", "Addr");
    Page<Library> page = new PageImpl<>(List.of(lib));

    when(repository.searchLibraries(any(), any(PageRequest.class)))
        .thenReturn(Optional.of(page));

    service.listLibraries(null, 3, 10);

    ArgumentCaptor<PageRequest> captor = ArgumentCaptor.forClass(PageRequest.class);
    verify(repository).searchLibraries(any(), captor.capture());
    assertEquals(2, captor.getValue().getPageNumber()); // page - 1
    assertEquals(10, captor.getValue().getPageSize());
  }

  @Test
  void should_return_empty_data_when_no_results() {
    Page<Library> page = new PageImpl<>(List.of());

    when(repository.searchLibraries("nonexistent", PageRequest.of(0, 20)))
        .thenReturn(Optional.of(page));

    Map<String, Object> result = service.listLibraries("nonexistent", 1, 20);

    assertSuccess(result, 1, 20, 0);
    assertTrue(((List<?>) result.get("data")).isEmpty());
  }

  @Test
  void should_map_all_fields_correctly() {
    UUID id = UUID.randomUUID();
    Library lib = new Library(id, "Main Library", "+261341234567", "main@library.org", "Antananarivo");
    Page<Library> page = new PageImpl<>(List.of(lib));

    when(repository.searchLibraries(any(), any(PageRequest.class)))
        .thenReturn(Optional.of(page));

    Map<String, Object> result = service.listLibraries(null, 1, 20);

    LibraryResponse dto = ((List<LibraryResponse>) result.get("data")).get(0);
    assertEquals(id, dto.getId());
    assertEquals("Main Library", dto.getName());
    assertEquals("+261341234567", dto.getPhone());
    assertEquals("main@library.org", dto.getEmail());
    assertEquals("Antananarivo", dto.getAddress());
  }

  @Test
  void should_return_correct_pagination_info() {
    Library lib = aLibrary("Lib", "l@mail.com", "Addr");
    Page<Library> page = new PageImpl<>(List.of(lib));

    when(repository.searchLibraries(any(), any(PageRequest.class)))
        .thenReturn(Optional.of(page));

    Map<String, Object> result = service.listLibraries(null, 2, 5);

    @SuppressWarnings("unchecked")
    Map<String, Object> pagination = (Map<String, Object>) result.get("pagination");
    assertEquals(2, pagination.get("page"));
    assertEquals(5, pagination.get("size"));
    assertEquals(1L, pagination.get("total"));
  }

  private static void assertSuccess(Map<String, Object> result, int page, int size, long total) {
    assertNotNull(result.get("data"));
    assertNotNull(result.get("pagination"));

    @SuppressWarnings("unchecked")
    Map<String, Object> pagination = (Map<String, Object>) result.get("pagination");
    assertEquals(page, pagination.get("page"));
    assertEquals(size, pagination.get("size"));
    assertEquals(total, pagination.get("total"));
  }

  private static Library aLibrary(String name, String email, String address) {
    return new Library(UUID.randomUUID(), name, "+261330000000", email, address);
  }
}
