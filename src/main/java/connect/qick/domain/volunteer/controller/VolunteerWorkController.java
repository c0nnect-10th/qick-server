package connect.qick.domain.volunteer.controller;

import connect.qick.domain.volunteer.dto.request.CompleteVolunteerWorkRequest;
import connect.qick.domain.volunteer.dto.request.CreateVolunteerWorkRequest;
import connect.qick.domain.volunteer.dto.response.*;
import connect.qick.domain.volunteer.entity.VolunteerWorkEntity;
import connect.qick.domain.volunteer.enums.WorkStatus;
import connect.qick.domain.volunteer.service.VolunteerWorkService;
import connect.qick.global.data.ApiResponse;
import connect.qick.global.data.ErrorResponse;
import connect.qick.global.security.entity.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("volunteer")
@Tag(name = "Volunteer", description = "심부름 조회 및 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class VolunteerWorkController {

    private final VolunteerWorkService volunteerWorkService;

    @GetMapping("/")
    @Operation(summary = "모든 심부름 목록 조회", description = "모집 중인 모든 심부름 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "심부름 목록 조회 성공",
                                    value = """
                                    {
                                        "status": 200,
                                        "data": [
                                            {
                                                "id": 1,
                                                "workName": "점심시간 교실 정리",
                                                "location": "1-1 교실",
                                                "teacherName": "김선생",
                                                "maxParticipants": 5,
                                                "currentParticipants": 2
                                            },
                                            {
                                                "id": 2,
                                                "workName": "체육대회 준비",
                                                "location": "운동장",
                                                "teacherName": "이선생",
                                                "maxParticipants": 10,
                                                "currentParticipants": 7
                                            }
                                        ]
                                    }
                                    """
                            )
                    )
            )
    })
    public ResponseEntity<ApiResponse<List<VolunteerWorkSummaryResponse>>> getVolunteerWorks() {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        volunteerWorkService.findAll()
                )
        );
    }

    @GetMapping("/{workId}")
    @Operation(summary = "특정 심부름 상세 조회", description = "ID에 해당하는 심부름의 상세 정보를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "심부름 상세 조회 성공",
                                    value = """
                                    {
                                        "status": 200,
                                        "data": {
                                            "id": 1,
                                            "workName": "점심시간 교실 정리",
                                            "difficulty": "EASY",
                                            "teacherName": "김선생",
                                            "location": "1-1 교실",
                                            "start_time": "2025-12-31T13:00:00",
                                            "description": "점심시간에 교실을 깨끗하게 정리합니다.",
                                            "maxParticipants": 5,
                                            "currentParticipants": 2,
                                            "status": "RECRUITING"
                                        }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "심부름을 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "심부름 없음",
                                    value = """
                                    {
                                        "status": 404,
                                        "error": {
                                            "code": "WORK_NOT_FOUND",
                                            "message": "봉사활동이 존재하지 않습니다.",
                                            "timestamp": "2025-12-31T12:00:00.000000"
                                        }
                                    }
                                    """
                            )
                    )
            )
    })
    public ResponseEntity<ApiResponse<VolunteerWorkResponse>> getVolunteerWork(@PathVariable Long workId) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        volunteerWorkService.findVolunteerWork(workId)
                )
        );
    }

    @PostMapping("/create")
    @Operation(summary = "새로운 심부름 생성", description = "교사 사용자가 새로운 심부름을 생성합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "심부름 생성 성공",
                                    value = """
                                    {
                                        "status": 200,
                                        "data": {
                                            "id": 1,
                                            "status": "RECRUITING"
                                        }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 형식",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "필수값 누락",
                                    value = """
                                    {
                                        "status": 400,
                                        "error": {
                                            "code": "INVALID_ARGUMENT",
                                            "message": "요청값이 유효하지 않습니다.",
                                            "timestamp": "2025-12-31T12:00:00.000000",
                                            "details": {
                                                "name": "이름은 필수입니다.",
                                                "maxParticipants": "모집 인원은 필수입니다."
                                            }
                                        }
                                    }
                                    """
                            )
                    )
            )
    })
    public ResponseEntity<ApiResponse<CreateVolunteerWorkResponse>> createVolunteerWork(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid CreateVolunteerWorkRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        volunteerWorkService.create(
                                request.name(),
                                request.maxParticipants(),
                                request.location(),
                                request.description(),
                                request.difficulty(),
                                request.startTime(),
                                userDetails.getGoogleId())
                )
        );
    }

    @DeleteMapping("/delete/{workId}")
    @Operation(summary = "심부름 삭제", description = "심부름을 생성한 교사가 해당 심부름을 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "삭제 성공",
                                    value = """
                                    {
                                        "status": 200,
                                        "data": "봉사활동이 정상적으로 삭제되었습니다."
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "삭제 권한 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "권한 없음",
                                    value = """
                                    {
                                        "status": 403,
                                        "error": {
                                            "code": "ACCESS_DENIED",
                                            "message": "접근 권한이 없습니다.",
                                            "timestamp": "2025-12-31T12:00:00.000000"
                                        }
                                    }
                                    """
                            )
                    )
            )
    })
    public ResponseEntity<ApiResponse<String>> deleteVolunteerWork(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long workId
    ) {
        volunteerWorkService.deleteVolunteerWork(workId, userDetails.getGoogleId());
        return ResponseEntity.ok(
                ApiResponse.ok("봉사활동이 정상적으로 삭제되었습니다.")
        );
    }

    @GetMapping("/{workId}/applications")
    @Operation(
            summary = "봉사활동 신청자 목록 조회",
            description = "교사가 자신이 생성한 봉사활동의 신청자 목록을 조회합니다. 출석 체크를 위해 사용됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "신청자 목록 조회 성공",
                                    value = """
                                    {
                                        "status": 200,
                                        "data": [
                                            {
                                                "applicationId": 1,
                                                "studentId": 5,
                                                "studentName": "홍길동",
                                                "grade": 2,
                                                "classNumber": 3,
                                                "number": 14,
                                                "status": "APPLIED",
                                                "appliedAt": "2025-12-31T10:30:00"
                                            },
                                            {
                                                "applicationId": 2,
                                                "studentId": 8,
                                                "studentName": "김철수",
                                                "grade": 2,
                                                "classNumber": 3,
                                                "number": 15,
                                                "status": "APPLIED",
                                                "appliedAt": "2025-12-31T10:35:00"
                                            }
                                        ]
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "권한 없음",
                                    value = """
                                    {
                                        "status": 403,
                                        "error": {
                                            "code": "ACCESS_DENIED",
                                            "message": "접근 권한이 없습니다.",
                                            "timestamp": "2025-12-31T12:00:00.000000"
                                        }
                                    }
                                    """
                            )
                    )
            )
    })
    public ResponseEntity<ApiResponse<List<ApplicationStudentResponse>>> getApplicationStudents(
            @PathVariable Long workId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<ApplicationStudentResponse> students = volunteerWorkService.getApplicationStudents(
                workId,
                userDetails.getGoogleId()
        );

        return ResponseEntity.ok(ApiResponse.ok(students));
    }

    @PostMapping("/{workId}/complete")
    @Operation(summary = "봉사활동 완료 처리", description = "교사가 봉사활동을 완료 처리하고 출석 체크를 합니다. 참여한 학생은 포인트를 획득하고, 미참여 학생은 포인트가 차감됩니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "완료 처리 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "완료 처리 성공",
                                    value = """
                                    {
                                        "status": 200,
                                        "data": {
                                            "workId": 1,
                                            "workName": "점심시간 교실 정리",
                                            "attendedCount": 3,
                                            "noShowCount": 2
                                        }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 상태 (ONGOING이 아님)",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "상태 오류",
                                    value = """
                                    {
                                        "status": 400,
                                        "error": {
                                            "code": "INVALID_WORK_STATUS",
                                            "message": "봉사활동의 상태가 올바르지 않습니다.",
                                            "timestamp": "2025-12-31T12:00:00.000000"
                                        }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "권한 없음",
                                    value = """
                                    {
                                        "status": 403,
                                        "error": {
                                            "code": "ACCESS_DENIED",
                                            "message": "접근 권한이 없습니다.",
                                            "timestamp": "2025-12-31T12:00:00.000000"
                                        }
                                    }
                                    """
                            )
                    )
            )
    })
    public ResponseEntity<ApiResponse<CompleteVolunteerWorkResponse>> completeVolunteerWork(
            @PathVariable Long workId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid CompleteVolunteerWorkRequest request
    ) {
        CompleteVolunteerWorkResponse response = volunteerWorkService.completeVolunteerWork(
                workId,
                request.attendedStudentIds(),
                userDetails.getGoogleId()
        );

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/my")
    @Operation(summary = "내가 생성한 봉사활동 목록 조회", description = "교사가 생성한 봉사활동 목록을 조회합니다. 상태별 필터링 가능합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "내 봉사활동 목록 조회 성공",
                                    value = """
                                    {
                                        "status": 200,
                                        "data": [
                                            {
                                                "id": 1,
                                                "workName": "점심시간 교실 정리",
                                                "difficulty": "EASY",
                                                "teacherName": "김선생",
                                                "location": "1-1 교실",
                                                "start_time": "2025-12-31T13:00:00",
                                                "description": "점심시간에 교실을 깨끗하게 정리합니다.",
                                                "maxParticipants": 5,
                                                "currentParticipants": 5,
                                                "status": "COMPLETED"
                                            }
                                        ]
                                    }
                                    """
                            )
                    )
            )
    })
    public ResponseEntity<ApiResponse<List<VolunteerWorkResponse>>> getMyVolunteerWorks(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) WorkStatus status
    ) {
        List<VolunteerWorkEntity> works = volunteerWorkService.getMyVolunteerWorks(
                userDetails.getGoogleId(),
                status
        );

        List<VolunteerWorkResponse> response = works.stream()
                .map(VolunteerWorkResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}