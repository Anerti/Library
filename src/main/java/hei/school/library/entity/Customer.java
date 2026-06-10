package hei.school.library.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, length = 100)
  private String lastName;

  @Column(length = 100)
  private String firstName;

  @Column(nullable = false)
  private LocalDate birthDate;

  @Column(nullable = false, length = 100, unique = true)
  private String email;

  @Column(length = 30)
  private String phone;

  @Column(updatable = false, nullable = false)
  @CreationTimestamp
  private Instant createdAt;

  @UpdateTimestamp private Instant updatedAt;
}
