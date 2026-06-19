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
          SELECT * FROM sale
          WHERE library_id = :libraryId
            AND (:status IS NULL OR status = :status)
            AND (:customerId IS NULL OR customer_id = :customerId)
            AND (:from IS NULL OR sale_date >= :from)
            AND (:to IS NULL OR sale_date <= :to)
          """,
      countQuery =
          """
          SELECT COUNT(*) FROM sale
          WHERE library_id = :libraryId
            AND (:status IS NULL OR status = :status)
            AND (:customerId IS NULL OR customer_id = :customerId)
            AND (:from IS NULL OR sale_date >= :from)
            AND (:to IS NULL OR sale_date <= :to)
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
          INSERT INTO sale (sale_date, status, customer_id, library_id, expiration_date)
          VALUES (:saleDate, :status, :customerId, :libraryId, :expirationDate)
          RETURNING *
          """,
      nativeQuery = true)
  Optional<Sale> create(
      @Param("saleDate") Instant saleDate,
      @Param("status") String status,
      @Param("customerId") UUID customerId,
      @Param("libraryId") UUID libraryId,
      @Param("expirationDate") Instant expirationDate);
}
