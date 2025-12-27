package connect.qick.domain.volunteer.controller;

import connect.qick.domain.volunteer.dto.request.CreateVolunteerWorkRequest;
import connect.qick.domain.volunteer.dto.response.CreateVolunteerWorkResponse;
import connect.qick.domain.volunteer.dto.response.VolunteerWorkResponse;
import connect.qick.domain.volunteer.dto.response.VolunteerWorkSummaryResponse;
import connect.qick.domain.volunteer.service.VolunteerWorkService;
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
@RequestMapping("volunteer")
@Tag(name = "Volunteer", description = "심부름 조회 및 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class VolunteerWorkController {

    private final VolunteerWorkService volunteerWorkService;

    @GetMapping("/")
    @Operation(summary = "모든 심부름 목록 조회", description = "존재하는 모든 심부름 목록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "심부름을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 형식",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자(교사)를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
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
                                request.start_time(),
                                userDetails.getGoogleId())
                )
        );
    }

    @DeleteMapping("/delete/{workId}")
    @Operation(summary = "심부름 삭제", description = "심부름을 생성한 교사가 해당 심부름을 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "삭제 권한 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "심부름을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
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
}
