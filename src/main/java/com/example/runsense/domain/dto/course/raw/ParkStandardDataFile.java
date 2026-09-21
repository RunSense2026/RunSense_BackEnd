package com.example.runsense.domain.dto.course.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// 공공데이터포털 "전국도시공원정보표준데이터" JSON 파일 최상위 구조 ({fields, records})
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParkStandardDataFile {

    private List<ParkRecordDto> records;
}
