package connect.qick.domain.point.controller;

import connect.qick.domain.point.dto.response.PointHistoryResponse;
import connect.qick.domain.point.service.PointService;
import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.exception.UserException;
import connect.qick.domain.user.exception.UserStatusCode;
import connect.qick.domain.user.service.UserService;
import connect.qick.global.data.ApiResponse;
import connect.qick.global.data.ErrorResponse;
import connect.qick.global.security.entity.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/point")
@Tag(name = "Point", description = "포인트 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class PointController {

    private final PointService pointService;
    private final UserService userService;

    @GetMapping("/history")
    @Operation(summary = "내 포인트 내역 조회", description = "학생이 자신의 포인트 획득/차감 내역을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<List<PointHistoryResponse>>> getMyPointHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UserEntity student = userService.getUserByGoogleId(userDetails.getGoogleId())
                .orElseThrow(() -> new UserException(UserStatusCode.NOT_FOUND));

        List<PointHistoryResponse> history = pointService.getPointHistory(student.getId());

        return ResponseEntity.ok(ApiResponse.ok(history));
    }
}