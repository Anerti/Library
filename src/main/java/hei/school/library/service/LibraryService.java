package hei.school.library.service;

import hei.school.library.dto.LibraryResponse;
import hei.school.library.entity.Library;
import hei.school.library.exception.NotFoundException;
import hei.school.library.repository.dao.LibraryRepository;
import java.util.List;
import java.util.Map;

import hei.school.library.validator.DataValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LibraryService {

  private final LibraryRepository repository;
  private final DataValidator dataValidator;

  public LibraryService(LibraryRepository repository, DataValidator dataValidator) {
    this.repository = repository;
    this.dataValidator = dataValidator;
  }

  @Transactional(readOnly = true)
  public Map<String, Object> listLibraries(String search, int page, int size) {
    dataValidator.validateString("search", search);
    Page<Library> libraryPage = repository.searchLibraries(search, PageRequest.of(page - 1, size))
            .orElseThrow(() -> new NotFoundException("Library not found."));

    List<LibraryResponse> data = libraryPage.stream()
        .map(lib -> new LibraryResponse(lib.getId(), lib.getName(), lib.getPhone(), lib.getEmail(), lib.getAddress()))
        .toList();

    return Map.of("data", data, "pagination", Map.of("page", page, "size", size, "total", libraryPage.getTotalElements()));
  }
}
