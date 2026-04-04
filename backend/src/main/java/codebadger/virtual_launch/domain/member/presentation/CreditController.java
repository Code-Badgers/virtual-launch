package codebadger.virtual_launch.domain.member.presentation;

import codebadger.virtual_launch.common.api.SuccessResponse;
import codebadger.virtual_launch.domain.member.application.CreditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/credits")
@RequiredArgsConstructor
public class CreditController {

    private final CreditService creditService;

    @PostMapping("/deduct")
    public ResponseEntity<SuccessResponse<CreditBalanceResponse>> deductCredit(
            @RequestBody @Valid CreditDeductRequest request
    ) {
        // 1. 서비스를 호출하여 차감 로직을 수행하고 남은 잔액을 받습니다.
        Long remainingBalance = creditService.deductCredit(
                request.ownerId(),
                request.ownerType(),
                request.amount()
        );

        // 2. 결과 DTO를 생성합니다.
        CreditBalanceResponse response = new CreditBalanceResponse(remainingBalance);

        // 3. ResponseEntity에 SuccessResponse를 담아 200 OK와 함께 반환합니다.
        return ResponseEntity.ok(SuccessResponse.ok(response));
    }
}
