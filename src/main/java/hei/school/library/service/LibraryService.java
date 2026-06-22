package hei.school.library.service;

import hei.school.library.dto.LibraryRequest;
import hei.school.library.dto.LibraryResponse;
import hei.school.library.entity.Library;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.LibraryMapper;
import hei.school.library.repository.dao.LibraryRepository;
import hei.school.library.validator.DataValidator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LibraryService {

  private final LibraryRepository repository;
  private final DataValidator dataValidator;
  private final LibraryMapper mapper;

  public LibraryService(
      LibraryRepository repository, DataValidator dataValidator, LibraryMapper mapper) {
    this.repository = repository;
    this.dataValidator = dataValidator;
    this.mapper = mapper;
  }

  @Transactional(readOnly = true)
  public Map<String, Object> listLibraries(String search, int page, int size) {
    if (search != null) {
      dataValidator.validateString("search", search);
    }
    Page<Library> libraryPage =
        repository
            .searchLibraries(search, PageRequest.of(page - 1, size))
            .orElseThrow(() -> new NotFoundException("Library not found."));

    List<LibraryResponse> data =
        libraryPage.stream()
            .map(
                lib ->
                    new LibraryResponse(
                        lib.getId(),
                        lib.getName(),
                        lib.getPhone(),
                        lib.getEmail(),
                        lib.getAddress()))
            .toList();

    return Map.of(
        "data",
        data,
        "pagination",
        Map.of("page", page, "size", size, "total", libraryPage.getTotalElements()));
  }

  @Transactional
  public LibraryResponse createLibrary(LibraryRequest request) {
    dataValidator.validateString("address", request.getAddress());
    dataValidator.validateName("name", request.getName());
    dataValidator.validateEmail(request.getEmail());
    dataValidator.validatePhone(request.getPhone());

    return mapper.toResponse(
        repository
            .insertLibraryIgnoreConflict(
                request.getName(), request.getPhone(), request.getEmail(), request.getAddress())
            .orElseThrow(
                () ->
                    new ConflictException(
                        "Library with email " + request.getEmail() + " already exists")));
  }

  @Transactional
  public void deleteLibraryById(UUID id) {
    repository
        .deleteByUUId(id)
        .orElseThrow(() -> new NotFoundException("Library with id " + id + " not found"));
  }
}
