package codebadger.virtual_launch.domain.simulation.presentation;

import codebadger.virtual_launch.common.api.SuccessResponse;
import codebadger.virtual_launch.domain.simulation.application.CategoryService;
import codebadger.virtual_launch.domain.simulation.domain.entity.RequiredSpec;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // 카테고리명에 따른 필수 사양 조회
    // http://localhost:8080/api/categories/노트북/specs
    @GetMapping("/{categoryName}/specs")
    @Operation(summary = "카테고리명에 따른 필수 사양 조회", description = "카테고리명에 따른 필수 사양 조회합니다")
    public SuccessResponse<Map<String, Map<String, RequiredSpec>>> getSpecsByCategoryName (@PathVariable String categoryName) {

        Map<String, Map<String, RequiredSpec>> response = categoryService.getSpecsByCategoryName(categoryName);
        return SuccessResponse.ok(response, "카테고리명에 따른 필수 사양 조회가 완료되었습니다");
    }
}
