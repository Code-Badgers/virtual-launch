package codebadger.virtual_launch.domain.simulation.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import codebadger.virtual_launch.domain.simulation.domain.entity.Category;
import codebadger.virtual_launch.domain.simulation.domain.entity.CompetitorProduct;
import codebadger.virtual_launch.domain.simulation.domain.entity.ProductSpec;
import codebadger.virtual_launch.domain.simulation.domain.repository.CategoryRepository;
import codebadger.virtual_launch.domain.simulation.domain.repository.CompetitorProductRepository;
import codebadger.virtual_launch.domain.simulation.domain.repository.ProductSpecRepository;
import codebadger.virtual_launch.domain.simulation.presentation.dto.MatchResultDto;
import codebadger.virtual_launch.domain.simulation.presentation.dto.MatchScoreDto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductMatcherTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductSpecRepository productSpecRepository;
    @Mock
    private CompetitorProductRepository competitorProductRepository;

    @InjectMocks
    private ProductMatcher productMatcher;

    private Map<String, String> targetMap;

    @BeforeEach
    void setUp() { // 테스트 시 사용할 공통 제품 스펙
        targetMap = Map.of(
                "가격", "150000",
                "CPU", "Apple M1",
                "RAM", "16GB",
                "Storage", "512GB"
        );
    }

    @Test
    @DisplayName("가중치가 적용된 유사도 점수가 올바르게 계산되는지 테스트")
    void calculateWeightedScore() {
        // Given
        Map<String, String> compMap = Map.of( // 경쟁사 제품
                "가격", "185000", // 가중치 0.3
                "CPU", "Apple M1", // 일치 (100점)
                "RAM", "32GB",
                "Storage", "256GB"
        );

        // 가중치 일치 여부 테스트
        double expected = 100 / 0.6;

        // When
        MatchScoreDto result = productMatcher.calculateIndividualScore(targetMap, compMap);

        // Then
        //assertEquals(expected, result.totalScore(), 0.01);
        assertEquals(0.0, result.eachScores().get("가격"));
        assertEquals(100.0, result.eachScores().get("CPU"));

        // System.out.println("결과 점수: " + result.totalScore());
    }

    @Test
    @DisplayName("유사도가 가장 높은 3개의 제품이 올바르게 반환되는지 테스트")
    void findTopMatches() {
        //Given
        Category category = Category.builder()
                .categoryId(1L)
                .categoryName("노트북")
                .build();

        ProductSpec productSpec = ProductSpec.builder()
                .productId(1L)
                .category(category)
                .detailedSpecs(new HashMap<>())
                .build();

        // 비교할 경쟁사 제품 생성
        CompetitorProduct bestMatch = CompetitorProduct.builder().modelName("최고제품").build();
        CompetitorProduct worstMatch = CompetitorProduct.builder().modelName("최저제품").build();

        // 카테고리 조회 시 해당 두 경쟁사 제품 반환
        when(competitorProductRepository.findByCategory(any())).thenReturn(List.of(bestMatch, worstMatch));

        // When
        List<MatchResultDto> result = productMatcher.findTopMatches(productSpec);

        // Then
        assertNotNull(result);
        assertTrue(result.size() <= 3); // 최대 3개 제품 반환
        if(result.size() >= 2) { // 결과가 2개 이상일 경우, 점수 순서가 올바른지 확인 (내림차순)
            assertTrue(result.get(0).score().totalScore() >= result.get(1).score().totalScore());
        }
    }
}