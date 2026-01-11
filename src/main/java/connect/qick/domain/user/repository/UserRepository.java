package connect.qick.domain.user.repository;

import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.enums.UserType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByGoogleId(String googleId);

    Optional<UserEntity> findByGoogleId(String googleId);

    UserEntity save(UserEntity userEntity);

    void deleteByGoogleId(String googleId);

    List<UserEntity> findByUserTypeOrderByTotalPointsDesc(UserType userType, Pageable pageable);
}
