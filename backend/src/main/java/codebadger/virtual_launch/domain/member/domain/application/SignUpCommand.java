package codebadger.virtual_launch.domain.member.domain.application;

import codebadger.virtual_launch.domain.member.domain.entity.MemberRole;

public record SignUpCommand(
        String email,
        String password,
        MemberRole role
) {}
