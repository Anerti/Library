package hei.school.library.repository.dao;

import hei.school.library.entity.SaleItem;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, UUID> {

  @Query(
      value =
          """
          SELECT id, book_copy_id, sale_id, quantity, price, created_at
          FROM sale_book_copy
          WHERE sale_id = :saleId
          """,
      nativeQuery = true)
  List<SaleItem> findBySaleId(@Param("saleId") UUID saleId);
}
