package hei.school.library.service;

import hei.school.library.dto.*;
import hei.school.library.entity.Library;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.LibraryMapper;
import hei.school.library.mapper.PaginationMapper;
import hei.school.library.repository.dao.LibraryRepository;
import hei.school.library.validator.DataValidator;
import hei.school.library.validator.LibraryValidator;
import java.util.List;
import java.util.UUID;
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
  private final LibraryValidator libraryValidator;

  @Transactional(readOnly = true)
  public LibraryListResponse listLibraries(String search, int page, int size) {
    dataValidator.validateString("search", search);

    Page<Library> libraryPage = repository.searchLibraries(search, PageRequest.of(page - 1, size));

    List<LibraryResponse> data =
        libraryPage.getContent().stream().map(libraryMapper::toResponse).toList();

    PaginationDto meta = paginationMapper.toPaginationDto(libraryPage, page, size);

    return LibraryListResponse.builder().data(data.isEmpty() ? null : data).meta(meta).build();
  }

  @Transactional
  public LibraryResponse createLibrary(LibraryRequest request) {
    libraryValidator.validateCreation(request);

    return libraryMapper.toResponse(
        repository
            .insertLibraryIgnoreConflict(
                request.getName(), request.getPhone(), request.getEmail(), request.getAddress())
            .orElseThrow(
                () ->
                    new ConflictException(
                        String.format(
                            "Library with this email %s, phone %s, or address %s already exists",
                            request.getEmail(), request.getPhone(), request.getAddress()))));
  }

  @Transactional(readOnly = true)
  public LibraryResponse getLibrary(UUID id) {
    return libraryMapper.toResponse(
        repository
            .findById(id)
            .orElseThrow(() -> new NotFoundException(String.format("Library %s not found.", id))));
  }

  @Transactional
  public void deleteLibrary(UUID id) {
    repository
        .deleteByIdAndReturn(id)
        .orElseThrow(() -> new NotFoundException(String.format("Library %s not found.", id)));
  }
}
