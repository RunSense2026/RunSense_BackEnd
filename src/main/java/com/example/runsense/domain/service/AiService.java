package com.example.runsense.domain.service;

import com.example.runsense.domain.dto.ai.CoachingRequestDto;
import com.example.runsense.domain.dto.ai.CoachingResponseDto;
import com.example.runsense.domain.dto.ai.ReportRequestDto;
import com.example.runsense.domain.dto.ai.ReportResponseDto;
import com.example.runsense.domain.repository.UserRepository;
import com.example.runsense.global.exception.CustomException;
import com.example.runsense.global.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class AiService {
    private static final String COACHING_PATH = "/ai/coaching";
    private static final String REPORT_PATH = "/ai/coaching/report";

    private final UserRepository userRepository;
    private final RestClient aiRestClient;

    public AiService(UserRepository userRepository, @Value("${app.ai.api-url}") String aiApiUrl) {
        this.userRepository = userRepository;
        this.aiRestClient = RestClient.builder().baseUrl(aiApiUrl).build();
    }

    public CoachingResponseDto create(CoachingRequestDto requestDto, Long userId){
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

        return post(COACHING_PATH, requestDto, CoachingResponseDto.class);
    }

    // 별도 API로 노출되지 않고 RunningActivityService.create() 내부에서만 호출됨.
    // 호출 전 유저 검증이 이미 끝난 상태이므로 여기서는 userId를 받지 않는다.
    public ReportResponseDto createReport(ReportRequestDto requestDto){
        return post(REPORT_PATH, requestDto, ReportResponseDto.class);
    }

    // create()/createReport() 공통 요청-예외 처리 로직
    private <T> T post(String path, Object requestBody, Class<T> responseType) {
        try {
            return aiRestClient.post()
                    .uri(path)
                    .body(requestBody)
                    .retrieve()
                    .body(responseType);
        } catch (RestClientException e) {
            throw new CustomException(ErrorCode.AI_SERVICE_ERROR);
        }
    }
}
