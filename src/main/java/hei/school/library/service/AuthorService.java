package hei.school.library.service;

import hei.school.library.dto.AuthorRequest;
import hei.school.library.dto.AuthorResponse;
import hei.school.library.dto.AuthorUpdateRequest;
import hei.school.library.entity.Author;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.NotFoundException;
import hei.school.library.mapper.AuthorMapper;
import hei.school.library.repository.dao.AuthorRepository;
import hei.school.library.validator.AuthorValidator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class AuthorService {
  private final AuthorRepository authorRepository;
  private final AuthorValidator authorValidator;
  private final AuthorMapper authorMapper;

  @Transactional(readOnly = true)
  public List<AuthorResponse> findAll() {
    return authorRepository.findAll().stream().map(authorMapper::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public AuthorResponse findById(UUID id) {
    return authorRepository
        .findById(id)
        .map(authorMapper::toResponse)
        .orElseThrow(() -> new NotFoundException("Author with id " + id + " not found"));
  }

  public AuthorResponse create(AuthorRequest authorRequest) {
    authorValidator.validateCreate(authorRequest);

    if (authorRepository.existsByFirstNameAndLastName(
        authorRequest.getFirstName(), authorRequest.getLastName())) {
      throw new ConflictException(
          "Author with name "
              + authorRequest.getFirstName()
              + " "
              + authorRequest.getLastName()
              + " already exists");
    }

    Author author =
        Author.builder()
            .firstName(authorRequest.getFirstName())
            .lastName(authorRequest.getLastName())
            .build();

    return authorMapper.toResponse(authorRepository.save(author));
  }

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

  public void delete(UUID id) {
    if (!authorRepository.existsById(id)) {
      throw new NotFoundException("Author with id " + id + " not found");
    }
    authorRepository.deleteById(id);
  }
}
