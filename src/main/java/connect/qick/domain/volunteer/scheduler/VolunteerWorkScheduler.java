package connect.qick.domain.volunteer.scheduler;

import connect.qick.domain.volunteer.entity.VolunteerWorkEntity;
import connect.qick.domain.volunteer.enums.WorkStatus;
import connect.qick.domain.volunteer.repository.VolunteerWorkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class VolunteerWorkScheduler {

    private final VolunteerWorkRepository volunteerWorkRepository;

    // 매 1분마다 실행되어 시작 시간이 된 모집중인 봉사활동을 ONGOING으로 바꿈
    @Scheduled(cron  = "0 * * * * *")
    @Transactional
    public void updateVolunteerWorkStatus() {
        LocalDateTime now = LocalDateTime.now();

        List<VolunteerWorkEntity> worksToStart = volunteerWorkRepository
                .findByStatusAndStartTimeBefore(WorkStatus.RECRUITING, now);

        if (!worksToStart.isEmpty()) {
            worksToStart.forEach(work -> {
                work.setStatus(WorkStatus.ONGOING);
                log.info("봉사활동 ID: {} 상태가 ONGOING으로 변경되었습니다.", work.getId());
            });

            volunteerWorkRepository.saveAll(worksToStart);
            log.info("총 {}개의 봉사활동이 ONGOING 상태로 변경되었습니다.", worksToStart.size());
        }

    }

}
