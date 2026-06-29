package hei.school.library.repository.dao;

import hei.school.library.entity.Sale;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends JpaRepository<Sale, UUID> {

  @Query(
      value =
          """
          SELECT id, sale_date, status, user_id, library_id, expiration_date, created_at FROM sale
          WHERE library_id = :libraryId
          AND (:status IS NULL OR status = CAST(:status AS sale_status))
          AND (CAST(:customerId AS uuid) IS NULL OR user_id = CAST(:customerId AS uuid))
          AND (CAST(:from AS timestamp) IS NULL OR sale_date >= CAST(:from AS timestamp))
          AND (CAST(:to AS timestamp) IS NULL OR sale_date <= CAST(:to AS timestamp))
          """,
      countQuery =
          """
          SELECT COUNT(id) FROM sale
          WHERE library_id = :libraryId
          AND (:status IS NULL OR status = CAST(:status AS sale_status))
          AND (CAST(:customerId AS uuid) IS NULL OR user_id = CAST(:customerId AS uuid))
          AND (CAST(:from AS timestamp) IS NULL OR sale_date >= CAST(:from AS timestamp))
          AND (CAST(:to AS timestamp) IS NULL OR sale_date <= CAST(:to AS timestamp))
          """,
      nativeQuery = true)
  Page<Sale> findByLibraryId(
      @Param("libraryId") UUID libraryId,
      @Param("status") String status,
      @Param("customerId") UUID customerId,
      @Param("from") Instant from,
      @Param("to") Instant to,
      Pageable pageable);

  @Query(
      value =
          """
          INSERT INTO sale (sale_date, status, user_id, library_id, expiration_date)
          VALUES (:saleDate, CAST(:status AS sale_status), :customerId, :libraryId, :expirationDate)
          RETURNING id, sale_date, status, user_id, library_id, expiration_date, created_at
          """,
      nativeQuery = true)
  Optional<Sale> create(
      @Param("saleDate") Instant saleDate,
      @Param("status") String status,
      @Param("customerId") UUID customerId,
      @Param("libraryId") UUID libraryId,
      @Param("expirationDate") Instant expirationDate);
}
