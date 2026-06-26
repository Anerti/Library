package hei.school.library.service;

import hei.school.library.dto.AuthorListResponse;
import hei.school.library.dto.AuthorRequest;
import hei.school.library.dto.AuthorResponse;
import hei.school.library.dto.AuthorUpdateRequest;
import hei.school.library.entity.Author;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.AuthorMapper;
import hei.school.library.repository.dao.AuthorRepository;
import hei.school.library.validator.AuthorValidator;
import hei.school.library.validator.DataValidator;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthorService {
  private final AuthorRepository authorRepository;
  private final AuthorValidator authorValidator;
  private final AuthorMapper authorMapper;
  private final DataValidator dataValidator;

  @Transactional(readOnly = true)
  public AuthorListResponse findAll(String search, int page, int size) {
    dataValidator.validateName("search", search);
    PageRequest pageable = PageRequest.of(page - 1, size);

    return (search == null || search.isBlank())
        ? authorMapper.toPageResponse(authorRepository.findAll(pageable), page, size)
        : authorMapper.toPageResponse(authorRepository.findBySearch(search, pageable), page, size);
  }

  @Transactional(readOnly = true)
  public AuthorResponse findById(UUID id) {
    return authorRepository
        .findById(id)
        .map(authorMapper::toResponse)
        .orElseThrow(() -> new NotFoundException(String.format("Author %s not found.", id)));
  }

  @Transactional
  public AuthorResponse create(AuthorRequest authorRequest) {
    dataValidator.validateName("firstName", authorRequest.getFirstName());
    dataValidator.validateName("lastName", authorRequest.getLastName());

    return authorMapper.toResponse(
        authorRepository
            .create(authorRequest.getFirstName(), authorRequest.getLastName())
            .orElseThrow(
                () ->
                    new ConflictException(
                        "Author "
                            + authorRequest.getFirstName()
                            + " "
                            + authorRequest.getLastName()
                            + " already exists")));
  }

  @Transactional
  public AuthorResponse update(UUID id, AuthorUpdateRequest authorUpdateRequest) {
    authorValidator.validateUpdate(authorUpdateRequest);
    Author author =
        authorRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Author with id " + id + " not found"));

    if (authorUpdateRequest.getFirstName() != null) {
      author.setFirstName(authorUpdateRequest.getFirstName());
    }
    if (authorUpdateRequest.getLastName() != null) {
      author.setLastName(authorUpdateRequest.getLastName());
    }
    return authorMapper.toResponse(authorRepository.save(author));
  }

  @Transactional
  public void delete(UUID id) {
    authorRepository
        .delete(id)
        .orElseThrow(() -> new NotFoundException("Author with id " + id + " not found"));
  }
}
