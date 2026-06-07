package hei.school.library.service;

import hei.school.library.dto.AuthorReponse;
import hei.school.library.dto.AuthorRequest;
import hei.school.library.entity.Author;
import hei.school.library.exception.ConflictException;
import hei.school.library.exception.NotFoundException;
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

  public AuthorReponse toDto(Author author) {
    return AuthorReponse.builder()
        .id(author.getId())
        .firstName(author.getFirstName())
        .lastName(author.getLastName())
        .build();
  }

  @Transactional(readOnly = true)
  public List<AuthorReponse> findAll() {
    return authorRepository.findAll().stream().map(this::toDto).toList();
  }

  @Transactional(readOnly = true)
  public AuthorReponse findById(UUID id) {
    return authorRepository
        .findById(id)
        .map(this::toDto)
        .orElseThrow(() -> new NotFoundException("Author with id " + id + " not found"));
  }

  public AuthorReponse create(AuthorRequest authorRequest) {
    authorValidator.validate(authorRequest);

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

    return toDto(authorRepository.save(author));
  }

  public AuthorReponse update(UUID id, AuthorRequest authorRequest) {
    authorValidator.validate(authorRequest);
    Author author =
        authorRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Author with id " + id + " not found"));
    author.setFirstName(authorRequest.getFirstName());
    author.setLastName(authorRequest.getLastName());
    return toDto(authorRepository.save(author));
  }

  public void delete(UUID id) {
    if (!authorRepository.existsById(id)) {
      throw new NotFoundException("Author with id " + id + " not found");
    }
    authorRepository.deleteById(id);
  }
}
