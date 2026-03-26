package codebadger.virtual_launch.domain.simulation.presentation.dto;

import codebadger.virtual_launch.domain.simulation.domain.entity.RequiredSpec;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;

@Schema(description = "가상 런칭 제품 스펙 데이터 요청")
public record ProductSpecSaveRequest(
        @Schema(description = "카테고리 ID", example = "1")
        @NotNull Long categoryId,

        @Schema(description = "제품명", example = "비전북 Pro 16")
        @NotBlank String productName,

        @Schema(description = "제품 상세 설명", example = "M3 Max 칩셋이 탑재된 전문가용 고성능 노트북")
        String productDescription,

        @Schema(description = "제품 이미지 URL", example = "https://example.com/images/laptop-pro.png")
        String productImageUrl,

        @Schema(description = "출시 예정일 (ISO 8601)", example = "2026-10-15T09:00:00+09:00")
        OffsetDateTime targetLaunchDate,

        @Schema(description = "목표 출시가", example = "3500000")
        BigDecimal plannedPrice,

        @Schema(
                description = "카테고리별 상세 스펙 데이터 (SQL에 정의된 노트북 스펙 예시)",
                example = """
                {
                  "technical_specs": {
                    "rated_voltage": { "label": "정격 전압", "type": "string", "unit": "V" },
                    "power_consumption": { "label": "소비 전력", "type": "number", "unit": "W" },
                    "weight": { "label": "무게", "type": "number", "unit": "kg" }
                  },
                  "product_specs": {
                    "processor": { "label": "프로세서(CPU)", "type": "select", "options": ["M3 Max"], "unit": "" },
                    "ram": { "label": "메모리(RAM)", "type": "select", "options": ["32GB"], "unit": "GB" },
                    "graphics": { "label": "그래픽(GPU)", "type": "select", "options": ["RTX 4070"], "unit": "" }
                  },
                  "compliance": {
                    "kc_auth": { "label": "KC 인증 번호", "type": "string", "unit": "" },
                    "warranty": { "label": "무상 보증 기간", "type": "select", "options": ["2년"], "unit": "" }
                  }
                }
                """
        )
        Map<String, Map<String, RequiredSpec>> detailedSpecs
) {
}
