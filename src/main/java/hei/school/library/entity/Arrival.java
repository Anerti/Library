package hei.school.library.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "arrival")
@Getter
@Setter
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

  @Column(nullable = false)
  private LocalDateTime arrivalDate;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
    if (this.arrivalDate == null) {
      this.arrivalDate = LocalDateTime.now();
    }
  }
}
