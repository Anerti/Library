package hei.school.library.repository.dao;

import hei.school.library.entity.Arrival;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArrivalRepository extends JpaRepository<Arrival, UUID> {

  @Query(
      "SELECT a FROM Arrival a WHERE a.library.id = :libraryId "
          + "AND (:from IS NULL OR a.arrivalDate >= :from) "
          + "AND (:to IS NULL OR a.arrivalDate <= :to)")
  Page<Arrival> findByLibraryIdAndDateRange(
      @Param("libraryId") UUID libraryId,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to,
      Pageable pageable);
}
