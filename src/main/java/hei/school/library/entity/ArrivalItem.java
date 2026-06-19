package hei.school.library.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "arrival_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArrivalItem {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "arrival_id", nullable = false)
  private Arrival arrival;

  @ManyToOne
  @JoinColumn(name = "book_copy_id", nullable = false)
  private BookCopy bookCopy;

  @Column(nullable = false)
  private Double purchasePrice;

  @Column(nullable = false)
  @Builder.Default
  private Integer quantity = 1;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PrePersist
  public void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  public void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }
}
