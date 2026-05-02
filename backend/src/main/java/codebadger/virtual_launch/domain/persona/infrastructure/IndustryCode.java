package codebadger.virtual_launch.domain.persona.infrastructure;

import lombok.Getter;

@Getter
public enum IndustryCode {

    // 상수명("한글 명칭", "KOSIS API 코드")
    ELECTRIC_GAS("전기, 가스, 증기 및 공기 조절 공급업", "190326INDUSTRY_10SD"),
    WATER_WASTE("수도, 하수 및 폐기물 처리, 원료 재생업", "190326INDUSTRY_10SE"),
    CONSTRUCTION("건설업", "190326INDUSTRY_10SF"),
    WHOLESALE_RETAIL("도매 및 소매업", "190326INDUSTRY_10SG"),
    TRANSPORT_STORAGE("운수 및 창고업", "190326INDUSTRY_10SH"),
    ACCOMMODATION_FOOD("숙박 및 음식점업", "190326INDUSTRY_10SI"),
    INFO_COMMUNICATION("정보통신업", "190326INDUSTRY_10SJ"),
    FINANCE_INSURANCE("금융 및 보험업", "190326INDUSTRY_10SK"),
    REAL_ESTATE("부동산업", "190326INDUSTRY_10SL"),
    PROFESSIONAL_TECH("전문, 과학 및 기술 서비스업", "190326INDUSTRY_10SM"),
    BUSINESS_SUPPORT("사업시설 관리, 사업 지원 및 임대 서비스업", "190326INDUSTRY_10SN"),
    EDUCATION("교육 서비스업", "190326INDUSTRY_10SPP"),
    HEALTH_SOCIAL_WELFARE("보건업 및 사회복지 서비스업", "190326INDUSTRY_10SQ"),
    ART_SPORTS_RECREATION("예술, 스포츠 및 여가 관련 서비스업", "190326INDUSTRY_10SR"),
    OTHER_SERVICES("협회 및 단체, 수리 및 기타 개인 서비스업", "190326INDUSTRY_10SS");

    private final String description; // 한글 명칭
    private final String code;        // KOSIS API용 코드

    IndustryCode(String description, String code) {
        this.description = description;
        this.code = code;
    }
}