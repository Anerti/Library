package hei.school.library.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
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

  @Column(nullable = false, length = 100)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String abstractText;

  @Column(nullable = false, unique = true, length = 100)
  private String isbn;

  @Column(nullable = false, length = 100)
  private String publisher;

  @Column(name = "published_at")
  private LocalDate publishedAt;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @ManyToMany
  @JoinTable(
      name = "book_authors",
      joinColumns = @JoinColumn(name = "book_id"),
      inverseJoinColumns = @JoinColumn(name = "author_id"))
  private Set<Author> authors = new HashSet<>();

  //    @ManyToMany
  //    @JoinTable(
  //            name = "book_genres",
  //            joinColumns = @JoinColumn(name = "book_id"),
  //            inverseJoinColumns = @JoinColumn(name = "genre_id")
  //    )
  //    private Set<Genre> genres = new HashSet<>();

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
  }
}
