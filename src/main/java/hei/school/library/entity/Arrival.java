package hei.school.library.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "arrival")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Arrival {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "library_id", nullable = false)
  private Library library;

  @Builder.Default
  @Column(nullable = false)
  private LocalDateTime arrivalDate = LocalDateTime.now();

  @CreationTimestamp
  @Column(updatable = false, nullable = false)
  private LocalDateTime createdAt;
}
