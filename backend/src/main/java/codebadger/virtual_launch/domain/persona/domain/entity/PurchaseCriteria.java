package codebadger.virtual_launch.domain.persona.domain.entity;

import lombok.Getter;

@Getter
public enum PurchaseCriteria {
    COST_EFFECTIVE("가성비", "가격 대비 성능과 혜택을 꼼꼼히 따지며 합리적인 소비를 지향함"),
    HIGH_END("하이엔드", "최고급 사양과 브랜드가 주는 프리미엄 가치를 가장 중요하게 생각함"),
    PERFORMANCE("성능 중심", "제품의 기술적 완성도, 스펙, 그리고 내구성을 최우선으로 고려함"),
    DESIGN_FOCUSED("디자인/감성", "제품의 외관, 색상, 그리고 브랜드가 전달하는 감성적인 면을 선호함"),
    CONVENIENCE("편의성/실용", "사용법이 직관적이고 일상에서 관리가 간편한 제품을 선호함"),
    BRAND_TRUST("신뢰/안정", "인지도 높은 브랜드의 명성이나 믿을 수 있는 사후 서비스(AS)를 중시함"),
    INNOVATION("트렌드/혁신", "최신 기술이 적용된 신제품이나 남들이 사용하지 않는 새로운 기능을 선호함");

    private final String title;       // 한글 명칭
    private final String description; // 상세 설명

    PurchaseCriteria(String title, String description) {
        this.title = title;
        this.description = description;
    }
}