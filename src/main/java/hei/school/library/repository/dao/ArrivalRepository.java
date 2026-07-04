package hei.school.library.repository.dao;

import hei.school.library.entity.Arrival;
import java.time.LocalDateTime;
import java.util.Optional;
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
      """
      SELECT a FROM Arrival a
      WHERE a.library.id = :libraryId
      AND (CAST(:from AS localdatetime) IS NULL OR a.arrivalDate >= :from)
      AND (CAST(:to AS localdatetime) IS NULL OR a.arrivalDate <= :to)
      """)
  Page<Arrival> findByLibraryIdAndDateRange(
      @Param("libraryId") UUID libraryId,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to,
      Pageable pageable);

  @Query(
      value =
          """
          INSERT INTO arrival (id, library_id, arrival_date, created_at)
          VALUES (gen_random_uuid(), :libraryId, :arrivalDate, now())
          RETURNING id, library_id, arrival_date, created_at
          """,
      nativeQuery = true)
  Optional<Arrival> create(
      @Param("libraryId") UUID libraryId, @Param("arrivalDate") LocalDateTime arrivalDate);
}
