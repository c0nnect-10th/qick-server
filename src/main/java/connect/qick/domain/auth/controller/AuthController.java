package connect.qick.domain.auth.controller;

import connect.qick.domain.auth.dto.request.TeacherSignUpRequest;
import connect.qick.domain.auth.usecase.TeacherSignUpUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TeacherSignUpUseCase teachersignUpUseCase;


    @PostMapping("signup/teacher")
    public ResponseEntity<Void> signUp(@Valid  @RequestBody TeacherSignUpRequest request) {
        teachersignUpUseCase.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /*@PostMapping("/login")
    public ResponseEntity<SignInResponse> login(@Valid @RequestBody SignInRequest signInRequest) {
        SignInResponse response = loginUseCase.login(signInRequest);
        return ResponseEntity.ok(response);
    }*/
}
