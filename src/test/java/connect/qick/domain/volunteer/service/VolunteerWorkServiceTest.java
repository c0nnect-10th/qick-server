package connect.qick.domain.volunteer.service;

import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.enums.UserStatus;
import connect.qick.domain.user.enums.UserType;
import connect.qick.domain.user.repository.UserRepository;
import connect.qick.domain.user.service.UserService;
import connect.qick.domain.volunteer.dto.request.CreateVolunteerWorkRequest;
import connect.qick.domain.volunteer.dto.response.ApplicationStudentResponse;
import connect.qick.domain.volunteer.dto.response.CreateVolunteerWorkResponse;
import connect.qick.domain.volunteer.dto.response.VolunteerWorkResponse;
import connect.qick.domain.volunteer.entity.VolunteerApplicationEntity;
import connect.qick.domain.volunteer.entity.VolunteerWorkEntity;
import connect.qick.domain.volunteer.enums.ApplicationStatus;
import connect.qick.domain.volunteer.enums.WorkDifficulty;
import connect.qick.domain.volunteer.enums.WorkStatus;
import connect.qick.domain.volunteer.exception.VolunteerException;
import connect.qick.domain.volunteer.exception.VolunteerStatusCode;
import connect.qick.domain.volunteer.repository.VolunteerApplicationRepository;
import connect.qick.domain.volunteer.repository.VolunteerWorkRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class VolunteerWorkServiceTest {

    @Autowired
    private VolunteerWorkService volunteerWorkService;
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

        VolunteerWorkEntity work = VolunteerWorkEntity.builder()
                .workName("봉사테스트1")
                .maxParticipants(5)
                .location("lol1")
                .description("1")
                .difficulty(WorkDifficulty.HARD)
                .status(WorkStatus.RECRUITING)
                .startTime(LocalDateTime.now())
                .build();
        work.setTeacher(teacher.get(0));
        volunteerWorks.add(volunteerWorkRepository.save(work));
    }

    @Test
    @DisplayName("봉사활동 생성 시 사용자에게 봉사활동이 정상적으로 추가 확인")
    void createVolunteerWork() {
        UserEntity user = teacher.get(0);

        String workName = "봉사제목1";
        int maxParticipants = 5;
        String location = "lo1";
        String description = "설명1";
        WorkDifficulty difficulty = WorkDifficulty.NORMAL;
        LocalDateTime startTime = LocalDateTime.now();
        String googleId = user.getGoogleId();
        CreateVolunteerWorkRequest request = new CreateVolunteerWorkRequest(workName, maxParticipants, location, description, difficulty, startTime);

        CreateVolunteerWorkResponse response = volunteerWorkService.create(googleId, request);


        assertThat(user.getVolunteerWorks().size())
                .isEqualTo(1);

        assertThat(volunteerWorkRepository.findById(response.getId()))
            .isPresent()
            .get()
            .extracting(w -> w.getWorkName())
            .isEqualTo(workName);

    }


    @Test
    @DisplayName("봉사활동 삭제")
    void deleteVolunteerWork() throws Exception {
        VolunteerWorkEntity work = volunteerWorks.stream()
            .findFirst()
            .orElseThrow(() -> new VolunteerException(VolunteerStatusCode.WORK_NOT_FOUND));
        assertThat(work.getTeacher().getVolunteerWorks().size())
            .isEqualTo(1);

        UserEntity user = work.getTeacher();
        String googleId = user.getGoogleId();
        volunteerWorkService.deleteVolunteerWork(work.getId(), googleId);

        assertThat(work.getStatus())
            .isEqualTo(WorkStatus.CANCELLED);
        assertThat(user.getVolunteerWorks())
            .doesNotContain(work);

    }

    @Test
    @DisplayName("봉사활동 완료")
    void completeVolunteerWork() {
        VolunteerWorkEntity work = volunteerWorkRepository.save(
            VolunteerWorkEntity.builder()
                .workName("봉사테스트1")
                .maxParticipants(5)
                .location("lol1")
                .description("1")
                .difficulty(WorkDifficulty.HARD)
                .status(WorkStatus.ONGOING)
                .startTime(LocalDateTime.now())
                .teacher(teacher.get(0))
                .build()
        );

        for (int i=0; i < 3; i++) {
            VolunteerApplicationEntity application = VolunteerApplicationEntity.builder()
                    .appliedAt(LocalDateTime.now())
                    .status(ApplicationStatus.APPLIED)
                    .volunteerWork(work)
                    .student(student.get(i))
                    .build();
            work.addApplication(application);
            volunteerApplications.add(
                volunteerApplicationRepository.save(application)
            );
        }

        List<Long> attendedIds = student.stream().map(UserEntity::getId).collect(Collectors.toList());
        attendedIds.remove(2);
        volunteerWorkService.completeVolunteerWork(work.getId(), attendedIds, work.getTeacher().getGoogleId());

        assertThat(work.getStatus())
            .isEqualTo(WorkStatus.COMPLETED);
        assertThat(volunteerApplications)
            .allMatch(a ->
                    (attendedIds.contains(a.getStudent().getId())) ?
                    a.getStatus() == ApplicationStatus.COMPLETED :
                    a.getStatus() == ApplicationStatus.NO_SHOW
            );
    }

    @Test
    @DisplayName("봉사활동 신청 학생")
    void getApplicationStudents() {
        StopWatch stopWatch = new StopWatch();
        VolunteerWorkEntity work = volunteerWorks.get(0);
        for (int i = 0; i < 10; i++) {
            VolunteerApplicationEntity application = VolunteerApplicationEntity.builder()
                    .appliedAt(LocalDateTime.now())
                    .status(ApplicationStatus.APPLIED)
                    .volunteerWork(work)
                    .student(student.get(i))
                    .build();
            work.addApplication(application);
            volunteerApplications.add(
                    volunteerApplicationRepository.save(application)
            );




        }

        stopWatch.start();
        List<ApplicationStudentResponse> responses = volunteerWorkService.getApplicationStudents(work.getId(), work.getTeacher().getGoogleId());
        stopWatch.stop();

        System.out.println("속도를 볼까요: " + stopWatch.prettyPrint());

        for (int i = 0; i < responses.size(); i++) {
            assertThat(responses.get(i).getApplicationId())
                .isEqualTo(volunteerApplications.get(i).getId());
            assertThat(responses.get(i).getStudentId())
                .isEqualTo(volunteerApplications.get(i).getStudent().getId());
            assertThat(responses.get(i).getStatus())
                .isEqualTo(ApplicationStatus.APPLIED);
        }

    }
}