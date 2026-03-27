package codebadger.virtual_launch.domain.member.presentation;

import codebadger.virtual_launch.domain.member.application.SignUpCommand;
import codebadger.virtual_launch.domain.member.domain.entity.MemberRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignUpRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        String password,

        @NotNull
        MemberRole role
) {
    public SignUpCommand toCommand() {
        return new SignUpCommand(email, password, role);
    }
}