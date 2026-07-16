package hei.school.library.dto.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OpenLibraryBookResponse {

    private String title;

    @JsonProperty("publish_date")
    private String publishDate;

    @JsonProperty("number_of_pages")
    private Integer numberOfPages;

    private List<OpenLibraryAuthor> authors = new ArrayList<>();

    private List<OpenLibraryPublisher> publishers = new ArrayList<>();

    private List<OpenLibrarySubject> subjects = new ArrayList<>();

    private OpenLibraryCover cover;
}
