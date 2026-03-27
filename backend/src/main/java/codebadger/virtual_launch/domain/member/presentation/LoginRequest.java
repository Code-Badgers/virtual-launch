package codebadger.virtual_launch.domain.member.presentation;

import codebadger.virtual_launch.domain.member.application.LoginCommand;
import codebadger.virtual_launch.domain.member.application.SignUpCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        String email,

        @Size(min = 8, max = 20)
        @NotBlank
        String password) {

    public LoginCommand toCommand() {
        return new LoginCommand(email, password);
    }
}
