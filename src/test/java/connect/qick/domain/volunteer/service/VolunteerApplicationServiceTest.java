package connect.qick.domain.volunteer.service;

import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.enums.UserStatus;
import connect.qick.domain.user.enums.UserType;
import connect.qick.domain.user.repository.UserRepository;
import connect.qick.domain.volunteer.entity.VolunteerApplicationEntity;
import connect.qick.domain.volunteer.entity.VolunteerWorkEntity;
import connect.qick.domain.volunteer.enums.WorkDifficulty;
import connect.qick.domain.volunteer.enums.WorkStatus;
import connect.qick.domain.volunteer.repository.VolunteerApplicationRepository;
import connect.qick.domain.volunteer.repository.VolunteerWorkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


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
                        .googleId("1234")
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


}