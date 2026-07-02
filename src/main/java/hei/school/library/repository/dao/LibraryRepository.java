package hei.school.library.repository.dao;

import hei.school.library.dto.RevenueByGenreItem;
import hei.school.library.entity.Library;
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
public interface LibraryRepository extends JpaRepository<Library, UUID> {

  @Query(
      value =
          """
          SELECT id, name, phone, email, address FROM library
          WHERE (:search IS NULL OR :search = ''
            OR name ILIKE '%' || :search || '%'
            OR email ILIKE '%' || :search || '%'
            OR phone ILIKE '%' || :search || '%'
            OR address ILIKE '%' || :search || '%')
          """,
      countQuery =
          """
          SELECT COUNT(id) FROM library
          WHERE (:search IS NULL OR :search = ''
            OR name ILIKE '%' || :search || '%'
            OR email ILIKE '%' || :search || '%'
            OR phone ILIKE '%' || :search || '%'
            OR address ILIKE '%' || :search || '%')
          """,
      nativeQuery = true)
  Page<Library> searchLibraries(@Param("search") String search, Pageable pageable);

  @Query(
      value =
          """
          INSERT INTO library (name, phone, email, address)
          VALUES (:name, :phone, :email, :address)
          ON CONFLICT DO NOTHING
          RETURNING id, name, phone, email, address
          """,
      nativeQuery = true)
  Optional<Library> insertLibraryIgnoreConflict(
      @Param("name") String name,
      @Param("phone") String phone,
      @Param("email") String email,
      @Param("address") String address);

  @Query(
      value =
          """
          DELETE FROM library WHERE id = :id
          RETURNING id
          """,
      nativeQuery = true)
  Optional<UUID> deleteByIdAndReturn(@Param("id") UUID id);

  @Query(
      value =
          """
          SELECT new hei.school.library.dto.RevenueByGenreItem(
              g.id, g.name, SUM(si.price * si.quantity), CAST(SUM(si.quantity) AS integer)
          )
          FROM Genre g
          JOIN g.books b
          JOIN BookCopy bc ON bc.book = b
          JOIN SaleItem si ON si.bookCopyId = bc.id
          JOIN Sale s ON s.id = si.saleId
          WHERE bc.library.id = :libraryId
            AND s.status = hei.school.library.entity.enums.SaleStatus.SOLD
            AND s.saleDate BETWEEN :start AND :end
          GROUP BY g.id, g.name
          ORDER BY CASE WHEN :sortOrder = 'asc' THEN SUM(si.price * si.quantity) END ASC,
                   CASE WHEN :sortOrder != 'asc' THEN SUM(si.price * si.quantity) END DESC
          """,
      countQuery =
          """
          SELECT COUNT(DISTINCT g.id)
          FROM Genre g
          JOIN g.books b
          JOIN BookCopy bc ON bc.book = b
          JOIN SaleItem si ON si.bookCopyId = bc.id
          JOIN Sale s ON s.id = si.saleId
          WHERE bc.library.id = :libraryId
            AND s.status = hei.school.library.entity.enums.SaleStatus.SOLD
            AND s.saleDate BETWEEN :start AND :end
          """)
  Page<RevenueByGenreItem> findRevenueByGenre(
      @Param("libraryId") UUID libraryId,
      @Param("start") Instant start,
      @Param("end") Instant end,
      @Param("sortOrder") String sortOrder,
      Pageable pageable);
}
