package hei.school.library.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(
    name = "sale_book_copy",
    uniqueConstraints = @UniqueConstraint(columnNames = {"book_copy_id", "sale_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleItem {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "book_copy_id", nullable = false)
  private UUID bookCopyId;

  @Column(name = "sale_id", nullable = false)
  private UUID saleId;

  @Column(nullable = false)
  private Integer quantity;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  @CreationTimestamp
  @Column(updatable = false, nullable = false)
  private Instant createdAt;
}
