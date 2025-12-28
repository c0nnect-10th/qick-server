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
    @Operation(summary = "봉사활동 신청", description = "학생이 특정 봉사활동에 신청합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "신청 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "봉사활동을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 신청했거나 모집 마감",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "취소 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "신청 내역을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "신청 내역을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
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