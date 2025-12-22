package connect.qick.domain.user.controller;

import connect.qick.domain.user.dto.request.UpdateUserRequest;
import connect.qick.domain.user.entity.UserEntity;
import connect.qick.domain.user.service.UserService;
import connect.qick.global.data.ApiResponse;
import connect.qick.global.security.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/")
    public ResponseEntity<?> getUser() {
        return ResponseEntity.ok("hello, world!!");
    }

    @PatchMapping("/")
    public ResponseEntity<ApiResponse<Void>> updateUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UpdateUserRequest request
            ) {
        userService.updateUser(userDetails.getGoogleId(), request);
        return ResponseEntity.noContent().build();
    }
}
