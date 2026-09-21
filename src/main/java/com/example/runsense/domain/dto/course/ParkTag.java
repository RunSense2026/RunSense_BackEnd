package com.example.runsense.domain.dto.course;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

// 공원 보유시설 원문(자유 입력)을 러닝에 쓸모 있는 표준 태그로 묶는다.
// 원문에 키워드가 하나라도 포함되면 해당 태그를 붙인다. 매핑되지 않은 시설은 태그 없이 facilities 원문으로만 노출한다.
@Getter
public enum ParkTag {
    WALKING_TRAIL("산책로", List.of("산책로", "둘레길", "트랙", "조깅", "황톳길", "맨발", "탐방로", "순환로", "데크길")),
    EXERCISE_EQUIPMENT("운동기구", List.of("운동기구", "체력단련", "헬스", "하늘걷기", "공중걷기", "허리돌리기", "철봉",
            "평행봉", "윗몸일으키기", "마라톤운동", "런닝트레이너", "에어워킹", "크로스컨트리", "레그프레스")),
    SPORTS_FIELD("구기장", List.of("농구", "배드민턴", "족구", "축구", "풋살", "테니스", "게이트볼", "운동장", "다목적구장")),
    TOILET("화장실", List.of("화장실")),
    WATER_FOUNTAIN("음수대", List.of("음수대", "음수전", "음수장", "급수대")),
    PARKING("주차장", List.of("주차장")),
    REST_AREA("휴식공간", List.of("벤치", "의자", "파고라", "퍼걸러", "정자", "쉼터", "평상", "원두막", "휴게")),
    CCTV("CCTV", List.of("CCTV", "cctv", "방범"));

    private final String label;
    private final List<String> keywords;

    ParkTag(String label, List<String> keywords) {
        this.label = label;
        this.keywords = keywords;
    }

    public boolean matches(String facilityText) {
        return keywords.stream().anyMatch(facilityText::contains);
    }

    public static Optional<ParkTag> fromLabel(String label) {
        return Arrays.stream(values())
                .filter(tag -> tag.label.equals(label))
                .findFirst();
    }
}
