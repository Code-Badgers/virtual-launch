package codebadger.virtual_launch.domain.simulation.domain.repository;

import codebadger.virtual_launch.domain.simulation.domain.entity.SimulationProject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SimulationProjectRepository extends JpaRepository<SimulationProject, Long> {
}
