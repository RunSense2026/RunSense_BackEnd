package com.example.runsense.domain.service;

import com.example.runsense.domain.dto.ai.CoachingRequestDto;
import com.example.runsense.domain.dto.ai.CoachingResponseDto;
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

    private final UserRepository userRepository;
    private final RestClient aiRestClient;

    public AiService(UserRepository userRepository, @Value("${app.ai.api-url}") String aiApiUrl) {
        this.userRepository = userRepository;
        this.aiRestClient = RestClient.builder().baseUrl(aiApiUrl).build();
    }

    public CoachingResponseDto create(CoachingRequestDto requestDto, Long userId){
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_INPUT_VALUE));

        try {
            return aiRestClient.post()
                    .uri(COACHING_PATH)
                    .body(requestDto)
                    .retrieve()
                    .body(CoachingResponseDto.class);
        } catch (RestClientException e) {
            throw new CustomException(ErrorCode.AI_SERVICE_ERROR);
        }
    }
}
