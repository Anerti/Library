package hei.school.library.repository.dao;

import hei.school.library.entity.ArrivalItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArrivalItemRepository extends JpaRepository<ArrivalItem, UUID> {

  @Query("SELECT ai FROM ArrivalItem ai WHERE ai.arrival.id = :arrivalId")
  List<ArrivalItem> findByArrivalId(@Param("arrivalId") UUID arrivalId);

  @Query(
      "SELECT ai FROM ArrivalItem ai "
          + "WHERE ai.arrival.id = :arrivalId AND ai.bookCopy.id = :bookCopyId")
  Optional<ArrivalItem> findByArrivalIdAndBookCopyId(
      @Param("arrivalId") UUID arrivalId, @Param("bookCopyId") UUID bookCopyId);
}
