package hei.school.library.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorBody {
  String error;
  String message;
  int status;
  @Builder.Default
  Instant timestamp = Instant.now();
}
