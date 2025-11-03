package connect.qick.domain.user.repository;

import connect.qick.domain.user.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
}
