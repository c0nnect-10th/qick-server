package connect.qick.domain.user.repository;

import connect.qick.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    boolean existsByTeacherCode(String teacherCode);

    boolean existsByEmail(String email);

    UserEntity findByEmail(String email);

    Optional<UserEntity> findByTeacherCode(String teacherCode);

}
