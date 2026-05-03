package codebadger.virtual_launch.domain.persona.infrastructure;

import lombok.Getter;

@Getter
public enum AgeRange {

    // 상수명("한글 명칭", "KOSIS API 코드")
    ALL("전체", "13102732817AGES.0"),
    UNDER_29("29세 이하", "13102732817AGES.1"),
    AGE_30_39("30~39세", "13102732817AGES.2"),
    AGE_40_49("40~49세", "13102732817AGES.3"),
    AGE_50_59("50~59세", "13102732817AGES.4"),
    OVER_60("60세 이상", "13102732817AGES.5");

    private final String description; // 한글 명칭
    private final String code;        // KOSIS API용 코드

    AgeRange(String description, String code) {
        this.description = description;
        this.code = code;
    }
}
