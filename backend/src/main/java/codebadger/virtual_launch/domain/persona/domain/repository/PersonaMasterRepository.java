package codebadger.virtual_launch.domain.persona.domain.repository;

import codebadger.virtual_launch.domain.persona.domain.entity.PersonaMaster;
import codebadger.virtual_launch.domain.persona.domain.entity.PurchaseCriteria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonaMasterRepository extends JpaRepository<PersonaMaster, Long> {

    // 1. 연령대와 성별로 페르소나 목록 조회
    List<PersonaMaster> findByAgeGroupAndGender(String ageGroup, String gender);

    // 2. 특정 구매 성향을 가진 페르소나 목록 조회
    List<PersonaMaster> findByPurchaseCriteria(PurchaseCriteria purchaseCriteria);

    // 3. 해당 조건의 페르소나가 존재하는지 확인 (중복 방지 등)
    boolean existsByAgeGroupAndGenderAndOccupation(String ageGroup, String gender, String occupation);
}
