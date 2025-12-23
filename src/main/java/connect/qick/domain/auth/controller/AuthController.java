package connect.qick.domain.auth.controller;

import connect.qick.domain.auth.dto.request.IdTokenRequest;
import connect.qick.domain.auth.dto.request.RefreshRequest;
import connect.qick.domain.auth.dto.response.LoginResponse;
import connect.qick.domain.auth.dto.response.RefreshResponse;
import connect.qick.domain.auth.service.AuthService;
import connect.qick.global.data.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
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
            description = "구글에서 발급받은 Id Token을 검증하고 jwt를 발급합니다.신규유저 여부를 알려줍니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "로그인 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "유효하지 않은 Id Token입니다."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "잘못된 요청: \n" +
                    "- Id Token 누락됨 \n" +
                    "- Id Token 형식이 올바르지 않음"
            ),
    })
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid IdTokenRequest idTokenRequest) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        authService.login(idTokenRequest.idToken())));
    }

    @PostMapping("/refresh")
    @Operation(
            summary="jwt 토큰 리프레시",
            description = "refresh 토큰을 통해 access 토큰을 재발급 합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "토큰 재발급 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "유효하지 않은 Id Token입니다."
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403", description = "잘못된 요청: \n" +
                    "- Id Token 누락됨 \n" +
                    "- Id Token 형식이 올바르지 않음"
            ),
    })
    public ResponseEntity<ApiResponse<RefreshResponse>> refresh(@RequestBody @Valid RefreshRequest request) {
        String accessToken = authService.refresh(request.refreshToken());
        RefreshResponse response = new RefreshResponse(accessToken);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}