package hei.school.library.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class AuthorDto {
    private UUID id;
    private String firstName;
    private String lastName;
}
