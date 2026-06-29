package hei.school.library.entity;

import hei.school.library.entity.enums.Role;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

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

  @Column(nullable = false)
  private String password;

  @Column(length = 30)
  private String phone;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Column(updatable = false, nullable = false)
  @CreationTimestamp
  private Instant createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  private Instant updatedAt;
}
