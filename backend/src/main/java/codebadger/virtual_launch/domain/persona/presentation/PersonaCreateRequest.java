package codebadger.virtual_launch.domain.persona.presentation;

import codebadger.virtual_launch.domain.persona.domain.entity.PurchaseCriteria;
import codebadger.virtual_launch.domain.persona.infrastructure.AgeRange;
import codebadger.virtual_launch.domain.persona.infrastructure.IndustryCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PersonaCreateRequest(
        @NotNull(message = "연령대는 필수 입력 항목입니다.") // Enum은 @NotNull 사용
        AgeRange ageRange,

        @NotBlank(message = "성별은 필수 입력 항목입니다.")
        String gender,

        @NotNull(message = "직업(산업군)은 필수 입력 항목입니다.") // Enum은 @NotNull 사용
        IndustryCode industryCode,

        @NotBlank(message = "소득수준은 필수 입력 항목입니다.")
        String incomeLevel,

        @NotNull(message = "구매기준은 필수 입력 항목입니다.")
        PurchaseCriteria purchaseCriteria,

        @Size(max = 200)
        String personality,

        @Size(max = 200)
        String lifestyle
) {
}
