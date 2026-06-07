package hei.school.library.service;

import hei.school.library.dto.AuthorDto;
import hei.school.library.entity.Author;
import hei.school.library.exception.NotFoundException;
import hei.school.library.repository.dao.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorDto toDto(Author author) {
        return AuthorDto.builder()
                .id(author.getId())
                .firstName(author.getFirstName())
                .lastName(author.getLastName())
                .build();
    }

    public List<AuthorDto> findAll(){
        return authorRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public AuthorDto findById(UUID id) {
        return authorRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new NotFoundException("Author with id " + id + " not found"));
    }

}
