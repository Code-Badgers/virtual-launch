package codebadger.virtual_launch.domain.member.application;

import codebadger.virtual_launch.domain.member.domain.entity.CreditOwnerType;
import codebadger.virtual_launch.domain.member.domain.entity.CreditWallet;
import codebadger.virtual_launch.domain.member.domain.repository.CreditWalletRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreditService {

    private final CreditWalletRepository creditWalletRepository;

    public void deductCredit(Long ownerId, CreditOwnerType ownerType, Long amount) {
        // 1. 도메인 서비스나 레포지토리를 통해 주체에 맞는 지갑 조회
        CreditWallet wallet = findWalletOrThrow(ownerId, ownerType);

        // 2. 비즈니스 로직 수행 (엔티티 내부 메서드 호출)
        wallet.deduct(amount);
    }

    private CreditWallet findWalletOrThrow(Long id, CreditOwnerType type) {
        return switch (type) {
            case MEMBER -> creditWalletRepository.findByMemberId(id)
                    .orElseThrow(() -> new EntityNotFoundException("Member wallet not found: " + id));
            case COMPANY -> creditWalletRepository.findByCompanyId(id)
                    .orElseThrow(() -> new EntityNotFoundException("Company wallet not found: " + id));
        };
    }


}
