package connect.qick.domain.auth.controller;

import connect.qick.domain.auth.dto.request.IdTokenRequest;
import connect.qick.domain.auth.dto.request.RefreshRequest;
import connect.qick.domain.auth.dto.response.LoginResponse;
import connect.qick.domain.auth.dto.response.RefreshResponse;
import connect.qick.domain.auth.service.AuthService;
import connect.qick.global.data.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid IdTokenRequest idTokenRequest) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        authService.login(idTokenRequest.idToken())));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshResponse>> refresh(@RequestBody @Valid RefreshRequest request) {
        String accessToken = authService.refresh(request.refreshToken());
        RefreshResponse response = new RefreshResponse(accessToken);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}