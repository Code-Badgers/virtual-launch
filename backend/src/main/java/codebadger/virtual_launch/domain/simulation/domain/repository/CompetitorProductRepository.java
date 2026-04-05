package codebadger.virtual_launch.domain.simulation.domain.repository;

import codebadger.virtual_launch.domain.simulation.domain.entity.Category;
import codebadger.virtual_launch.domain.simulation.domain.entity.CompetitorProduct;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitorProductRepository extends JpaRepository<CompetitorProduct, Long> {

    // 카테고리를 통한 경쟁사 제품 조회
    List<CompetitorProduct> findByCategory(Category category);
}
