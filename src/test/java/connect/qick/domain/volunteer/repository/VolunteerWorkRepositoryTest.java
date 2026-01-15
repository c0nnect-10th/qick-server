package connect.qick.domain.volunteer.repository;

import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.enums.UserStatus;
import connect.qick.domain.user.enums.UserType;
import connect.qick.domain.user.repository.UserRepository;
import connect.qick.domain.volunteer.dto.response.VolunteerWorkSummaryResponse;
import connect.qick.domain.volunteer.entity.VolunteerApplicationEntity;
import connect.qick.domain.volunteer.entity.VolunteerWorkEntity;
import connect.qick.domain.volunteer.enums.ApplicationStatus;
import connect.qick.domain.volunteer.enums.WorkDifficulty;
import connect.qick.domain.volunteer.enums.WorkStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
class VolunteerWorkRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VolunteerWorkRepository volunteerWorkRepository;

    @Autowired
    private VolunteerApplicationRepository volunteerApplicationRepository;

    public boolean compareVolunteerEntity(VolunteerWorkEntity o1, Long id) {
        return o1.getApplications().stream().anyMatch(app -> app.getStudent().getId().equals(id));
    }

    @Test
    @DisplayName("사용자가 신청한 volunteer를 우선 조회")
    void findAllSummaryByUserId() {
        UserEntity student = userRepository.save(
                UserEntity.builder()
                        .userType(UserType.STUDENT)
                        .userStatus(UserStatus.ACTIVE)
                        .googleId("123456789")
                        .build()
        );
        UserEntity student1 = userRepository.save(
                UserEntity.builder()
                        .userType(UserType.STUDENT)
                        .userStatus(UserStatus.ACTIVE)
                        .googleId("qwertyuio")
                        .build()
        );

        UserEntity teacher = userRepository.save(
                UserEntity.builder()
                        .userType(UserType.TEACHER)
                        .userStatus(UserStatus.ACTIVE)
                        .build()
        );

        List<VolunteerWorkEntity> works = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            VolunteerWorkEntity work = VolunteerWorkEntity.builder()
                    .workName("봉사활동 " + i)
                    .location("장소 " + i)
                    .maxParticipants(10)
                    .currentParticipants(0)
                    .difficulty(WorkDifficulty.EASY)
                    .description("설명 " + i)
                    .points(10)
                    .status(WorkStatus.RECRUITING)
                    .startTime(LocalDateTime.now().plusDays(i))
                    .build();
            work.setTeacher(teacher);
            works.add(work);
        }
        volunteerWorkRepository.saveAll(works);

        List<VolunteerApplicationEntity> applications = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            VolunteerApplicationEntity app = VolunteerApplicationEntity.builder()
                    .volunteerWork(works.get(i * 2))
                    .student(student)
                    .status(ApplicationStatus.APPLIED)
                    .build();
            applications.add(app);
        }
        volunteerApplicationRepository.saveAll(applications);
        volunteerApplicationRepository.save(
                VolunteerApplicationEntity.builder()
                        .volunteerWork(works.get(3))
                        .student(student1)
                        .build()
        );

        List<VolunteerWorkEntity> result =
                volunteerWorkRepository.findAllOrderByApplications(student.getGoogleId());
        List<VolunteerWorkSummaryResponse> a = volunteerWorkRepository.findAllSummary();
        for (VolunteerWorkSummaryResponse o : a) {
            System.out.println(o.getWorkName());
        }
        System.out.println("===");
        for (VolunteerWorkEntity work : result) {
            System.out.println(work.getId().toString() + "and" + work.getWorkName());
        }

    }
}