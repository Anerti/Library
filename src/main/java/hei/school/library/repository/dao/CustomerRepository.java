package hei.school.library.repository.dao;

import hei.school.library.entity.Customer;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

  @Query(
      value =
          """
          SELECT * FROM customer
          WHERE (:search IS NULL OR :search = ''
             OR last_name  ILIKE '%' || :search || '%'
             OR first_name ILIKE '%' || :search || '%'
             OR email      ILIKE '%' || :search || '%')
          """,
      countQuery =
          """
          SELECT COUNT(id) FROM customer
          WHERE (:search IS NULL OR :search = ''
             OR last_name  ILIKE '%' || :search || '%'
             OR first_name ILIKE '%' || :search || '%'
             OR email      ILIKE '%' || :search || '%')
          """,
      nativeQuery = true)
  Page<Customer> findBySearch(@Param("search") String search, Pageable pageable);
}
