package codebadger.virtual_launch.domain.persona.presentation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KosisIncomeResponse {

    @JsonProperty("DT")
    private String value;          // 통계 수치 (예: "3374")

    @JsonProperty("ITM_NM")
    private String itemName;       // 항목명 (예: "월급여액")

    @JsonProperty("UNIT_NM")
    private String unit;           // 단위 (예: "천원")

    @JsonProperty("NM")
    private String categoryName;   // 분류 명칭 (예: "정보통신업" 또는 "전체근로자")

    /**
     * "월급여액" 항목인지 확인하는 유틸리티 메서드
     */
    public boolean isSalaryItem() {
        return "월급여액".equals(itemName);
    }

    /**
     * 실제 원화(KRW) 단위로 환산된 금액을 반환
     */
    public long getConvertedAmount() {
        if (value == null || value.isEmpty()) return 0L;

        double rawValue = Double.parseDouble(value);
        if ("천원".equals(unit)) {
            return (long) (rawValue * 1000);
        }
        return (long) rawValue;
    }
}
