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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;


@Transactional
@SpringBootTest
class VolunteerApplicationServiceTest {

    @Autowired
    private VolunteerApplicationService volunteerApplicationService;
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
        student.clear();
        teacher.clear();
        volunteerWorks.clear();

        teacher.add(userRepository.save(
                UserEntity.builder()
                        .userType(UserType.TEACHER)
                        .userStatus(UserStatus.ACTIVE)
                        .googleId("teacher-google-id")
                        .name("test1")
                        .build()
        ));

        for(int i =0; i < 300; i++) {
            student.add(userRepository.save(
                    UserEntity.builder()
                            .userType(UserType.STUDENT)
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

        System.out.println("================================\n\n");
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

    @Test
    @DisplayName("300명 봉사활동 신청 및 전원 취소 테스트")
    void applyAndCancel_300_Users_Test() {
        // Given
        VolunteerWorkEntity largeWork = VolunteerWorkEntity.builder()
                .workName("대규모 봉사활동")
                .maxParticipants(300)
                .location("온라인")
                .description("300명 테스트")
                .difficulty(WorkDifficulty.EASY)
                .status(WorkStatus.RECRUITING)
                .startTime(LocalDateTime.now())
                .build();
        largeWork.setTeacher(teacher.get(0));
        volunteerWorkRepository.save(largeWork);

        // When
        // 300명의 학생이 봉사활동을 신청
        for (UserEntity studentEntity : student) {
            volunteerApplicationService.applyToVolunteer(largeWork.getId(), studentEntity.getGoogleId());
        }

        // 신청 확인
        VolunteerWorkEntity workAfterApplication = volunteerWorkRepository.findById(largeWork.getId()).get();
        assertThat(workAfterApplication.getCurrentParticipants()).isEqualTo(300);

        // 300명의 학생이 신청을 취소
        List<VolunteerApplicationEntity> applications = volunteerApplicationRepository.findAllByVolunteerWorkId(largeWork.getId());
        assertThat(applications).hasSize(300);

        for (VolunteerApplicationEntity application : applications) {
            volunteerApplicationService.cancelApplication(application.getId(), application.getStudent().getGoogleId(), "전원 취소 테스트");
        }

        // Then
        VolunteerWorkEntity finalWork = volunteerWorkRepository.findById(largeWork.getId()).get();
        assertThat(finalWork.getCurrentParticipants()).isEqualTo(0);

        List<VolunteerApplicationEntity> finalApplications = volunteerApplicationRepository.findAllByVolunteerWorkId(largeWork.getId());
        assertThat(finalApplications).allMatch(a -> a.getStatus() == ApplicationStatus.CANCELLED);
    }




    @Test
    @DisplayName("동시성 봉사활동 신청 테스트 - 최대 인원 제한")
    void concurrentApplicationTest() throws InterruptedException {
        int maxParticipants = 10;
        VolunteerWorkEntity concurrentWork = VolunteerWorkEntity.builder()
                .workName("동시성 봉사활동")
                .maxParticipants(maxParticipants)
                .location("온라인")
                .description("동시성 테스트")
                .difficulty(WorkDifficulty.NORMAL)
                .status(WorkStatus.RECRUITING)
                .startTime(LocalDateTime.now())
                .build();
        concurrentWork.setTeacher(teacher.get(0));
        volunteerWorkRepository.save(concurrentWork);

        int numberOfStudents = 20; // Try to apply more students than maxParticipants
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfStudents);
        CountDownLatch latch = new CountDownLatch(numberOfStudents);

        for (int i = 0; i < numberOfStudents; i++) {
            UserEntity studentEntity = student.get(i);
            executorService.submit(() -> {
                try {
                    volunteerApplicationService.applyToVolunteer(concurrentWork.getId(), studentEntity.getGoogleId());
                } catch (Exception e) {
                    // Expected exceptions for exceeding max participants will be caught here
                    System.out.println("Application failed for student " + studentEntity.getName() + ": " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS); // Wait for all threads to complete
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        // Fetch the work again to get updated participant count
        VolunteerWorkEntity updatedWork = volunteerWorkRepository.findById(concurrentWork.getId()).get();
        List<VolunteerApplicationEntity> applications = volunteerApplicationRepository.findAllByVolunteerWorkId(updatedWork.getId());

        assertThat(updatedWork.getCurrentParticipants()).isEqualTo(maxParticipants);
        assertThat(applications.size()).isEqualTo(maxParticipants);
        assertThat(applications).allMatch(app -> app.getStatus() == ApplicationStatus.APPLIED);
    }
}