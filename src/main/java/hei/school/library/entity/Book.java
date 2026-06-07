package hei.school.library.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  //  @NotBlank
  //  @Size(max = 100)
  @Column(nullable = false, length = 100)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String abstractText; // "abstract" est un mot réservé en Java

  //  @NotBlank
  //  @Size(max = 100)
  @Column(nullable = false, unique = true, length = 100)
  private String isbn;

  //  @NotBlank
  //  @Size(max = 100)
  @Column(nullable = false, length = 100)
  private String publisher;

  @Column(name = "published_at")
  private LocalDate publishedAt;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
  }
}
