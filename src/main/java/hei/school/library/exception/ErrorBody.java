package hei.school.library.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorBody {
  int status;
  String error;
  String message;
  @Builder.Default Instant timestamp = Instant.now();

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static void send(HttpServletResponse response, HttpStatus status, String message)
      throws IOException {
    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(status.value());
    MAPPER.writeValue(
        response.getWriter(),
        ErrorBody.builder()
            .status(status.value())
            .error(status.getReasonPhrase())
            .message(message)
            .build());
  }
}
