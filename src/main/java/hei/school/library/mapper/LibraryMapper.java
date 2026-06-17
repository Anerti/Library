package hei.school.library.mapper;

import hei.school.library.dto.LibraryResponse;
import hei.school.library.entity.Library;
import lombok.Builder;
import org.springframework.stereotype.Component;

@Builder
@Component
public class LibraryMapper {
    public LibraryResponse toResponse(Library library) {
        if (library == null) {
            return null;
        }
        return LibraryResponse.builder()
                .id(library.getId())
                .name(library.getName())
                .phone(library.getPhone())
                .email(library.getEmail())
                .address(library.getAddress())
                .build();
    }
}
