package hei.school.library.service;

import hei.school.library.dto.AuthorDto;
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

@RequiredArgsConstructor
@Service
public class AuthorService {
  private final AuthorRepository authorRepository;
  private final AuthorValidator authorValidator;

  public AuthorDto toDto(Author author) {
    return AuthorDto.builder()
        .id(author.getId())
        .firstName(author.getFirstName())
        .lastName(author.getLastName())
        .build();
  }

  public List<AuthorDto> findAll() {
    return authorRepository.findAll().stream().map(this::toDto).toList();
  }

  public AuthorDto findById(UUID id) {
    return authorRepository
        .findById(id)
        .map(this::toDto)
        .orElseThrow(() -> new NotFoundException("Author with id " + id + " not found"));
  }

  public AuthorDto create(AuthorRequest authorRequest) {
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
}
