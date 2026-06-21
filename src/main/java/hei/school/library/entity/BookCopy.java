package hei.school.library.entity;

import hei.school.library.entity.enums.BookCopyFormat;
import hei.school.library.entity.enums.BookCopyStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "book_copy")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookCopy {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private Double price;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BookCopyFormat format;

  @ManyToOne
  @JoinColumn(name = "library_id", nullable = false)
  private Library library;

  @ManyToOne
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private BookCopyStatus status = BookCopyStatus.AVAILABLE;

  @Column(nullable = false)
  private Integer pageNumber;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  @PreUpdate
  public void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }
}
