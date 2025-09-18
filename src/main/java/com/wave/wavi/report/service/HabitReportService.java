package com.wave.wavi.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wave.wavi.habit.model.Habit;
import com.wave.wavi.habit.model.HabitSchedule;
import com.wave.wavi.habit.repository.HabitRepository;
import com.wave.wavi.habit.repository.HabitScheduleRepository;
import com.wave.wavi.log.model.HabitFailureLog;
import com.wave.wavi.log.model.HabitLog;
import com.wave.wavi.log.repository.HabitLogRepository;
import com.wave.wavi.report.dto.RecommendationResponseDto;
import com.wave.wavi.report.dto.ReportGenerateRequestDto;
import com.wave.wavi.report.dto.ReportRequestDto;
import com.wave.wavi.report.dto.TopFailureReasonResponseDto;
import com.wave.wavi.report.model.HabitReport;
import com.wave.wavi.report.model.Recommendation;
import com.wave.wavi.report.model.ReportType;
import com.wave.wavi.report.model.TopFailureReason;
import com.wave.wavi.report.repository.HabitReportRepository;
import com.wave.wavi.report.repository.RecommendationRepository;
import com.wave.wavi.report.repository.TopFailureReasonRepository;
import com.wave.wavi.user.model.User;
import com.wave.wavi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class HabitReportService {

    private final UserRepository userRepository;
    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;
    private final HabitScheduleRepository habitScheduleRepository;
    private final HabitReportRepository habitReportRepository;
    private final TopFailureReasonRepository topFailureReasonRepository;
    private final RecommendationRepository recommendationRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${api.fastapi.base-url}")
    private String baseUrl;

    @Transactional
    public void generateReport(@RequestBody ReportRequestDto requestDto, String email) {
        // 유저 정보 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 email의 유저를 찾지 못했습니다."));

        // 유저의 습관들, 기록들 조회
        List<Habit> habits = habitRepository.findByUserId(user.getId());
        List<HabitLog> logs = habitLogRepository.findByUserIdAndDateBetween(user.getId(), requestDto.getStartDate(), requestDto.getEndDate());

        // 습관별 dto 리스트 형성
        List<ReportGenerateRequestDto.Habit> habitDtos = new ArrayList<>();
        for (Habit habit : habits) {
            // 요일 리스트 형성
            List<Integer> daysOfWeek = new ArrayList<>();
            List<HabitSchedule> habitSchedules = habitScheduleRepository.findByHabitId(habit.getId());
            habitSchedules.forEach(habitSchedule -> daysOfWeek.add(habitSchedule.getDayOfWeek()));

            // 습관별 기록 필터링
            List<HabitLog> filteredLogs = logs
                    .stream()
                    .filter(log -> Objects.equals(log.getHabit().getId(), habit.getId()))
                    .toList();

            // 기록별 dto 리스트 형성
            List<ReportGenerateRequestDto.HabitLog> habitLogDtos = new ArrayList<>();
            for (HabitLog log : filteredLogs) {
                // 실패 이유 리스트 형성
                List<HabitFailureLog> failureLogs = log.getFailureLogs();
                List<String> failureReasons = failureLogs
                        .stream()
                        .map(failureLog ->
                                failureLog.getReason() == null
                                        ? failureLog.getCustomReason()
                                        : failureLog.getReason().getReason())
                        .toList();

                // 기록 dto 형성
                ReportGenerateRequestDto.HabitLog habitLogDto = ReportGenerateRequestDto.HabitLog.builder()
                        .date(log.getDate())
                        .completed(log.isCompleted())
                        .failureReason(failureReasons)
                        .build();
                habitLogDtos.add(habitLogDto);
            }

            // 습관 dto 형성
            ReportGenerateRequestDto.Habit habitDto = ReportGenerateRequestDto.Habit.builder()
                    .habitId(habit.getId())
                    .name(habit.getName())
                    .dayOfWeek(daysOfWeek)
                    .startTime(habit.getStartTime())
                    .endTime(habit.getEndTime())
                    .habitLog(habitLogDtos)
                    .build();
            habitDtos.add(habitDto);
        }

        // 리포트 생성을 위한 request dto 형성
        ReportGenerateRequestDto reportGenerateRequestDto = ReportGenerateRequestDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .birthYear(user.getBirthYear())
                .gender(user.getGender())
                .job(user.getJob())
                .type(requestDto.getType())
                .startDate(requestDto.getStartDate())
                .endDate(requestDto.getEndDate())
                .habits(habitDtos)
                .build();

        // FastAPI 서버에 요청
        // Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Body 설정
        String jsonBody = "";
        try {
            jsonBody = objectMapper.writeValueAsString(reportGenerateRequestDto);
            log.info("FastAPI로 전송할 실제 JSON 데이터: {}", jsonBody);
        } catch (Exception e) {
            log.error("JSON 변환 중 오류 발생", e);
        }

        // 요청 Entity 생성
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        // 요청
        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/reports/generate", HttpMethod.POST, entity, String.class);

        // 통신 성공 시 DB에 저장
        // 응답 json parsing
        JSONParser parser = new JSONParser();
        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) parser.parse(response.getBody());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // 기록 저장
        HabitReport habitReport = HabitReport.builder()
                .user(user)
                .type(Objects.equals(jsonObject.get("type").toString(), "monthly") ? ReportType.MONTHLY : ReportType.WEEKLY)
                .startDate(LocalDate.parse(jsonObject.get("start_date").toString()))
                .endDate(LocalDate.parse(jsonObject.get("end_date").toString()))
                .summary(jsonObject.get("summary").toString())
                .build();
        HabitReport savedHabitReport = habitReportRepository.save(habitReport);

        try {
            // 탑 실패 이유들 저장
            List<TopFailureReasonResponseDto> topFailureReasonResponseDtos = objectMapper.readValue(jsonObject.get("top_failure_reasons").toString(), new TypeReference<>() {});
            for (TopFailureReasonResponseDto topFailureReasonResponseDto : topFailureReasonResponseDtos) {
                for (int i = 1; i <= topFailureReasonResponseDto.getReasons().size(); i++) {
                    TopFailureReason topFailureReason = TopFailureReason.builder()
                            .habit(habitRepository.findById(topFailureReasonResponseDto.getHabitId()).orElse(null))
                            .habitReport(savedHabitReport)
                            .priority((long) i)
                            .reason(topFailureReasonResponseDto.getReasons().get(i - 1))
                            .build();
                    topFailureReasonRepository.save(topFailureReason);
                }
            }

            // 추천 저장
            List<RecommendationResponseDto> recommendationResponseDtos = objectMapper.readValue(jsonObject.get("recommendation").toString(), new TypeReference<>() {});
            for (RecommendationResponseDto recommendationResponseDto : recommendationResponseDtos) {
                Recommendation recommendation = Recommendation.builder()
                        .habit(habitRepository.findById(recommendationResponseDto.getHabitId()).orElse(null))
                        .habitReport(savedHabitReport)
                        .name(recommendationResponseDto.getName())
                        .startTime(recommendationResponseDto.getStartTime())
                        .endTime(recommendationResponseDto.getEndTime())
                        .dayOfWeek(recommendationResponseDto.getDayOfWeek().toString())
                        .build();
                recommendationRepository.save(recommendation);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
