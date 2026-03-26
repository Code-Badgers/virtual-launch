package codebadger.virtual_launch.common.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.OffsetDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class) // 자동 시간 기록 JPA Auditing 활성화
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(updatable = false, nullable = false)
    @Schema(description = "생성 일시", example = "2024-06-01T12:00:00")
    private OffsetDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    @Schema(description = "수정 일시", example = "2024-06-01T12:30:00")
    private OffsetDateTime updatedAt;
}
