package codebadger.virtual_launch.common.api;

import java.time.LocalDateTime;

public record ErrorResponse(
        String title,
        int status,
        String detail,
        String instance,
        LocalDateTime timestamp
) {}
