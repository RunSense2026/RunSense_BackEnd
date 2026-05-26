package com.example.runsense.domain.dto.running_activity;

import lombok.Builder;
import lombok.Getter;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Builder
public class RunningActivityListResponseDto {

	@Schema(description = "러닝 액티비티 목록(페이지) 항목")
	private List<RunningActivityListItemResponseDto> activities;

	@Schema(description = "현재 페이지 번호(0-based)", example = "0")
	private int page;

	@Schema(description = "페이지 사이즈", example = "10")
	private int size;

	@Schema(description = "전체 요소 수", example = "123")
	private long totalElements;

	@Schema(description = "전체 페이지 수", example = "13")
	private int totalPages;

	@Schema(description = "다음 페이지 존재 여부", example = "true")
	private boolean hasNext;
}