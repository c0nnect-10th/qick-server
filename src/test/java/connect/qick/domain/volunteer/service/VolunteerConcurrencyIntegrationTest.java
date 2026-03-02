package connect.qick.domain.volunteer.service;

import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.enums.UserStatus;
import connect.qick.domain.user.enums.UserType;
import connect.qick.domain.user.repository.UserRepository;
import connect.qick.domain.user.service.UserService;
import connect.qick.domain.user.service.UserServiceImpl;
import connect.qick.domain.tcode.service.TCodeService;
import connect.qick.domain.volunteer.entity.VolunteerApplicationEntity;
import connect.qick.domain.volunteer.entity.VolunteerWorkEntity;
import connect.qick.domain.volunteer.enums.ApplicationStatus;
import connect.qick.domain.volunteer.enums.WorkDifficulty;
import connect.qick.domain.volunteer.enums.WorkStatus;
import connect.qick.domain.volunteer.repository.VolunteerApplicationRepository;
import connect.qick.domain.volunteer.repository.VolunteerWorkRepository;
import connect.qick.global.security.jwt.JwtProvider;
import connect.qick.global.util.PushAlarmUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({VolunteerApplicationService.class, UserServiceImpl.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class VolunteerConcurrencyIntegrationTest {

    @Autowired
    private VolunteerApplicationService volunteerApplicationService;
    @Autowired
    private VolunteerWorkRepository volunteerWorkRepository;
    @Autowired
    private VolunteerApplicationRepository volunteerApplicationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @MockBean
    private PushAlarmUtil pushAlarmUtil;
    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private TCodeService tCodeService;

    @Test
    @DisplayName("동시 신청 시 최대 인원을 초과하지 않는다")
    void concurrentApplicationsDoNotExceedMaxParticipants() throws InterruptedException {
        UserEntity teacher = createTeacher("teacher-max");
        VolunteerWorkEntity work = createWork(teacher, 5, "max-limit");
        List<UserEntity> students = createStudents(30, "student-max");

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(students.size());
        ExecutorService pool = Executors.newFixedThreadPool(20);

        for (UserEntity student : students) {
            pool.submit(() -> {
                try {
                    start.await();
                    volunteerApplicationService.applyToVolunteer(work.getId(), student.getGoogleId());
                } catch (Exception ignored) {
                    // 모집 마감/동시성 충돌로 일부 신청 실패는 정상이다.
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        boolean allTasksCompleted = done.await(20, TimeUnit.SECONDS);
        pool.shutdown();
        boolean poolTerminated = pool.awaitTermination(1, TimeUnit.MINUTES);

        assertThat(allTasksCompleted).isTrue();
        assertThat(poolTerminated).isTrue();

        VolunteerWorkEntity updated = volunteerWorkRepository.findById(work.getId()).orElseThrow();
        List<VolunteerApplicationEntity> applications =
                volunteerApplicationRepository.findAllByVolunteerWorkId(work.getId());
        long appliedCount = applications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.APPLIED)
                .count();

        assertThat(updated.getCurrentParticipants()).isEqualTo(5);
        assertThat(appliedCount).isEqualTo(5);
    }

    @Test
    @DisplayName("선생님 탈퇴와 신청이 동시에 일어나도 활성 신청이 남지 않는다")
    void teacherWithdrawalAndConcurrentApplicationsLeavesNoApplied() throws InterruptedException {
        UserEntity teacher = createTeacher("teacher-withdraw");
        VolunteerWorkEntity work = createWork(teacher, 10, "withdraw-race");
        List<UserEntity> students = createStudents(20, "student-withdraw");

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(students.size() + 1);
        ExecutorService pool = Executors.newFixedThreadPool(24);
        AtomicReference<Throwable> withdrawalError = new AtomicReference<>();

        for (UserEntity student : students) {
            pool.submit(() -> {
                try {
                    start.await();
                    volunteerApplicationService.applyToVolunteer(work.getId(), student.getGoogleId());
                } catch (Exception ignored) {
                    // 탈퇴/취소 타이밍에 따른 실패는 정상이다.
                } finally {
                    done.countDown();
                }
            });
        }

        pool.submit(() -> {
            try {
                start.await();
                userService.deleteUser(teacher.getGoogleId());
            } catch (Throwable e) {
                withdrawalError.set(e);
            } finally {
                done.countDown();
            }
        });

        start.countDown();
        boolean allTasksCompleted = done.await(20, TimeUnit.SECONDS);
        pool.shutdown();
        boolean poolTerminated = pool.awaitTermination(1, TimeUnit.MINUTES);

        assertThat(allTasksCompleted).isTrue();
        assertThat(poolTerminated).isTrue();

        VolunteerWorkEntity updatedWork = volunteerWorkRepository.findById(work.getId()).orElseThrow();
        List<VolunteerApplicationEntity> applications =
                volunteerApplicationRepository.findAllByVolunteerWorkId(work.getId());
        long appliedCount = applications.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.APPLIED)
                .count();

        UserEntity withdrawnTeacher = userRepository.findById(teacher.getId()).orElseThrow();

        assertThat(withdrawalError.get()).isNull();
        assertThat(updatedWork.getStatus()).isEqualTo(WorkStatus.CANCELLED);
        assertThat(updatedWork.getCurrentParticipants()).isEqualTo(0);
        assertThat(appliedCount).isZero();
        assertThat(withdrawnTeacher.getUserStatus()).isEqualTo(UserStatus.DELETED);
    }

    private UserEntity createTeacher(String prefix) {
        String seed = UUID.randomUUID().toString().substring(0, 8);
        return userRepository.save(
                UserEntity.builder()
                        .userType(UserType.TEACHER)
                        .userStatus(UserStatus.ACTIVE)
                        .googleId(prefix + "-" + seed)
                        .email(prefix + "-" + seed + "@qick.test")
                        .name("teacher-" + seed)
                        .build()
        );
    }

    private VolunteerWorkEntity createWork(UserEntity teacher, int maxParticipants, String prefix) {
        VolunteerWorkEntity work = VolunteerWorkEntity.builder()
                .workName(prefix + "-work")
                .maxParticipants(maxParticipants)
                .location("online")
                .description("concurrency-test")
                .difficulty(WorkDifficulty.NORMAL)
                .status(WorkStatus.RECRUITING)
                .startTime(LocalDateTime.now().plusHours(1))
                .build();
        work.setTeacher(teacher);
        return volunteerWorkRepository.save(work);
    }

    private List<UserEntity> createStudents(int count, String prefix) {
        List<UserEntity> students = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String seed = UUID.randomUUID().toString().substring(0, 8);
            students.add(userRepository.save(
                    UserEntity.builder()
                            .userType(UserType.STUDENT)
                            .userStatus(UserStatus.ACTIVE)
                            .googleId(prefix + "-" + i + "-" + seed)
                            .email(prefix + "-" + i + "-" + seed + "@qick.test")
                            .name("student-" + i)
                            .build()
            ));
        }
        return students;
    }
}
