package connect.qick.domain.volunteer.service;

import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.enums.UserStatus;
import connect.qick.domain.user.enums.UserType;
import connect.qick.domain.user.repository.UserRepository;
import connect.qick.domain.volunteer.entity.VolunteerApplicationEntity;
import connect.qick.domain.volunteer.entity.VolunteerWorkEntity;
import connect.qick.domain.volunteer.enums.ApplicationStatus;
import connect.qick.domain.volunteer.enums.WorkDifficulty;
import connect.qick.domain.volunteer.enums.WorkStatus;
import connect.qick.domain.volunteer.repository.VolunteerApplicationRepository;
import connect.qick.domain.volunteer.repository.VolunteerWorkRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Transactional
@SpringBootTest
class VolunteerApplicationServiceTest {

    @Autowired
    private VolunteerApplicationService volunteerApplicationService;
    @Autowired
    private VolunteerWorkRepository volunteerWorkService ;
    @Autowired
    private VolunteerWorkRepository volunteerWorkRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VolunteerApplicationRepository volunteerApplicationRepository;

    private final List<UserEntity> student = new ArrayList<>();
    private final List<UserEntity> teacher = new ArrayList<>();
    private final List<VolunteerWorkEntity> volunteerWorks = new ArrayList<>();
    private final List<VolunteerApplicationEntity> volunteerApplications = new ArrayList<>();


    @BeforeEach
    void setUp() {
        teacher.add(userRepository.save(
                UserEntity.builder()
                        .userType(UserType.TEACHER)
                        .userStatus(UserStatus.ACTIVE)
                        .googleId("new")
                        .name("test1")
                        .build()
        ));

        for(int i =0; i < 10; i++) {
            student.add(userRepository.save(
                    UserEntity.builder()
                            .userType(UserType.TEACHER)
                            .userStatus(UserStatus.ACTIVE)
                            .googleId("testGoogleId"+i)
                            .name("testUser"+i)
                            .build()
            ));
        }

        for (int i =0; i < 10; i++) {
            VolunteerWorkEntity work = VolunteerWorkEntity.builder()
                    .workName("봉사테스트"+i)
                    .maxParticipants(5)
                    .location("lol"+i)
                    .description(""+i)
                    .difficulty(WorkDifficulty.HARD)
                    .status(WorkStatus.RECRUITING)
                    .startTime(LocalDateTime.now())
                    .build();
            work.setTeacher(teacher.get(0));
            volunteerWorks.add(volunteerWorkRepository.save(work));
        }

    }

    @Test
    @DisplayName("봉사활동 취소 테스트")
    void cancelVolunteerApplication() {
        VolunteerWorkEntity work = volunteerWorks.get(0);
        UserEntity student1 = student.get(0);
        VolunteerApplicationEntity application =
                VolunteerApplicationEntity.builder()
                .status(ApplicationStatus.APPLIED)
                .appliedAt(LocalDateTime.now())
                .build();
        work.addApplication(application);
        application.setStudent(student1);
        volunteerApplicationRepository.save(application);

        volunteerApplicationService.cancelApplication(
                application.getId(), student1.getGoogleId(), "test1");

        assertThat(work.getCurrentParticipants()).isEqualTo(0);
        assertThat(application.getCancelReason()).isEqualTo("test1");
        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.CANCELLED);
    }


}