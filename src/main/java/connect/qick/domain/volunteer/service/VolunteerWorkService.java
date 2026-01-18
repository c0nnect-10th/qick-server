package connect.qick.domain.volunteer.service;

import connect.qick.domain.point.service.PointService;
import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.service.UserService;
import connect.qick.domain.volunteer.dto.request.CreateVolunteerWorkRequest;
import connect.qick.domain.volunteer.dto.response.*;
import connect.qick.domain.volunteer.entity.VolunteerApplicationEntity;
import connect.qick.domain.volunteer.dto.response.CreateVolunteerWorkResponse;
import connect.qick.domain.volunteer.dto.response.VolunteerWorkResponse;
import connect.qick.domain.volunteer.dto.response.VolunteerWorkSummaryResponse;
import connect.qick.domain.volunteer.entity.VolunteerWorkEntity;
import connect.qick.domain.volunteer.enums.WorkStatus;
import connect.qick.domain.volunteer.exception.VolunteerException;
import connect.qick.domain.volunteer.exception.VolunteerStatusCode;
import connect.qick.domain.volunteer.repository.VolunteerWorkRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class VolunteerWorkService {
    private final VolunteerWorkRepository volunteerWorkRepository;
    private final UserService userService;
    private final PointService pointService;

    /**
     * 봉사활동 목록 조회
     * @return 봉사활동 요약 리스트 반환
     */
    public List<VolunteerWorkSummaryResponse> findAll() {
        return volunteerWorkRepository.findAllSummary();
    }

    //TODO: 선생님이 만든 봉사활동만 조회

    public List<VolunteerWorkSummaryResponse> findAllSummary(String googleId) {
        return volunteerWorkRepository.findAllOrderByApplications(googleId)
            .stream().map(w -> VolunteerWorkSummaryResponse.from(w))
            .toList(); //TODO: response 참가중인지 아닌지
    }

    /**
     * 특정 봉사활동 조회
     * @param id 봉사활동 id
     * @return 봉사활동 내용
     */
    public VolunteerWorkResponse findVolunteerWork(Long id) {
        return VolunteerWorkResponse.from(findById(id));
    }

    /**
     * 봉사활동 생성
     * @param googleId 유저(선생님) 구글 Id
     * @param request CreateVolunteerWorkRequest
     * @return 봉사활동 Id, 봉사활동 상태
     */
    public CreateVolunteerWorkResponse create(
            String googleId,
            CreateVolunteerWorkRequest request
    ) {
        UserEntity teacher =  userService.getUserByGoogleId(googleId);
        VolunteerWorkEntity work = VolunteerWorkEntity.createVolunteerWork(teacher, request);
        volunteerWorkRepository.save(work);

        return new CreateVolunteerWorkResponse(work.getId(), work.getStatus());
    }

    /**
     * 봉사활동 삭제
     * @param workId 봉사활동 Id
     * @param googleId 유저(선생님) 구글 Id
     */
    public void deleteVolunteerWork(Long workId, String googleId) {
        VolunteerWorkEntity work = volunteerWorkRepository.findByWorkId(workId)
            .orElseThrow(() -> new VolunteerException(VolunteerStatusCode.WORK_NOT_FOUND));

        work.cancelBy(googleId);
    }

    /**
     * 봉사활동 종료
     * @param workId 봉사활동 Id
     * @param attendedStudentIds 참석한 유저(학생) Id
     * @param googleId 유저(선생님) 구글 Id
     * @return CompleteVolunteerWorkResponse
     */
    public CompleteVolunteerWorkResponse completeVolunteerWork(
            Long workId,
            List<Long> attendedStudentIds,
            String googleId
    ) {
        // 봉사활동 조회
        VolunteerWorkEntity work = findById(workId);
        work.validateTeacher(googleId);
        work.validateCompletable();

        // 해당 봉사활동의 모든 신청 내역 조회
        List<VolunteerApplicationEntity> applications = work.getApplications();

        int attendedCount = 0;
        int noShowCount = 0;

        // 출석 체크 및 상태 업데이트
        for (VolunteerApplicationEntity application : applications) {
            if(!application.isApplied()) continue;

            boolean attended = attendedStudentIds.contains(application.getStudent().getId());
            application.markAttendance(attended);

            if (attended) {
                // 포인트 지급
                pointService.earnPoints(application.getStudent(), work);
                attendedCount++;
            } else {
                // 포인트 차감
                pointService.deductPoints(application.getStudent(), work);
                noShowCount++;
            }

        }

        // 봉사활동 상태를 COMPLETED로 변경
        work.complete();

        return new CompleteVolunteerWorkResponse(
                work.getId(),
                work.getWorkName(),
                attendedCount,
                noShowCount
        );
    }

    /**
     * 선생님이 생성한 봉사활동 목록 조회 (상태별)
     * @param googleId 유저(선생님) 구글 Id
     * @param status 봉사활동 상태
     * @return List<VolunteerWorkEntity>
     */
    public List<VolunteerWorkEntity> getMyVolunteerWorks(String googleId, WorkStatus status) {
        UserEntity teacher = userService.getUserByGoogleId(googleId);

        if (status == null) {
            return volunteerWorkRepository.findAllOrderByStatus(teacher.getId());
        }
        return volunteerWorkRepository.findByTeacherIdAndStatus(teacher.getId(), status);
    }

    /**
     * 특정 봉사를 신청한 모든 학생 목록 조회
     * @param workId 봉사활동 Id
     * @param googleId 유저(선생님) Id
     * @return List<ApplicationStudentResponse>
     */
    public List<ApplicationStudentResponse> getApplicationStudents(Long workId, String googleId) {
        // 봉사활동 조회
        VolunteerWorkEntity work = findById(workId);
        work.validateTeacher(googleId);

        // 신청자 목록 조회
        List<VolunteerApplicationEntity> applications = work.getApplications();
        return applications.stream()
                .filter(VolunteerApplicationEntity::isApplied)
                .map(ApplicationStudentResponse::from)
                .collect(Collectors.toList());
    }

    public VolunteerWorkEntity findById(Long id) {
        return volunteerWorkRepository.findById(id)
            .orElseThrow(() -> new VolunteerException(VolunteerStatusCode.WORK_NOT_FOUND));
    }

}
