package codebadger.virtual_launch.domain.simulation.presentation.dto;

import codebadger.virtual_launch.domain.simulation.domain.entity.ProductSpec;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ProductSpecResponse(
        Long productId,
        String categoryName,
        String productName,
        String productDescription,
        String productImageUrl,
        OffsetDateTime plannedLaunchDate,
        BigDecimal plannedPrice,
        OffsetDateTime createdAt
) {
        public static ProductSpecResponse from(ProductSpec entity) {
            return new ProductSpecResponse(
                    entity.getProductId(),
                    entity.getCategory().getCategoryName(),
                    entity.getProductName(),
                    entity.getProductDescription(),
                    entity.getProductImageUrl(),
                    entity.getPlannedLaunchDate(),
                    entity.getPlannedPrice(),
                    entity.getCreatedAt()
            );
        }

        public String getFormattedPrice() {
            return plannedPrice + "원";
        }
}
