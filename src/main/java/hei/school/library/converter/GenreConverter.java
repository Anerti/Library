package hei.school.library.converter;

import hei.school.library.dto.genre.GenreResponse;
import hei.school.library.entity.GenreEntity;
import org.springframework.stereotype.Component;

@Component
public class GenreConverter {
    public GenreResponse toResponse(GenreEntity genre) {
        if (genre == null) {
            return null;
        }
        var response = new GenreResponse();
        response.setId(genre.getId());
        response.setName(genre.getName());
        response.setCreatedAt(genre.getCreatedAt());
        response.setUpdatedAt(genre.getUpdatedAt());
        return response;
    }
}
