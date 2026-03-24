package codebadger.virtual_launch.domain.simulation.application;

import codebadger.virtual_launch.common.exception.BusinessException;
import codebadger.virtual_launch.common.exception.ErrorCode;
import codebadger.virtual_launch.domain.simulation.domain.Category;
import codebadger.virtual_launch.domain.simulation.domain.CategoryRepository;
import codebadger.virtual_launch.domain.simulation.domain.RequiredSpec;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRespoitory;

    // 카테고리명을 통한 조회
    @Transactional(readOnly = true)
    public Map<String, Map<String, RequiredSpec>> getSpecsByCategoryName(String categoryName) {
        return categoryRespoitory.findByCategoryName(categoryName)
                .map(Category::getRequiredSpecs)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
    }
}
