package connect.qick.domain.tcode.repository;

import connect.qick.domain.tcode.entity.TCodeEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TCodeRepository extends JpaRepository<TCodeEntity, Long> {
    Optional<TCodeEntity> findByCode(String code);
    boolean existsByCode(String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM TCodeEntity t WHERE t.code = :code")
    Optional<TCodeEntity> findByCodeForUpdate(@Param("code") String code);
}
