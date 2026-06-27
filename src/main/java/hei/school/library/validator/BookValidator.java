package hei.school.library.validator;

import hei.school.library.dto.BookRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class BookValidator {
    private final DataValidator dataValidator;

    public void validateCreation(BookRequest request) {
        dataValidator.checkNull("title",  request.getTitle());
        dataValidator.checkStringLength("title", request.getTitle(), 100);
        dataValidator.validateBookTitle(request.getTitle());

        dataValidator.checkStringLength("summary", request.getSummary(), 1000);
        dataValidator.validateText("summary", request.getSummary());

        dataValidator.checkNull("isbn",  request.getIsbn());
        dataValidator.validateIsbn(request.getIsbn());

        dataValidator.checkNull("publisher", request.getPublisher());
        dataValidator.checkStringLength("publisher", request.getPublisher(), 100);
        dataValidator.validateName("publisher", request.getPublisher());

        dataValidator.checkNull("publishedAt",  request.getPublishedAt());
    }
}
