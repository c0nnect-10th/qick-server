package connect.qick.domain.volunteer.repository;

import connect.qick.domain.volunteer.dto.response.VolunteerWorkResponse;
import connect.qick.domain.volunteer.entity.VolunteerWorkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VolunteerWorkRepository extends JpaRepository<VolunteerWorkEntity, Long> {

    @Query("""
    select new connect.qick.domain.volunteer.dto.response.VolunteerWorkResponse(
        e.workName,
        e.location,
        t.name,
        e.maxParticipants,
        e.currentParticipants
        )
    from VolunteerWorkEntity e
    join e.teacher t
    order by e.createdAt desc
    """)
    List<VolunteerWorkResponse> findAllSummary();
}
