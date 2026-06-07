package hei.school.library.repository.dao;

import hei.school.library.entity.Author;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, UUID> {
  Boolean existsByFirstNameAndLastName(String firstName, String lastName);
}
