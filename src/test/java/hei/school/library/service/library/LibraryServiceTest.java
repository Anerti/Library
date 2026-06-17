package hei.school.library.service.library;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import hei.school.library.dto.LibraryRequest;
import hei.school.library.dto.LibraryResponse;
import hei.school.library.entity.Library;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.mapper.LibraryMapper;
import hei.school.library.repository.dao.LibraryRepository;
import hei.school.library.service.LibraryService;
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
  @Mock private LibraryMapper mapper;
  private DataValidator dataValidator;
  private LibraryService service;

  @BeforeEach
  void setUp() {
    dataValidator = new DataValidator();
    service = new LibraryService(repository, dataValidator, mapper);
  }

  @Test
  void should_return_all_libraries_when_search_is_null() {
    Library lib = aLibrary("Lib A", "a@mail.com", "123 Street");
    Page<Library> page = new PageImpl<>(List.of(lib));

    when(repository.searchLibraries("Lib A", PageRequest.of(0, 20))).thenReturn(Optional.of(page));

    Map<String, Object> result = service.listLibraries("Lib A", 1, 20);

    assertSuccess(result, 1, 20, 1);
    LibraryResponse dto = ((List<LibraryResponse>) result.get("data")).get(0);
    assertEquals("Lib A", dto.getName());
    assertEquals("a@mail.com", dto.getEmail());
  }

  @Test
  void should_return_all_libraries_when_search_is_empty() {
    Library lib = aLibrary("Lib A", "a@mail.com", "123 Street");
    Page<Library> page = new PageImpl<>(List.of(lib));

    when(repository.searchLibraries("Lib A", PageRequest.of(0, 20))).thenReturn(Optional.of(page));

    Map<String, Object> result = service.listLibraries("Lib A", 1, 20);

    assertSuccess(result, 1, 20, 1);
  }

  @Test
  void should_return_filtered_libraries_when_search_is_valid() {
    Library lib = aLibrary("Tech Library", "tech@mail.com", "456 Avenue");
    Page<Library> page = new PageImpl<>(List.of(lib));

    when(repository.searchLibraries("tech", PageRequest.of(0, 20))).thenReturn(Optional.of(page));

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
    UnprocessableEntityException ex =
        assertThrows(
            UnprocessableEntityException.class, () -> service.listLibraries("library!</>", 1, 20));

    assertTrue(ex.getMessage().contains("invalid characters"));
    verifyNoInteractions(repository);
  }

  @Test
  void should_use_correct_page_request_for_custom_pagination() {
    Library lib = aLibrary("Lib", "l@mail.com", "Addr");
    Page<Library> page = new PageImpl<>(List.of(lib));

    when(repository.searchLibraries(any(), any(PageRequest.class))).thenReturn(Optional.of(page));

    service.listLibraries("Lib", 3, 10);

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
    Library lib =
        new Library(id, "Main Library", "+261341234567", "main@library.org", "Antananarivo");
    Page<Library> page = new PageImpl<>(List.of(lib));

    when(repository.searchLibraries(any(), any(PageRequest.class))).thenReturn(Optional.of(page));

    Map<String, Object> result = service.listLibraries("Main Library", 1, 20);

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

    when(repository.searchLibraries(any(), any(PageRequest.class))).thenReturn(Optional.of(page));

    Map<String, Object> result = service.listLibraries("Lib", 2, 5);

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

  @Test
  void should_create_library() {
    LibraryRequest request = new LibraryRequest();
    request.setName("Central Library");
    request.setEmail("contact@central.com");
    request.setAddress("123 Main St");
    request.setPhone("+261340000000");

    Library savedLibrary = new Library();
    LibraryResponse expectedResponse = new LibraryResponse();

    when(repository.insertLibraryIgnoreConflict(
            request.getName(), request.getPhone(), request.getEmail(), request.getAddress()))
        .thenReturn(Optional.of(savedLibrary));

    when(mapper.toResponse(savedLibrary)).thenReturn(expectedResponse);
    LibraryResponse actualResponse = service.createLibrary(request);

    assertNotNull(actualResponse);
    verify(repository, times(1))
        .insertLibraryIgnoreConflict(
            request.getName(), request.getPhone(), request.getEmail(), request.getAddress());
    verify(mapper, times(1)).toResponse(savedLibrary);
  }

  @Test
  void should_throw_conflict_exception_when__library_already_exists() {
    LibraryRequest request = new LibraryRequest();
    request.setName("Central Library");
    request.setEmail("contact@central.com");
    request.setAddress("123 Main St");
    request.setPhone("+261340000000");

    when(repository.insertLibraryIgnoreConflict(any(), any(), any(), any()))
        .thenReturn(Optional.empty());
    ConflictException exception =
        assertThrows(
            ConflictException.class,
            () -> {
              service.createLibrary(request);
            });

    assertEquals("Library with email contact@central.com already exists", exception.getMessage());
    verify(mapper, never()).toResponse(any());
  }

  @Test
  void createLibrary_should_throw_UnprocessableEntityException_when_name_is_invalid() {
    LibraryRequest badRequest = new LibraryRequest();
    badRequest.setName("Librairie_Invalide#");
    badRequest.setEmail("contact@library.com");
    badRequest.setPhone("+261340000000");
    badRequest.setAddress("123 Rue de l'Independance");

    UnprocessableEntityException exception =
        assertThrows(UnprocessableEntityException.class, () -> service.createLibrary(badRequest));

    assertTrue(exception.getMessage().contains("forbidden characters"));
    verifyNoInteractions(repository);
  }

  @Test
  void createLibrary_should_throw_UnprocessableEntityException_when_email_is_missing() {
    LibraryRequest badRequest = new LibraryRequest();
    badRequest.setName("Librairie Generale");
    badRequest.setEmail("   ");
    badRequest.setPhone("+261340000000");
    badRequest.setAddress("123 Rue de l'Independance");

    UnprocessableEntityException exception =
        assertThrows(UnprocessableEntityException.class, () -> service.createLibrary(badRequest));

    assertEquals("email is required.", exception.getMessage());
    verifyNoInteractions(repository);
  }

  @Test
  void createLibrary_should_throw_UnprocessableEntityException_when_phone_format_is_invalid() {
    LibraryRequest badRequest = new LibraryRequest();
    badRequest.setName("Librairie Generale");
    badRequest.setEmail("contact@library.com");
    badRequest.setPhone("abc12345");
    badRequest.setAddress("123 Rue de l'Independance");
    UnprocessableEntityException exception =
        assertThrows(UnprocessableEntityException.class, () -> service.createLibrary(badRequest));

    assertTrue(exception.getMessage().contains("Invalid phone format"));
    verifyNoInteractions(repository);
  }

  @Test
  void createLibrary_should_throw_UnprocessableEntityException_when_address_format_is_missing() {
    LibraryRequest badRequest = new LibraryRequest();
    badRequest.setName("Librairie Generale");
    badRequest.setEmail("contact@library.com");
    badRequest.setPhone("+261340000000");
    badRequest.setAddress("");
    UnprocessableEntityException exception =
        assertThrows(UnprocessableEntityException.class, () -> service.createLibrary(badRequest));

    assertEquals("address is required.", exception.getMessage());
    verifyNoInteractions(repository);
  }
}
