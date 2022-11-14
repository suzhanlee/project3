package zerobase.project3.persist;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import zerobase.project3.persist.entity.MemberEntity;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

  Optional<MemberEntity> findByUsername(String username);

  boolean existsByUsername(String username);

}
