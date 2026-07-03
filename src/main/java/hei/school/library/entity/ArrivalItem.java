package hei.school.library.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

  @CreationTimestamp
  @Column(name = "created_at", updatable = false, nullable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
