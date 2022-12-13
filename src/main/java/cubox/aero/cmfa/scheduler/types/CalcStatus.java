package cubox.aero.cmfa.scheduler.types;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum CalcStatus {
    MONTH("M", 1),
    QUARTER("Q", 3),
    YEAR("Y", 12);

    private final String calcType;
    private final int calcValue;

    CalcStatus(String calcType, int calcValue) {
        this.calcType = calcType;
        this.calcValue = calcValue;
    }

    private static final Map<String, String> CODE_MAP =
            Collections.unmodifiableMap(Stream
                        .of(values())
                        .collect(Collectors.toMap(CalcStatus::getCalcType, CalcStatus::name)));

    public static CalcStatus of(final String calcType) {
        return CalcStatus.valueOf(CODE_MAP.get(calcType));
    }
}
