package codebadger.virtual_launch.domain.member.domain.repository;

import codebadger.virtual_launch.domain.member.domain.entity.CreditWallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CreditWalletRepository extends JpaRepository<CreditWallet, Long> {

    Optional<CreditWallet> findByMemberId(Long memberId);

    Optional<CreditWallet> findByCompanyId(Long companyId);
}
