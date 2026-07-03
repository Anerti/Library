package hei.school.library.repository.dao;

import hei.school.library.entity.ArrivalBookCopy;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArrivalBookCopyRepository extends JpaRepository<ArrivalBookCopy, UUID> {

  @Query("SELECT abc FROM ArrivalBookCopy abc WHERE abc.arrival.id = :arrivalId")
  List<ArrivalBookCopy> findByArrivalId(@Param("arrivalId") UUID arrivalId);

  @Query(
      "SELECT abc FROM ArrivalBookCopy abc "
          + "WHERE abc.arrival.id = :arrivalId AND abc.bookCopy.id = :bookCopyId")
  Optional<ArrivalBookCopy> findByArrivalIdAndBookCopyId(
      @Param("arrivalId") UUID arrivalId, @Param("bookCopyId") UUID bookCopyId);
}
