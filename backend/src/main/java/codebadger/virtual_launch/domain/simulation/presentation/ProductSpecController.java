package codebadger.virtual_launch.domain.simulation.presentation;

import codebadger.virtual_launch.common.api.SuccessResponse;
import codebadger.virtual_launch.common.security.CustomUserDetails;
import codebadger.virtual_launch.domain.simulation.application.ProductSpecService;
import codebadger.virtual_launch.domain.simulation.presentation.dto.ProductSpecResponse;
import codebadger.virtual_launch.domain.simulation.presentation.dto.ProductSpecSaveRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/product-specs")
@RequiredArgsConstructor
public class ProductSpecController {

    private final ProductSpecService productSpecService;

    @PostMapping
    @Operation(summary = "가상 런칭 제품 상세 스펙 작성 및 저장", description = "가상 런칭 제품 상세 스펙 작성 및 저장합니다")
    public SuccessResponse<ProductSpecResponse> saveProductSpec(
            @Valid @RequestBody ProductSpecSaveRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getId();
        ProductSpecResponse response = productSpecService.saveProductSpec(request, memberId);

        return SuccessResponse.ok(response, "가상 런칭 제품 상세 스펙 작성 및 저장이 완료되었습니다");
    }
}
