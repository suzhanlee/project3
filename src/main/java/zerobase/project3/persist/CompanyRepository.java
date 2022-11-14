package zerobase.project3.persist;

import java.util.List;
import java.util.Optional;
import javax.swing.text.html.Option;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.project3.persist.entity.CompanyEntity;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {

  boolean existsByTicker(String ticker);

  Optional<CompanyEntity> findByName(String name);

  Page<CompanyEntity> findByNameStartingWithIgnoreCase(String s, Pageable pageable);


}
