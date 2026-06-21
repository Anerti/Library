package hei.school.library.repository.dao;

import hei.school.library.entity.SaleItem;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, UUID> {

  @Query(value = "SELECT * FROM sale_book_copy WHERE sale_id = :saleId", nativeQuery = true)
  List<SaleItem> findBySaleId(@Param("saleId") UUID saleId);

  @Query(
      value =
          """
          INSERT INTO sale_book_copy (book_copy_id, sale_id, quantity, price)
          VALUES (:bookCopyId, :saleId, :quantity, :price)
          ON CONFLICT (book_copy_id, sale_id) DO NOTHING
          RETURNING *
          """,
      nativeQuery = true)
  Optional<SaleItem> create(
      @Param("bookCopyId") UUID bookCopyId,
      @Param("saleId") UUID saleId,
      @Param("quantity") Integer quantity,
      @Param("price") BigDecimal price);

  @Query(
      value =
          """
          DELETE FROM sale_book_copy
          WHERE sale_id = :saleId AND book_copy_id = :bookCopyId
          RETURNING book_copy_id
          """,
      nativeQuery = true)
  Optional<UUID> delete(@Param("saleId") UUID saleId, @Param("bookCopyId") UUID bookCopyId);
}
