package hei.school.library.entity;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

@Entity
@Table(
    name = "author",
    uniqueConstraints =
        @UniqueConstraint(
            columnNames = {"first_name", "last_name"},
            name = "uq_author_name"))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Author {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, length = 100)
  private String firstName;

  @Column(nullable = false, length = 100)
  private String lastName;
}
