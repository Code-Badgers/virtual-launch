package codebadger.virtual_launch.domain.simulation.application;

import codebadger.virtual_launch.common.exception.BusinessException;
import codebadger.virtual_launch.common.exception.ErrorCode;
import codebadger.virtual_launch.domain.member.domain.entity.Member;
import codebadger.virtual_launch.domain.member.domain.repository.MemberRepository;
import codebadger.virtual_launch.domain.simulation.domain.entity.Category;
import codebadger.virtual_launch.domain.simulation.domain.entity.ProductSpec;
import codebadger.virtual_launch.domain.simulation.domain.repository.CategoryRepository;
import codebadger.virtual_launch.domain.simulation.domain.repository.ProductSpecRepository;
import codebadger.virtual_launch.domain.simulation.presentation.dto.ProductSpecResponse;
import codebadger.virtual_launch.domain.simulation.presentation.dto.ProductSpecSaveRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductSpecService {

    private final ProductSpecRepository productSpecRepository;
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;

    // 가상 런칭 제품 스펙 저장 메서드
    public ProductSpecResponse saveProductSpec(ProductSpecSaveRequest request, Long memberId) {

        // 카테고리 ID가 없을 경우 기본값 1L로 설정
        Long categoryId = (request.categoryId() == null || request.categoryId() == 0)
                ? 1L : request.categoryId();

        // 카테고리 Id 확인
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // 제품 사양과 카테고리 간의 연관 관계를 설정하는 메서드
        ProductSpec productSpec = ProductSpec.builder()
                .category(category)
                .productName(request.productName())
                .member(member)
                .productDescription(request.productDescription())
                .productImageUrl(request.productImageUrl())
                .plannedLaunchDate(request.targetLaunchDate())
                .plannedPrice(request.plannedPrice())
                .detailedSpecs(request.detailedSpecs())
                .build();

        // 가상 런칭 제품 스펙 저장
        ProductSpec savedProductSpec = productSpecRepository.save(productSpec);

        // response DTO 반환
        return ProductSpecResponse.from(savedProductSpec);
    }
}
