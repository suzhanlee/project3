package zerobase.project3.persist;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.project3.persist.entity.DividendEntity;

@Repository
public interface DividendRepository extends JpaRepository<DividendEntity, Long> {

  List<DividendEntity> findAllByCompanyId(Long companyId);

  boolean existsByCompanyIdAndDate(Long companyId, LocalDateTime date); //db index 와 cardinality 공부하기



}
