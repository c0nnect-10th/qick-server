package connect.qick.domain.volunteer.service;

import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.enums.UserStatus;
import connect.qick.domain.user.enums.UserType;
import connect.qick.domain.user.repository.UserRepository;
import connect.qick.domain.user.service.UserService;
import connect.qick.domain.volunteer.enums.WorkDifficulty;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class VolunteerWorkServiceTest {

    @Autowired
    private VolunteerWorkService volunteerWorkService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("봉사활동 생성 시 사용자에게 봉사활동이 정상적으로 추가 확인")
    void createVolunteerWork() {
        UserEntity user = userRepository.save(
                UserEntity.builder()
                    .userType(UserType.TEACHER)
                    .userStatus(UserStatus.ACTIVE)
                    .googleId("1234")
                    .build()
            );

        String workName = "봉사제목1";
        int maxParticipants = 5;
        String location = "lo1";
        String description = "설명1";
        WorkDifficulty difficulty = WorkDifficulty.NORMAL;
        LocalDateTime startTime = LocalDateTime.now();
        String googleId = user.getGoogleId();

        volunteerWorkService.create(workName, maxParticipants, location, description, difficulty, startTime, googleId);


        assertThat(user.getVolunteerWorks().size())
                .isEqualTo(1);

    }

}