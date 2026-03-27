package codebadger.virtual_launch.domain.simulation.domain.repository;

import codebadger.virtual_launch.domain.simulation.domain.entity.CompetitorProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitorProductRepository extends JpaRepository<CompetitorProduct, Long> {
}
