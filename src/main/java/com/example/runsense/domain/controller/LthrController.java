package com.example.runsense.domain.controller;

import com.example.runsense.domain.dto.lthr.LthrRecordCreateRequestDto;
import com.example.runsense.domain.dto.lthr.LthrRecordResponseDto;
import com.example.runsense.domain.service.LthrService;
import com.example.runsense.global.exception.ApiErrorAnnotation;
import com.example.runsense.global.exception.ErrorCode;
import com.example.runsense.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lthr")
@Tag(name = "lthr", description = "LTHR 테스트 기록 엔드포인트")
public class LthrController {

    private final LthrService lthrService;

    public LthrController(LthrService lthrService) {
        this.lthrService = lthrService;
    }

    @Operation(summary = "LTHR 테스트 결과 저장",
            description = "LTHR 테스트 결과를 기록으로 저장하고 유저의 현재 LTHR·안정시 심박수도 함께 갱신합니다. "
                    + "두 값은 유저 정보 수정 API로는 바꿀 수 없고 이 API로만 갱신됩니다. "
                    + "직전 LTHR은 마지막 테스트 기록 기준으로 서버가 채웁니다.")
    @PostMapping
    @ApiErrorAnnotation({ErrorCode.USER_NOT_FOUND, ErrorCode.INVALID_INPUT_VALUE})
    public ApiResponse<LthrRecordResponseDto> create(@Valid @RequestBody LthrRecordCreateRequestDto requestDto,
                                                     @AuthenticationPrincipal(expression = "userId") Long userId) {
        return ApiResponse.success(lthrService.create(requestDto, userId));
    }

    @Operation(summary = "마지막 LTHR 테스트 기록 조회",
            description = "LTHR 시작 화면의 '지난 측정' 카드용. 측정 후 6주(42일) 이상 지나면 recommendRetest가 true입니다. "
                    + "테스트 기록이 없으면 data가 null입니다.")
    @GetMapping("/latest")
    public ApiResponse<LthrRecordResponseDto> readLatest(@AuthenticationPrincipal(expression = "userId") Long userId) {
        return ApiResponse.success(lthrService.readLatest(userId));
    }
}
