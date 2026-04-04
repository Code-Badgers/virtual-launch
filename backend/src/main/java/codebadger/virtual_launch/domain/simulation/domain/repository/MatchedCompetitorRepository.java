package codebadger.virtual_launch.domain.simulation.domain.repository;

import codebadger.virtual_launch.domain.simulation.domain.entity.MatchedCompetitor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchedCompetitorRepository extends JpaRepository<MatchedCompetitor, Long> {
}
