package connect.qick.domain.auth.controller;

import connect.qick.domain.auth.dto.request.IdTokenRequest;
import connect.qick.domain.auth.dto.request.RefreshRequest;
import connect.qick.domain.auth.dto.response.LoginResponse;
import connect.qick.domain.auth.dto.response.RefreshResponse;
import connect.qick.domain.auth.service.AuthService;
import connect.qick.global.data.ApiResponse;
import connect.qick.global.data.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 및 토큰 관리 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary="Google OAuth2 로그인",
            description = "구글에서 발급받은 Id Token을 검증하고 Qick의 JWT를 발급합니다. 신규 유저일 경우 isNewUser 필드가 true로 반환됩니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "신규 유저 로그인 성공",
                                            summary = "새로운 사용자가 성공적으로 로그인했을 때의 응답",
                                            value = "{\"status\":200,\"data\":{\"accessToken\":\"eyJhbGciOiJIUzI1NiJ9...\"," +
                                                    "\"refreshToken\":\"eyJhbGciOiJIUzI1NiJ9...\"," +
                                                    "\"isNewUser\":true}}"
                                    ),
                                    @ExampleObject(
                                            name = "기존 유저 로그인 성공",
                                            summary = "기존 사용자가 성공적으로 로그인했을 때의 응답",
                                            value = "{\"status\":200,\"data\":{\"accessToken\":\"eyJhbGciOiJIUzI1NiJ9...\"," +
                                                    "\"refreshToken\":\"eyJhbGciOiJIUzI1NiJ9...\"," +
                                                    "\"isNewUser\":false}}"
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 (Id Token 누락 또는 형식 오류)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Id Token 누락",
                                            summary = "Id Token이 요청 본문에 누락되었을 때의 응답",
                                            value = "{\n    \"status\": 400,\n    \"error\": {\n        \"code\": \"INVALID_ARGUMENT\",\n        \"message\": \"요청값이 유효하지 않습니다.\",\n        \"timestamp\": \"2025-12-31T12:05:04.082689137\",\n        \"details\": {\n            \"idToken\": \"idToken은 필수입니다.\"\n        }\n    }\n}"
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패 (유효하지 않은 Id Token)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "유효하지 않은 Id Token",
                                            summary = "Google에서 발급한 Id Token이 유효하지 않을 때의 응답",
                                            value = "{\"status\":401,\"data\":null,\"error\":{\"code\":\"INVALID_ID_TOKEN\",\"message\":\"잘못된 idToken 입니다.\",\"timestamp\":\"2023-10-26T10:00:00.000000\"}}"
                                    )
                            }
                    )
            )
    })
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid IdTokenRequest idTokenRequest) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        authService.login(idTokenRequest.idToken())));
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "JWT 토큰 재발급",
            description = "Refresh Token을 사용하여 새로운 Access Token을 발급합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "토큰 재발급 성공",
                    content = @Content(
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    name = "재발급 성공 예시",
                                    value = """
                {
                    "status": 200,
                    "data": {
                        "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWI....."
                    }
                }
                """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (Refresh Token 누락)",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "필수값 누락 예시",
                                    value = """
                {
                    "status": 400,
                    "error": {
                        "code": "INVALID_ARGUMENT",
                        "message": "요청값이 유효하지 않습니다.",
                        "timestamp": "2025-12-31T12:03:21.140784957",
                        "details": {
                            "refreshToken": "refreshToken은 필수입니다."
                        }
                    }
                }
                """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (유효하지 않거나 만료된 Refresh Token)",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "인증 실패 예시",
                                    value = """
                {
                    "status": 401,
                    "error": {
                        "code": "UNAUTHORIZED",
                        "message": "유효하지 않거나 만료된 Refresh Token입니다.",
                        "timestamp": "2025-12-31T12:05:00.000000"
                    }
                }
                """
                            )
                    )
            )
    })
    public ResponseEntity<ApiResponse<RefreshResponse>> refresh(@RequestBody @Valid RefreshRequest request) {
        String accessToken = authService.refresh(request.refreshToken());
        RefreshResponse response = new RefreshResponse(accessToken);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}