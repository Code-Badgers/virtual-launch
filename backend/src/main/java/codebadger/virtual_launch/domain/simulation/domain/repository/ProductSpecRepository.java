package codebadger.virtual_launch.domain.simulation.domain.repository;

import codebadger.virtual_launch.domain.simulation.domain.entity.ProductSpec;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductSpecRepository extends JpaRepository<ProductSpec, Long> {

    @Query("SELECT p FROM ProductSpec p JOIN FETCH p.category WHERE p.productId = :productId")
    Optional<ProductSpec> findByIdWithCategory(@Param("productId") Long productId);
}
