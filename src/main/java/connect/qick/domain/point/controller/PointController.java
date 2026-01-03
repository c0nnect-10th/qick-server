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
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "포인트 내역 조회 성공",
                                    value = """
                                    {
                                        "status": 200,
                                        "data": [
                                            {
                                                "id": 5,
                                                "pointType": "EARN",
                                                "points": 100,
                                                "reason": "'체육대회 준비' 봉사활동 완료",
                                                "workId": 2,
                                                "workName": "체육대회 준비",
                                                "createdAt": "2025-12-31T15:30:00"
                                            },
                                            {
                                                "id": 4,
                                                "pointType": "DEDUCT",
                                                "points": 60,
                                                "reason": "'도서관 정리' 봉사활동 미참여",
                                                "workId": 3,
                                                "workName": "도서관 정리",
                                                "createdAt": "2025-12-30T14:00:00"
                                            },
                                            {
                                                "id": 3,
                                                "pointType": "EARN",
                                                "points": 40,
                                                "reason": "'점심시간 교실 정리' 봉사활동 완료",
                                                "workId": 1,
                                                "workName": "점심시간 교실 정리",
                                                "createdAt": "2025-12-29T13:30:00"
                                            }
                                        ]
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    value = """
                                    {
                                        "status": 401,
                                        "error": {
                                            "code": "UNAUTHORIZED",
                                            "message": "인증이 필요합니다.",
                                            "timestamp": "2025-12-31T12:00:00.000000"
                                        }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "사용자 없음",
                                    value = """
                                    {
                                        "status": 404,
                                        "error": {
                                            "code": "NOT_FOUND",
                                            "message": "유저가 존재하지 않습니다.",
                                            "timestamp": "2025-12-31T12:00:00.000000"
                                        }
                                    }
                                    """
                            )
                    )
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