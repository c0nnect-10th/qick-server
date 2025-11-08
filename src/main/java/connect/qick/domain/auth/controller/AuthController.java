package connect.qick.domain.auth.controller;

import connect.qick.domain.auth.dto.request.StudentSignUpRequest;
import connect.qick.domain.auth.dto.request.TeacherSignUpRequest;
import connect.qick.domain.auth.usecase.SignUpUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SignUpUseCase signUpUseCase;

    @PostMapping("signup/teacher")
    public ResponseEntity<Void> signUp(@Valid  @RequestBody TeacherSignUpRequest request) {
        signUpUseCase.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("signup/student")
    public ResponseEntity<Void> signUp(@Valid @RequestBody StudentSignUpRequest request) {
        signUpUseCase.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
