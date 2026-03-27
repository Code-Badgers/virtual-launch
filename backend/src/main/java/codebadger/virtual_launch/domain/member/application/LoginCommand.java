package codebadger.virtual_launch.domain.member.application;

public record LoginCommand(String email,
                           String password) {
}
