package codebadger.virtual_launch.domain.simulation.presentation.dto;

import codebadger.virtual_launch.domain.simulation.domain.entity.RequiredSpec;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;

public record ProductSpecSaveRequest(
        @NotNull Long categoryId,
        @NotNull String productName,
        String productDescription,
        String productImageUrl,
        OffsetDateTime targetLaunchDate,
        BigDecimal targetLaunchPrice,
        Map<String, Map<String, RequiredSpec>> detailedSpecs
) {
}
