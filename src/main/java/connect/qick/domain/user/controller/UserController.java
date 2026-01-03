package connect.qick.domain.user.controller;

import connect.qick.domain.user.dto.request.SignupStudentRequest;
import connect.qick.domain.user.dto.request.SignupTeacherRequest;
import connect.qick.domain.user.dto.request.UpdateStudentRequest;
import connect.qick.domain.user.dto.request.UpdateTeacherRequest;
import connect.qick.domain.user.dto.response.UserResponse;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "사용자 정보 관리 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/")
    @Operation(
            summary = "내 정보 조회",
            description = "인증된 사용자의 정보를 조회합니다. 헤더에 JWT 토큰이 필요합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "정보 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "조회 성공 예시",
                                    value = """
                {
                    "status": 200,
                    "data": {
                        "name": "현승민",
                        "email": "hsm20090529@dgsw.hs.kr",
                        "totalPoints": 0,
                        "totalCount": 0,
                        "grade": 1,
                        "classNumber": 3,
                        "number": 17
                    }
                }
                """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (유효하지 않은 토큰)",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패 예시",
                                    value = """
                {
                    "status": 401,
                    "error": {
                        "code": "INVALID_JWT",
                        "message": "유효하지 않은 JWT입니다.",
                        "timestamp": "2025-12-31T21:08:32.949231"
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
                                    name = "사용자 없음 예시",
                                    value = """
                {
                    "status": 404,
                    "error": {
                        "code": "USER_NOT_FOUND",
                        "message": "해당 사용자를 찾을 수 없습니다.",
                        "timestamp": "2025-12-31T21:10:00.000000"
                    }
                }
                """
                            )
                    )
            )
    })
    public ResponseEntity<ApiResponse<UserResponse>> getUser(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                    userService.getUserInfo(userDetails.getGoogleId())
                )
        );
    }

    @PostMapping("/signup/student")
    @Operation(
            summary = "학생 회원가입",
            description = "신규 학생 사용자의 추가 정보를 등록합니다. 초기 인증 후 추가 정보 입력 단계에서 사용됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "회원가입 성공",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "회원가입 성공 예시",
                                    value = """
                {
                    "status": 200,
                    "data": "성공적으로 가입했습니다."
                }
                """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (입력값 누락 또는 형식 오류)",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "잘못된 요청 예시",
                                    value = """
                {
                     "status": 400,
                     "error": {
                         "code": "INVALID_CLASSROOM",
                         "message": "학반의 형식이 일치하지 않습니다.",
                         "timestamp": "2025-12-31T12:13:22.364191278"
                     }
                 }
                """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (유효하지 않은 토큰)",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패 예시",
                                    value = """
                {
                    "status": 401,
                    "error": {
                        "code": "INVALID_JWT",
                        "message": "유효하지 않은 JWT입니다.",
                        "timestamp": "2025-12-31T21:08:32.949231"
                    }
                }
                """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "이미 가입된 사용자",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "중복 가입 예시",
                                    value = """
                {
                    "status": 409,
                    "error": {
                        "code": "ALREADY_EXISTS",
                        "message": "이미 가입된 사용자입니다.",
                        "timestamp": "2025-12-31T12:10:25.968561449"
                    }
                }
                """
                            )
                    )
            )
    })
    public ResponseEntity<ApiResponse<?>> signupUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid SignupStudentRequest request
    ) {
        userService.signupStudent(userDetails.getGoogleId(), request);
        return ResponseEntity.ok(
                ApiResponse.ok("성공적으로 가입했습니다.")
        );
    }

    @PatchMapping("/student")
    @Operation(
            summary = "학생 정보 수정",
            description = "학생 사용자의 정보를 수정합니다. 요청 바디에 포함된 필드만 선택적으로 수정됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "학생 정보 수정 성공",
                                    value = """
                {
                    "status": 200,
                    "data": {
                        "name": "김길동",
                        "email": "gildong@qick.com",
                        "totalPoints": 15,
                        "totalCount": 5,
                        "grade": 2,
                        "classNumber": 3,
                        "number": 14
                    },
                    "error": null
                }
                """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (입력값 형식 오류)",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "유효성 검사 실패 예시",
                                    value = """
                {
                    "status": 400,
                    "error": {
                        "code": "INVALID_ARGUMENT",
                        "message": "학반의 형식이 일치하지 않습니다.",
                        "timestamp": "2025-12-31T12:13:22.364191278"
                    }
                }
                """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (유효하지 않은 토큰)",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패 예시",
                                    value = """
                {
                    "status": 401,
                    "error": {
                        "code": "INVALID_JWT",
                        "message": "유효하지 않은 JWT입니다.",
                        "timestamp": "2025-12-31T21:08:32.949231"
                    }
                }
                """
                            )
                    )
            )
    })
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UpdateStudentRequest request
        ) {
        UserResponse updatedUser = userService.updateStudent(userDetails.getGoogleId(), request);
        return ResponseEntity.ok(ApiResponse.ok(updatedUser));
    }

    @DeleteMapping("/")
    @Operation(summary = "회원 탈퇴", description = "인증된 사용자의 계정을 삭제합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "회원 탈퇴 성공",
                                    value = "{\"status\":200,\"data\":\"계정이 성공적으로 삭제되었습니다.\",\"error\":null}"
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (유효하지 않은 토큰)",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패 예시",
                                    value = """
                {
                    "status": 401,
                    "error": {
                        "code": "INVALID_JWT",
                        "message": "유효하지 않은 JWT입니다.",
                        "timestamp": "2025-12-31T21:08:32.949231"
                    }
                }
                """
                            )
                    )
            )
    })
    public ResponseEntity<ApiResponse<String>> deleteUser(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        userService.deleteUser(userDetails.getGoogleId());
        return ResponseEntity.ok(ApiResponse.ok("계정이 성공적으로 삭제되었습니다."));
    }
}
