package hei.school.library.service;

import hei.school.library.dto.LibraryListResponse;
import hei.school.library.dto.LibraryRequest;
import hei.school.library.dto.LibraryResponse;
import hei.school.library.dto.PaginationDto;
import hei.school.library.entity.Library;
import hei.school.library.exception.ConflictException;
import hei.school.library.mapper.LibraryMapper;
import hei.school.library.mapper.PaginationMapper;
import hei.school.library.repository.dao.LibraryRepository;
import hei.school.library.validator.DataValidator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LibraryService {

  private final LibraryRepository repository;
  private final DataValidator dataValidator;
  private final LibraryMapper libraryMapper;
  private final PaginationMapper paginationMapper;

  @Transactional(readOnly = true)
  public LibraryListResponse listLibraries(String search, int page, int size) {
    dataValidator.validateString("search", search);

    Page<Library> libraryPage =
        repository.searchLibraries(search, PageRequest.of(page - 1, size));

    List<LibraryResponse> data = libraryPage.getContent()
            .stream()
            .map(libraryMapper::toResponse)
            .toList();

    PaginationDto meta = paginationMapper.toPaginationDto(libraryPage, page, size);

    return LibraryListResponse
            .builder()
            .data(data)
            .meta(meta)
            .build();
  }

  @Transactional
  public LibraryResponse createLibrary(LibraryRequest request) {
    dataValidator.validateString("address", request.getAddress());
    dataValidator.validateName("name", request.getName());
    dataValidator.validateEmail(request.getEmail());
    dataValidator.validatePhone(request.getPhone());

    return libraryMapper.toResponse(
        repository
            .insertLibraryIgnoreConflict(
                request.getName(), request.getPhone(), request.getEmail(), request.getAddress())
            .orElseThrow(
                () ->
                    new ConflictException(
                        "Library with email " + request.getEmail() + " already exists")));
  }
}
