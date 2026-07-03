package hei.school.library.entity;

import hei.school.library.entity.enums.SaleStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "sale")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sale {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private Instant saleDate;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private SaleStatus status;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "library_id", nullable = false)
  private Library library;

  @Column private Instant expirationDate;

  @CreationTimestamp
  @Column(updatable = false, nullable = false)
  private Instant createdAt;
}
