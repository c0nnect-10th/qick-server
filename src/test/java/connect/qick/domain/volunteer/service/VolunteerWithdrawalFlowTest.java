package connect.qick.domain.volunteer.service;

import connect.qick.domain.point.repository.PointHistoryRepository;
import connect.qick.domain.point.service.PointService;
import connect.qick.domain.tcode.service.TCodeService;
import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.enums.UserStatus;
import connect.qick.domain.user.enums.UserType;
import connect.qick.domain.user.repository.UserRepository;
import connect.qick.domain.user.service.UserService;
import connect.qick.domain.user.service.UserServiceImpl;
import connect.qick.domain.volunteer.dto.response.CompleteVolunteerWorkResponse;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({
        VolunteerApplicationService.class,
        VolunteerWorkService.class,
        UserServiceImpl.class,
        PointService.class
})
class VolunteerWithdrawalFlowTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VolunteerWorkRepository volunteerWorkRepository;
    @Autowired
    private VolunteerApplicationRepository volunteerApplicationRepository;
    @Autowired
    private VolunteerApplicationService volunteerApplicationService;
    @Autowired
    private VolunteerWorkService volunteerWorkService;
    @Autowired
    private UserService userService;
    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @MockBean
    private PushAlarmUtil pushAlarmUtil;
    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private TCodeService tCodeService;

    @Test
    @DisplayName("신청 후 탈퇴 + ONGOING 중 탈퇴 + 탈퇴 유저 ID로 complete 요청 시 탈퇴 유저는 무시된다")
    void withdrawAfterApplyThenCompleteWithWithdrawnUserId() {
        UserEntity teacher = saveTeacher("teacher");
        UserEntity withdrawnStudent = saveStudent("withdrawn");
        UserEntity activeStudent = saveStudent("active");

        VolunteerWorkEntity work = VolunteerWorkEntity.builder()
                .workName("탈퇴 플로우 테스트")
                .maxParticipants(5)
                .currentParticipants(0)
                .location("테스트 장소")
                .description("탈퇴 플로우")
                .difficulty(WorkDifficulty.NORMAL)
                .status(WorkStatus.RECRUITING)
                .startTime(LocalDateTime.now().plusHours(1))
                .build();
        work.setTeacher(teacher);
        volunteerWorkRepository.save(work);

        // 신청 후 탈퇴 시나리오를 만들기 위해 두 학생 모두 신청
        volunteerApplicationService.applyToVolunteer(work.getId(), withdrawnStudent.getGoogleId());
        volunteerApplicationService.applyToVolunteer(work.getId(), activeStudent.getGoogleId());

        VolunteerWorkEntity recruitingWork = volunteerWorkRepository.findById(work.getId()).orElseThrow();
        assertThat(recruitingWork.getCurrentParticipants()).isEqualTo(2);

        // ONGOING 전환 후 학생 탈퇴
        recruitingWork.setStatus(WorkStatus.ONGOING);
        userService.deleteUser(withdrawnStudent.getGoogleId());

        UserEntity deletedStudent = userRepository.findById(withdrawnStudent.getId()).orElseThrow();
        assertThat(deletedStudent.getUserStatus()).isEqualTo(UserStatus.DELETED);

        List<VolunteerApplicationEntity> applicationsAfterWithdrawal =
                volunteerApplicationRepository.findAllByVolunteerWorkId(work.getId());
        VolunteerApplicationEntity deletedStudentApplication = applicationsAfterWithdrawal.stream()
                .filter(a -> a.getStudent().getId().equals(withdrawnStudent.getId()))
                .findFirst()
                .orElseThrow();
        VolunteerApplicationEntity activeStudentApplication = applicationsAfterWithdrawal.stream()
                .filter(a -> a.getStudent().getId().equals(activeStudent.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(deletedStudentApplication.getStatus()).isEqualTo(ApplicationStatus.CANCELLED);
        assertThat(activeStudentApplication.getStatus()).isEqualTo(ApplicationStatus.APPLIED);

        // 탈퇴한 유저 id를 complete 요청의 출석 목록에 넣어도 APPLIED 대상만 처리되어야 함
        CompleteVolunteerWorkResponse response = volunteerWorkService.completeVolunteerWork(
                work.getId(),
                List.of(withdrawnStudent.getId(), activeStudent.getId()),
                teacher.getGoogleId()
        );

        VolunteerWorkEntity completedWork = volunteerWorkRepository.findById(work.getId()).orElseThrow();
        List<VolunteerApplicationEntity> finalApplications =
                volunteerApplicationRepository.findAllByVolunteerWorkId(work.getId());
        VolunteerApplicationEntity finalDeletedStudentApplication = finalApplications.stream()
                .filter(a -> a.getStudent().getId().equals(withdrawnStudent.getId()))
                .findFirst()
                .orElseThrow();
        VolunteerApplicationEntity finalActiveStudentApplication = finalApplications.stream()
                .filter(a -> a.getStudent().getId().equals(activeStudent.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(response.getAttendedCount()).isEqualTo(1);
        assertThat(response.getNoShowCount()).isEqualTo(0);
        assertThat(completedWork.getStatus()).isEqualTo(WorkStatus.COMPLETED);
        assertThat(finalDeletedStudentApplication.getStatus()).isEqualTo(ApplicationStatus.CANCELLED);
        assertThat(finalActiveStudentApplication.getStatus()).isEqualTo(ApplicationStatus.COMPLETED);
        assertThat(pointHistoryRepository.findAllByStudentId(activeStudent.getId())).hasSize(1);
        assertThat(pointHistoryRepository.findAllByStudentId(withdrawnStudent.getId())).isEmpty();
    }

    private UserEntity saveTeacher(String prefix) {
        String seed = UUID.randomUUID().toString().substring(0, 8);
        return userRepository.save(
                UserEntity.builder()
                        .googleId(prefix + "-" + seed)
                        .email(prefix + "-" + seed + "@qick.test")
                        .name("teacher-" + seed)
                        .userType(UserType.TEACHER)
                        .userStatus(UserStatus.ACTIVE)
                        .build()
        );
    }

    private UserEntity saveStudent(String prefix) {
        String seed = UUID.randomUUID().toString().substring(0, 8);
        return userRepository.save(
                UserEntity.builder()
                        .googleId(prefix + "-" + seed)
                        .email(prefix + "-" + seed + "@qick.test")
                        .name("student-" + seed)
                        .userType(UserType.STUDENT)
                        .userStatus(UserStatus.ACTIVE)
                        .build()
        );
    }
}
