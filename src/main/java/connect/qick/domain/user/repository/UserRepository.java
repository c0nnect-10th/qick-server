package connect.qick.domain.user.repository;

import connect.qick.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    boolean existsByTeacherCode(String teacherCode);

    boolean existsByEmail(String email);
}
