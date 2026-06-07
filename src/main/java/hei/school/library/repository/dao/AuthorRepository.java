package hei.school.library.repository.dao;

import hei.school.library.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface AuthorRepository extends JpaRepository<Author, UUID> {
    Boolean existsByFirstNameAndLastName(String firstName, String lastName);
}
