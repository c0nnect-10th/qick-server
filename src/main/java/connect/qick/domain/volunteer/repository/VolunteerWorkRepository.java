package connect.qick.domain.volunteer.repository;

import connect.qick.domain.volunteer.entity.VolunteerWorkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolunteerWorkRepository extends JpaRepository<VolunteerWorkEntity, Long> {
}
