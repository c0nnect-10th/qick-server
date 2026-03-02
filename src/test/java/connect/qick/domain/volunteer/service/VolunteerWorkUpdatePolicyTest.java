package connect.qick.domain.volunteer.service;

import connect.qick.domain.point.service.PointService;
import connect.qick.domain.tcode.service.TCodeService;
import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.enums.UserStatus;
import connect.qick.domain.user.enums.UserType;
import connect.qick.domain.user.repository.UserRepository;
import connect.qick.domain.user.service.UserServiceImpl;
import connect.qick.domain.volunteer.dto.request.UpdateVolunteerWorkRequest;
import connect.qick.domain.volunteer.dto.response.VolunteerWorkResponse;
import connect.qick.domain.volunteer.entity.VolunteerWorkEntity;
import connect.qick.domain.volunteer.enums.WorkDifficulty;
import connect.qick.domain.volunteer.enums.WorkStatus;
import connect.qick.domain.volunteer.exception.VolunteerException;
import connect.qick.domain.volunteer.exception.VolunteerStatusCode;
import connect.qick.domain.volunteer.repository.VolunteerWorkRepository;
import connect.qick.global.security.jwt.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({VolunteerWorkService.class, UserServiceImpl.class, PointService.class})
class VolunteerWorkUpdatePolicyTest {

    @Autowired
    private VolunteerWorkService volunteerWorkService;
    @Autowired
    private VolunteerWorkRepository volunteerWorkRepository;
    @Autowired
    private UserRepository userRepository;

    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private TCodeService tCodeService;

    @Test
    @DisplayName("RECRUITING 상태에서 봉사활동 수정 가능")
    void updateAllowedOnlyWhenRecruiting() {
        UserEntity teacher = saveTeacher();
        VolunteerWorkEntity work = volunteerWorkRepository.save(
                VolunteerWorkEntity.builder()
                        .workName("초기 제목")
                        .maxParticipants(10)
                        .currentParticipants(3)
                        .location("초기 장소")
                        .description("초기 설명")
                        .difficulty(WorkDifficulty.EASY)
                        .status(WorkStatus.RECRUITING)
                        .startTime(LocalDateTime.now().plusHours(2))
                        .teacher(teacher)
                        .build()
        );

        UpdateVolunteerWorkRequest request = new UpdateVolunteerWorkRequest(
                "수정 제목",
                8,
                "수정 장소",
                "수정 설명",
                WorkDifficulty.HARD,
                LocalDateTime.now().plusHours(3)
        );

        VolunteerWorkResponse response = volunteerWorkService.updateVolunteerWork(
                work.getId(),
                teacher.getGoogleId(),
                request
        );

        assertThat(response.getWorkName()).isEqualTo("수정 제목");
        assertThat(response.getMaxParticipants()).isEqualTo(8);
        assertThat(response.getLocation()).isEqualTo("수정 장소");
        assertThat(response.getDifficulty()).isEqualTo(WorkDifficulty.HARD);
        assertThat(response.getStatus()).isEqualTo(WorkStatus.RECRUITING);
    }

    @Test
    @DisplayName("현재 참여 인원보다 작은 maxParticipants로는 수정 불가")
    void rejectLowerThanCurrentParticipants() {
        UserEntity teacher = saveTeacher();
        VolunteerWorkEntity work = volunteerWorkRepository.save(
                VolunteerWorkEntity.builder()
                        .workName("초기 제목")
                        .maxParticipants(10)
                        .currentParticipants(4)
                        .location("초기 장소")
                        .description("초기 설명")
                        .difficulty(WorkDifficulty.NORMAL)
                        .status(WorkStatus.RECRUITING)
                        .startTime(LocalDateTime.now().plusHours(2))
                        .teacher(teacher)
                        .build()
        );

        UpdateVolunteerWorkRequest request = new UpdateVolunteerWorkRequest(
                null,
                3,
                null,
                null,
                null,
                null
        );

        assertThatThrownBy(() -> volunteerWorkService.updateVolunteerWork(
                work.getId(),
                teacher.getGoogleId(),
                request
        ))
                .isInstanceOf(VolunteerException.class)
                .satisfies(ex -> {
                    VolunteerException ve = (VolunteerException) ex;
                    assertThat(ve.getStatusCode()).isEqualTo(VolunteerStatusCode.MAX_PARTICIPANTS_BELOW_CURRENT);
                });
    }

    @Test
    @DisplayName("ONGOING 상태에서는 봉사활동 수정 불가")
    void rejectWhenStatusIsOngoing() {
        UserEntity teacher = saveTeacher();
        VolunteerWorkEntity work = volunteerWorkRepository.save(
                VolunteerWorkEntity.builder()
                        .workName("초기 제목")
                        .maxParticipants(10)
                        .currentParticipants(4)
                        .location("초기 장소")
                        .description("초기 설명")
                        .difficulty(WorkDifficulty.NORMAL)
                        .status(WorkStatus.ONGOING)
                        .startTime(LocalDateTime.now().plusHours(2))
                        .teacher(teacher)
                        .build()
        );

        UpdateVolunteerWorkRequest request = new UpdateVolunteerWorkRequest(
                "수정",
                12,
                null,
                null,
                null,
                null
        );

        assertThatThrownBy(() -> volunteerWorkService.updateVolunteerWork(
                work.getId(),
                teacher.getGoogleId(),
                request
        ))
                .isInstanceOf(VolunteerException.class)
                .satisfies(ex -> {
                    VolunteerException ve = (VolunteerException) ex;
                    assertThat(ve.getStatusCode()).isEqualTo(VolunteerStatusCode.INVALID_WORK_STATUS);
                });
    }

    private UserEntity saveTeacher() {
        String seed = UUID.randomUUID().toString().substring(0, 8);
        return userRepository.save(
                UserEntity.builder()
                        .googleId("teacher-" + seed)
                        .email("teacher-" + seed + "@qick.test")
                        .name("teacher-" + seed)
                        .userType(UserType.TEACHER)
                        .userStatus(UserStatus.ACTIVE)
                        .build()
        );
    }
}
