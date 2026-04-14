package codebadger.virtual_launch.domain.simulation.domain.entity;

import java.util.List;
import lombok.Builder;

@Builder
public record RequiredSpec(
        String label, // 예: "CPU", "RAM", "Storage"
        String type, // 예: "text", "number", "select"
        List<String> options, // type이 select인 경우 선택 가능한 옵션들
        String unit, // 데이터 뒤에 붙는 단위 예: "kg", "L", "W", "dB" / 단위가 필요 없는 문자열의 경우 null 가능
        String value, // 사용자가 입력한 값
        String selectedOption // type이 select인 경우 사용자가 선택한 옵션
) { }
