package connect.qick.domain.user.controller;

import connect.qick.domain.user.dto.request.SignupStudentRequest;
import connect.qick.domain.user.dto.request.UpdateStudentRequest;
import connect.qick.domain.user.dto.response.UserResponse;
import connect.qick.domain.user.service.UserService;
import connect.qick.global.data.ApiResponse;
import connect.qick.global.security.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/")
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
    public ResponseEntity<ApiResponse<?>> signupUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody SignupStudentRequest request
    ) {
        userService.signupStudent(userDetails.getGoogleId(), request);
        return ResponseEntity.ok(
                ApiResponse.ok("Signed up successfully!")
        );
    }

//    @PostMapping("/signup/teacher")
//    public ResponseEntity<ApiResponse<?>> signupTeacher(
//            @AuthenticationPrincipal CustomUserDetails userDetails,
//            @RequestBody Signup
//    )

    @PatchMapping("/student")
    public ResponseEntity<ApiResponse<Void>> updateUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UpdateStudentRequest request
        ) {
        userService.updateStudent(userDetails.getGoogleId(), request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        userService.deleteUser(userDetails.getGoogleId());
        return ResponseEntity.noContent().build();
    }
}
