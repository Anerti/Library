package hei.school.library.service.library;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import hei.school.library.dto.LibraryListResponse;
import hei.school.library.dto.LibraryRequest;
import hei.school.library.dto.LibraryResponse;
import hei.school.library.entity.Library;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.NotFoundException;
import hei.school.library.exception.UnprocessableEntityException;
import hei.school.library.mapper.LibraryMapper;
import hei.school.library.mapper.PaginationMapper;
import hei.school.library.repository.dao.LibraryRepository;
import hei.school.library.service.LibraryService;
import hei.school.library.validator.DataValidator;
import hei.school.library.validator.LibraryValidator;
import java.util.List;
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
  @Mock private LibraryMapper libraryMapper;
  private PaginationMapper paginationMapper;
  private DataValidator dataValidator;
  private LibraryValidator libraryValidator;
  private LibraryService service;

  @BeforeEach
  void setUp() {
    paginationMapper = new PaginationMapper();
    dataValidator = new DataValidator();
    libraryValidator = new LibraryValidator(dataValidator);
    service =
        new LibraryService(
            repository, dataValidator, libraryMapper, paginationMapper, libraryValidator);
  }

  @Test
  void should_return_all_libraries_when_search_is_null() {
    Library lib = aLibrary("Lib A", "a@mail.com", "123 Street");
    LibraryResponse dto = aLibraryResponse(lib);
    Page<Library> page = new PageImpl<>(List.of(lib));

    when(repository.searchLibraries("Lib A", PageRequest.of(0, 20))).thenReturn(page);
    when(libraryMapper.toResponse(lib)).thenReturn(dto);

    LibraryListResponse result = service.listLibraries("Lib A", 1, 20);

    assertSuccess(result, 1, 20, 1);
    assertEquals("Lib A", result.getData().get(0).getName());
    assertEquals("a@mail.com", result.getData().get(0).getEmail());
  }

  @Test
  void should_return_all_libraries_when_search_is_empty() {
    Library lib = aLibrary("Lib A", "a@mail.com", "123 Street");
    LibraryResponse dto = aLibraryResponse(lib);
    Page<Library> page = new PageImpl<>(List.of(lib));

    when(repository.searchLibraries("Lib A", PageRequest.of(0, 20))).thenReturn(page);
    when(libraryMapper.toResponse(lib)).thenReturn(dto);

    LibraryListResponse result = service.listLibraries("Lib A", 1, 20);

    assertSuccess(result, 1, 20, 1);
  }

  @Test
  void should_return_filtered_libraries_when_search_is_valid() {
    Library lib = aLibrary("Tech Library", "tech@mail.com", "456 Avenue");
    LibraryResponse dto = aLibraryResponse(lib);
    Page<Library> page = new PageImpl<>(List.of(lib));

    when(repository.searchLibraries("tech", PageRequest.of(0, 20))).thenReturn(page);
    when(libraryMapper.toResponse(lib)).thenReturn(dto);

    LibraryListResponse result = service.listLibraries("tech", 1, 20);

    assertSuccess(result, 1, 20, 1);
  }

  @Test
  void should_allow_special_characters_in_search() {
    Library lib = aLibrary("St. Martin's", "saint@mail.com", "1' Street");
    LibraryResponse dto = aLibraryResponse(lib);
    Page<Library> page = new PageImpl<>(List.of(lib));

    when(repository.searchLibraries("St. Martin's", PageRequest.of(0, 20))).thenReturn(page);
    when(libraryMapper.toResponse(lib)).thenReturn(dto);

    LibraryListResponse result = service.listLibraries("St. Martin's", 1, 20);

    assertSuccess(result, 1, 20, 1);
  }

  @Test
  void should_search_by_phone_number() {
    Library lib = aLibrary("Lib A", "a@mail.com", "123 Street");
    LibraryResponse dto = aLibraryResponse(lib);
    Page<Library> page = new PageImpl<>(List.of(lib));

    when(repository.searchLibraries("+261", PageRequest.of(0, 20))).thenReturn(page);
    when(libraryMapper.toResponse(lib)).thenReturn(dto);

    LibraryListResponse result = service.listLibraries("+261", 1, 20);

    assertSuccess(result, 1, 20, 1);
    assertTrue(result.getData().get(0).getPhone().startsWith("+261"));
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
    LibraryResponse dto = aLibraryResponse(lib);
    Page<Library> page = new PageImpl<>(List.of(lib));

    when(repository.searchLibraries(any(), any(PageRequest.class))).thenReturn(page);
    when(libraryMapper.toResponse(lib)).thenReturn(dto);

    service.listLibraries("Lib", 3, 10);

    ArgumentCaptor<PageRequest> captor = ArgumentCaptor.forClass(PageRequest.class);
    verify(repository).searchLibraries(any(), captor.capture());
    assertEquals(2, captor.getValue().getPageNumber());
    assertEquals(10, captor.getValue().getPageSize());
  }

  @Test
  void should_return_empty_data_when_no_results() {
    Page<Library> page = new PageImpl<>(List.of());

    when(repository.searchLibraries("nonexistent", PageRequest.of(0, 20))).thenReturn(page);

    LibraryListResponse result = service.listLibraries("nonexistent", 1, 20);

    assertSuccess(result, 1, 20, 0);
    assertNull(result.getData());
  }

  @Test
  void should_map_all_fields_correctly() {
    UUID id = UUID.randomUUID();
    Library lib =
        new Library(id, "Main Library", "+261****4567", "main@library.org", "Antananarivo");
    Page<Library> page = new PageImpl<>(List.of(lib));

    LibraryResponse expected =
        LibraryResponse.builder()
            .id(id)
            .name("Main Library")
            .phone("+261****4567")
            .email("main@library.org")
            .address("Antananarivo")
            .build();

    when(repository.searchLibraries(any(), any(PageRequest.class))).thenReturn(page);
    when(libraryMapper.toResponse(lib)).thenReturn(expected);

    LibraryListResponse result = service.listLibraries("Main Library", 1, 20);

    LibraryResponse dto = result.getData().get(0);
    assertEquals(id, dto.getId());
    assertEquals("Main Library", dto.getName());
    assertEquals("+261****4567", dto.getPhone());
    assertEquals("main@library.org", dto.getEmail());
    assertEquals("Antananarivo", dto.getAddress());
  }

  @Test
  void should_return_correct_pagination_info() {
    Library lib = aLibrary("Lib", "l@mail.com", "Addr");
    LibraryResponse dto = aLibraryResponse(lib);
    Page<Library> page = new PageImpl<>(List.of(lib));

    when(repository.searchLibraries(any(), any(PageRequest.class))).thenReturn(page);
    when(libraryMapper.toResponse(lib)).thenReturn(dto);

    LibraryListResponse result = service.listLibraries("Lib", 2, 5);

    assertEquals(2, result.getMeta().getPage());
    assertEquals(5, result.getMeta().getSize());
    assertEquals(1L, result.getMeta().getTotal());
  }

  @Test
  void should_create_library() {
    LibraryRequest request = new LibraryRequest();
    request.setName("Central Library");
    request.setEmail("contact@central.com");
    request.setAddress("Main Street");
    request.setPhone("+261 33 44 55 66");

    Library savedLibrary = new Library();
    LibraryResponse expectedResponse = new LibraryResponse();

    when(repository.insertLibraryIgnoreConflict(
            request.getName(), request.getPhone(), request.getEmail(), request.getAddress()))
        .thenReturn(Optional.of(savedLibrary));
    when(libraryMapper.toResponse(savedLibrary)).thenReturn(expectedResponse);

    LibraryResponse actualResponse = service.createLibrary(request);

    assertNotNull(actualResponse);
    verify(repository)
        .insertLibraryIgnoreConflict(
            request.getName(), request.getPhone(), request.getEmail(), request.getAddress());
    verify(libraryMapper).toResponse(savedLibrary);
  }

  @Test
  void should_throw_conflict_exception_when_library_already_exists() {
    LibraryRequest request = new LibraryRequest();
    request.setName("Central Library");
    request.setEmail("contact@central.com");
    request.setAddress("Main Street");
    request.setPhone("+261 33 44 55 66");

    when(repository.insertLibraryIgnoreConflict(any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    ConflictException exception =
        assertThrows(ConflictException.class, () -> service.createLibrary(request));

    assertTrue(exception.getMessage().contains("already exists"));
    verify(libraryMapper, never()).toResponse(any());
  }

  @Test
  void createLibrary_should_throw_UnprocessableEntityException_when_name_is_invalid() {
    LibraryRequest badRequest = new LibraryRequest();
    badRequest.setName("Librairie_Invalide#");
    badRequest.setEmail("contact@library.com");
    badRequest.setPhone("+261 33 44 55 66");
    badRequest.setAddress("123 Rue de l'Independance");

    assertThrows(UnprocessableEntityException.class, () -> service.createLibrary(badRequest));

    verifyNoInteractions(repository);
  }

  @Test
  void createLibrary_should_throw_UnprocessableEntityException_when_email_is_missing() {
    LibraryRequest badRequest = new LibraryRequest();
    badRequest.setName("Librairie Generale");
    badRequest.setEmail("   ");
    badRequest.setPhone("+261 33 44 55 66");
    badRequest.setAddress("123 Rue de l'Independance");

    UnprocessableEntityException exception =
        assertThrows(UnprocessableEntityException.class, () -> service.createLibrary(badRequest));

    assertEquals("email is required and cannot be blank.", exception.getMessage());
    verifyNoInteractions(repository);
  }

  @Test
  void createLibrary_should_throw_UnprocessableEntityException_when_phone_format_is_invalid() {
    LibraryRequest badRequest = new LibraryRequest();
    badRequest.setName("Librairie Generale");
    badRequest.setEmail("contact@library.com");
    badRequest.setPhone("abc12345");
    badRequest.setAddress("123 Rue de l'Independance");

    assertThrows(UnprocessableEntityException.class, () -> service.createLibrary(badRequest));

    verifyNoInteractions(repository);
  }

  @Test
  void
      createLibrary_should_throw_UnprocessableEntityException_when_address_has_invalid_characters() {
    LibraryRequest badRequest = new LibraryRequest();
    badRequest.setName("Librairie Generale");
    badRequest.setEmail("contact@library.com");
    badRequest.setPhone("+261 33 44 55 66");
    badRequest.setAddress("123 Main St|");

    assertThrows(UnprocessableEntityException.class, () -> service.createLibrary(badRequest));

    verifyNoInteractions(repository);
  }

  @Test
  void should_return_library_when_id_exists() {
    UUID id = UUID.randomUUID();
    Library lib =
        new Library(
            id,
            "Librairie Générale",
            "+261 34 12 345 67",
            "contact@librairie-generale.mg",
            "15 Avenue de l'Indépendance, Antananarivo");
    LibraryResponse expected = aLibraryResponse(lib);

    when(repository.findById(id)).thenReturn(Optional.of(lib));
    when(libraryMapper.toResponse(lib)).thenReturn(expected);

    LibraryResponse actual = service.getLibrary(id);

    assertNotNull(actual);
    assertEquals(id, actual.getId());
    assertEquals("Librairie Générale", actual.getName());
    assertEquals("contact@librairie-generale.mg", actual.getEmail());
    verify(repository).findById(id);
    verify(libraryMapper).toResponse(lib);
  }

  @Test
  void should_throw_not_found_when_id_does_not_exist() {
    UUID id = UUID.randomUUID();
    when(repository.findById(id)).thenReturn(Optional.empty());

    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> service.getLibrary(id));

    assertTrue(
        exception.getMessage().contains("not found."),
        "Expected message to contain 'not found.'. Actual: '" + exception.getMessage() + "'");
    verify(repository).findById(id);
    verifyNoInteractions(libraryMapper);
  }

  @Test
  void should_delete_library_when_id_exists() {
    UUID id = UUID.randomUUID();
    when(repository.deleteByIdAndReturn(id)).thenReturn(Optional.of(id));

    service.deleteLibrary(id);

    verify(repository).deleteByIdAndReturn(id);
  }

  @Test
  void should_throw_not_found_when_deleting_non_existent_library() {
    UUID id = UUID.randomUUID();
    when(repository.deleteByIdAndReturn(id)).thenReturn(Optional.empty());

    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> service.deleteLibrary(id));

    assertTrue(exception.getMessage().contains("not found"));
    verify(repository).deleteByIdAndReturn(id);
  }

  private static void assertSuccess(LibraryListResponse result, int page, int size, long total) {
    if (total > 0) {
      assertNotNull(result.getData());
    }
    assertNotNull(result.getMeta());
    assertEquals(page, result.getMeta().getPage());
    assertEquals(size, result.getMeta().getSize());
    assertEquals(total, result.getMeta().getTotal());
  }

  private static Library aLibrary(String name, String email, String address) {
    return new Library(UUID.randomUUID(), name, "+261****4567", email, address);
  }

  private static LibraryResponse aLibraryResponse(Library lib) {
    return LibraryResponse.builder()
        .id(lib.getId())
        .name(lib.getName())
        .phone(lib.getPhone())
        .email(lib.getEmail())
        .address(lib.getAddress())
        .build();
  }
}
