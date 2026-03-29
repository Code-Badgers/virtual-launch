package codebadger.virtual_launch.domain.member.presentation;

import codebadger.virtual_launch.common.api.SuccessResponse;
import codebadger.virtual_launch.domain.member.application.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SuccessResponse<Long>> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        Long id = authService.signUp(signUpRequest.toCommand());
        SuccessResponse<Long> response = SuccessResponse.ok(id, "회원가입이 성공적으로 완료되었습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<String>> login(@Valid @RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest.toCommand());
        return ResponseEntity.ok(SuccessResponse.ok(token, "로그인이 성공적으로 완료되었습니다."));
    }
}
