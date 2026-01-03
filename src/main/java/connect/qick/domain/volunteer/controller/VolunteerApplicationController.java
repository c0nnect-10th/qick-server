package connect.qick.domain.volunteer.controller;

import connect.qick.domain.volunteer.dto.request.CancelApplicationRequest;
import connect.qick.domain.volunteer.dto.response.ApplicationResponse;
import connect.qick.domain.volunteer.dto.response.MyApplicationResponse;
import connect.qick.domain.volunteer.service.VolunteerApplicationService;
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
@RequestMapping("volunteer/application")
@Tag(name = "Volunteer Application", description = "봉사활동 신청 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class VolunteerApplicationController {

    private final VolunteerApplicationService applicationService;

    @PostMapping("/{workId}")
    @Operation(summary = "봉사활동 신청", description = "학생이 특정 봉사활동에 신청합니다. 선착순으로 처리됩니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "신청 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "신청 성공",
                                    value = """
                                    {
                                        "status": 200,
                                        "data": {
                                            "applicationId": 1,
                                            "workId": 5,
                                            "workName": "점심시간 교실 정리",
                                            "status": "APPLIED",
                                            "appliedAt": "2025-12-31T10:30:00",
                                            "cancelReason": null,
                                            "cancelledAt": null
                                        }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "봉사활동을 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "봉사활동 없음",
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
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이미 신청했거나 모집 마감",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "중복 신청",
                                            value = """
                                            {
                                                "status": 409,
                                                "error": {
                                                    "code": "ALREADY_APPLIED",
                                                    "message": "이미 신청한 봉사활동입니다.",
                                                    "timestamp": "2025-12-31T12:00:00.000000"
                                                }
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "모집 마감",
                                            value = """
                                            {
                                                "status": 409,
                                                "error": {
                                                    "code": "RECRUITMENT_FULL",
                                                    "message": "모집 인원이 마감되었습니다.",
                                                    "timestamp": "2025-12-31T12:00:00.000000"
                                                }
                                            }
                                            """
                                    )
                            }
                    )
            )
    })
    public ResponseEntity<ApiResponse<ApplicationResponse>> applyToVolunteer(
            @PathVariable Long workId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ApplicationResponse response = applicationService.applyToVolunteer(
                workId,
                userDetails.getGoogleId()
        );
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/{applicationId}")
    @Operation(summary = "봉사활동 신청 취소", description = "학생이 신청한 봉사활동을 취소합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "취소 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "취소 성공",
                                    value = """
                                    {
                                        "status": 200,
                                        "data": "봉사활동 신청이 취소되었습니다."
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "필수값 누락",
                                            value = """
                                            {
                                                "status": 400,
                                                "error": {
                                                    "code": "INVALID_ARGUMENT",
                                                    "message": "요청값이 유효하지 않습니다.",
                                                    "timestamp": "2025-12-31T12:00:00.000000",
                                                    "details": {
                                                        "cancelReason": "취소 사유는 필수입니다."
                                                    }
                                                }
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "취소 불가 상태",
                                            value = """
                                            {
                                                "status": 400,
                                                "error": {
                                                    "code": "CANNOT_CANCEL",
                                                    "message": "취소할 수 없는 상태입니다.",
                                                    "timestamp": "2025-12-31T12:00:00.000000"
                                                }
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "신청 내역을 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "신청 내역 없음",
                                    value = """
                                    {
                                        "status": 404,
                                        "error": {
                                            "code": "APPLICATION_NOT_FOUND",
                                            "message": "신청 내역을 찾을 수 없습니다.",
                                            "timestamp": "2025-12-31T12:00:00.000000"
                                        }
                                    }
                                    """
                            )
                    )
            )
    })
    public ResponseEntity<ApiResponse<String>> cancelApplication(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid CancelApplicationRequest request
    ) {
        applicationService.cancelApplication(
                applicationId,
                userDetails.getGoogleId(),
                request.cancelReason()
        );
        return ResponseEntity.ok(ApiResponse.ok("봉사활동 신청이 취소되었습니다."));
    }

    @GetMapping("/my")
    @Operation(summary = "내가 신청한 봉사활동 목록 조회", description = "학생이 신청한 모든 봉사활동 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "내 신청 목록 조회 성공",
                                    value = """
                                    {
                                        "status": 200,
                                        "data": [
                                            {
                                                "applicationId": 1,
                                                "workId": 5,
                                                "workName": "점심시간 교실 정리",
                                                "teacherName": "김선생",
                                                "location": "1-1 교실",
                                                "startTime": "2025-12-31T13:00:00",
                                                "difficulty": "EASY",
                                                "status": "APPLIED",
                                                "appliedAt": "2025-12-31T10:30:00"
                                            },
                                            {
                                                "applicationId": 2,
                                                "workId": 8,
                                                "workName": "체육대회 준비",
                                                "teacherName": "이선생",
                                                "location": "운동장",
                                                "startTime": "2026-01-02T09:00:00",
                                                "difficulty": "HARD",
                                                "status": "CANCELLED",
                                                "appliedAt": "2025-12-30T14:20:00"
                                            }
                                        ]
                                    }
                                    """
                            )
                    )
            )
    })
    public ResponseEntity<ApiResponse<List<MyApplicationResponse>>> getMyApplications(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<MyApplicationResponse> applications = applicationService.getMyApplications(
                userDetails.getGoogleId()
        );
        return ResponseEntity.ok(ApiResponse.ok(applications));
    }

    @GetMapping("/{applicationId}")
    @Operation(summary = "신청 상세 정보 조회", description = "특정 신청의 상세 정보를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "신청 상세 조회 성공 (APPLIED)",
                                            value = """
                                            {
                                                "status": 200,
                                                "data": {
                                                    "applicationId": 1,
                                                    "workId": 5,
                                                    "workName": "점심시간 교실 정리",
                                                    "status": "APPLIED",
                                                    "appliedAt": "2025-12-31T10:30:00",
                                                    "cancelReason": null,
                                                    "cancelledAt": null
                                                }
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "신청 상세 조회 성공 (CANCELLED)",
                                            value = """
                                            {
                                                "status": 200,
                                                "data": {
                                                    "applicationId": 2,
                                                    "workId": 8,
                                                    "workName": "체육대회 준비",
                                                    "status": "CANCELLED",
                                                    "appliedAt": "2025-12-30T14:20:00",
                                                    "cancelReason": "개인 사정으로 인한 취소",
                                                    "cancelledAt": "2025-12-31T09:15:00"
                                                }
                                            }
                                            """
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "신청 내역을 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "신청 내역 없음",
                                    value = """
                                    {
                                        "status": 404,
                                        "error": {
                                            "code": "APPLICATION_NOT_FOUND",
                                            "message": "신청 내역을 찾을 수 없습니다.",
                                            "timestamp": "2025-12-31T12:00:00.000000"
                                        }
                                    }
                                    """
                            )
                    )
            )
    })
    public ResponseEntity<ApiResponse<ApplicationResponse>> getApplicationDetail(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ApplicationResponse response = applicationService.getApplicationDetail(
                applicationId,
                userDetails.getGoogleId()
        );
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}