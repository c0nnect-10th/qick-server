package connect.qick.domain.volunteer.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum WorkDifficulty {
    EASY(1),
    NORMAL(2),
    HARD(3);

    private final int difficulty;
}
