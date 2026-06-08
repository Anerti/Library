package hei.school.library.mapper;

import hei.school.library.dto.GenreResponse;
import hei.school.library.entity.Genre;
import lombok.Builder;
import org.springframework.stereotype.Component;
@Builder
@Component
public class GenreConverter {
    public GenreResponse toResponse(Genre genre) {
        if (genre == null) {
            return null;
        }
        return GenreResponse.builder()
                .id(genre.getId())
                .name(genre.getName())
                .createdAt(genre.getCreatedAt())
                .updatedAt(genre.getUpdatedAt())
                .build();
    }
}
