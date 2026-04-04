package codebadger.virtual_launch.domain.member.presentation;

import codebadger.virtual_launch.domain.member.domain.entity.CreditOwnerType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreditDeductRequest(
        @NotNull(message = "소유자 ID는 필수입니다.")
        Long ownerId,

        @NotNull(message = "소유자 타입은 필수입니다.")
        CreditOwnerType ownerType,

        @Positive(message = "차감 금액은 0보다 커야 합니다.")
        Long amount
) {
}
