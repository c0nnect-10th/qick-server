package connect.qick.global.scheduler;

import connect.qick.domain.volunteer.entity.VolunteerApplicationEntity;
import connect.qick.domain.volunteer.entity.VolunteerWorkEntity;
import connect.qick.domain.volunteer.enums.ApplicationStatus;
import connect.qick.domain.volunteer.enums.WorkStatus;
import connect.qick.domain.volunteer.repository.VolunteerApplicationRepository;
import connect.qick.domain.volunteer.repository.VolunteerWorkRepository;
import connect.qick.global.util.PushAlarmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSchedulerService {

    private final VolunteerWorkRepository volunteerWorkRepository;
    private final VolunteerApplicationRepository volunteerApplicationRepository;
    private final PushAlarmUtil pushAlarmUtil;

    @Scheduled(cron = "0 * * * * ?")
    public void sendVolunteerReminders() {
        LocalDateTime now = LocalDateTime.now();

        // 10분 전 알림
        LocalDateTime tenMinutesFromNow = now.plusMinutes(10);
        List<VolunteerWorkEntity> worksStartingIn10Min = volunteerWorkRepository.findByStatusAndStartTimeBetween(
                WorkStatus.RECRUITING,
                tenMinutesFromNow.withSecond(0).withNano(0),
                tenMinutesFromNow.withSecond(59).withNano(999999999)
        );

        for (VolunteerWorkEntity work : worksStartingIn10Min) {
            sendReminderForWork(work, 10);
        }

        // 5분 전 알림
        LocalDateTime fiveMinutesFromNow = now.plusMinutes(5);
        List<VolunteerWorkEntity> worksStartingIn5Min = volunteerWorkRepository.findByStatusAndStartTimeBetween(
                WorkStatus.RECRUITING,
                fiveMinutesFromNow.withSecond(0).withNano(0),
                fiveMinutesFromNow.withSecond(59).withNano(999999999)
        );

        for (VolunteerWorkEntity work : worksStartingIn5Min) {
            sendReminderForWork(work, 5);
        }
    }

    private void sendReminderForWork(VolunteerWorkEntity work, int minutesBefore) {
        List<VolunteerApplicationEntity> applications = volunteerApplicationRepository.findAllByVolunteerWorkAndStatus(
                work, ApplicationStatus.APPLIED);

        if (applications.isEmpty()) {
            return;
        }

        List<String> fcmTokens = applications.stream()
                .map(app -> app.getStudent().getFcmToken())
                .filter(token -> token != null && !token.isEmpty())
                .collect(Collectors.toList());

        if (fcmTokens.isEmpty()) {
            log.info("봉사 ID {}에 대한 {}분 전 알림: 유효한 FCM 토큰을 가진 학생이 없습니다.", work.getId(), minutesBefore);
            return;
        }

        String title = String.format("'%s' 봉사활동 시작 %d분 전", work.getWorkName(), minutesBefore);
        String body = String.format("활동 장소는 '%s'입니다. 늦지 않게 준비해주세요!", work.getLocation());

        log.info("{}분 전 알림 전송: 봉사 ID: {}, 대상 학생 수: {}", minutesBefore, work.getId(), fcmTokens.size());
        pushAlarmUtil.sendMulticast(fcmTokens, title, body);
    }
}
