package com.wave.wavi.habit.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.wave.wavi.habit.dto.ChatAnalysisRequestDto;
import com.wave.wavi.habit.dto.HabitRequestDto;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatAnalysisService {

    private final Client client;

    public Object analyzePrompt(ChatAnalysisRequestDto requestDto) {
        String prompt = String.format("""
다음의 대화 history와 현재 발화(currentPrompt)를 모두 함께 고려하여 습관을 등록하세요. 필요한 정보가 부족하면 한 번에 모두 물어보도록 `ask`를 구성하세요.

당신은 습관 등록 전문가입니다. 사용자가 입력한 자연어 메시지를 분석하여 습관 정보를 구조화된 JSON 형태로 변환해주세요.

**대화 History:**
%s

**현재 발화(currentPrompt):**
%s

**출력 형식 (JSON):**
{
    "icon": "습관에 맞는 번호",
    "name": "습관 이름 (어떤 습관을 몇분/몇회 하겠다)",
    "startTime": 수행 가능 시작 시간 (HH:MM:SS 형식),
    "endTime": 수행 가능 종료 시간 (HH:MM:SS 형식),
    "dayOfWeek": [요일 배열 (1=월, 2=화, 3=수, 4=목, 5=금, 6=토, 7=일)]
}

**분석 가이드라인:**
1. **icon**: 습관의 성격에 맞는 번호 선택
   - 코딩/프로그래밍: 1
   - 운동/헬스: 2
   - 독서/학습: 3
   - 음악: 4
   - 건강/식단: 5
   - 명상/요가: 6
   - 커피/음료: 7
   - 예술/창작: 8

2. **name**: 구체적이고 명확한 습관명
   - "운동 30분" (시간 기반)
   - "팔굽혀펴기 30개" (횟수 기반)
   - "책 읽기 1시간" (시간 기반)

3. **startTime**: 습관 수행 범위 시작 시간 (HH:MM:SS 형식)
   - 09:00:00 (이 시간부터 습관 수행 가능)

4. **endTime**: 습관 수행 범위 종료 시간 (HH:MM:SS 형식)
   - 11:00:00 (이 시간까지 습관 수행 가능)

5. **dayOfWeek**: 요일 배열
   - [1, 3, 5] (월, 수, 금)
   - [1, 2, 3, 4, 5] (평일)
   - [6, 7] (주말)

**예시:**
- "매일 아침 9시에 코딩 1시간씩 하고 싶어"
  → {"icon": 1, "name": "코딩 1시간", "startTime": "09:00:00", "endTime": "10:00:00", "dayOfWeek": [1, 2, 3, 4, 5, 6, 7]}

- "오전 9시~11시 사이에 코딩 1시간"
  → {"icon": 1, "name": "코딩 1시간", "startTime": "09:00:00", "endTime": "11:00:00", "dayOfWeek": [1, 2, 3, 4, 5, 6, 7]}

- "월수금 저녁 7시~9시 사이에 운동 30분"
  → {"icon": 2, "name": "운동 30분", "startTime": "19:00:00", "endTime": "21:00:00", "dayOfWeek": [1, 3, 5]}

**중요사항:**
- 반드시 유효한 JSON 형식으로 출력
- 시간은 24시간 형식 (HH:MM:SS)
- 요일은 숫자로 표현 (1=월요일, 7=일요일)
- 사용자가 명시하지 않은 정보는 임의로 추정하지 마세요
- startTime과 endTime은 습관을 수행할 수 있는 시간 범위를 나타냅니다 (예: 9시~11시 사이에 언제든 1시간 코딩)
- 필수 필드 중 하나라도 확실히 채울 수 없으면 다음 규칙을 따르세요:
  1) 모든 필수 키(`icon`, `name`, `startTime`, `endTime`, `dayOfWeek`)는 반드시 포함하되, 알 수 없는 값은 null 로 설정
  2) 다음 보조 키를 함께 포함: `needMoreInfo`: true, `ask`: "누락된 모든 정보를 한 번에 요청하는 한국어 한 문장"
  3) `ask`에는 구체적으로 어떤 항목이 필요한지 함께 명시 (예: "수행 가능한 시간 범위(시작~종료 시간)와 요일을 알려주세요.")
- 모든 정보가 충분하면 `needMoreInfo`는 false 로 설정하거나 생략해도 됩니다
- 오직 JSON만 출력하고 다른 설명은 포함하지 마세요

**부족 정보 처리 예시:**
- "코딩 1시간씩 하고 싶어"
  → {"icon": 1, "name": "코딩 1시간", "startTime": null, "endTime": null, "dayOfWeek": null, "needMoreInfo": true, "ask": "수행 가능한 시간 범위(시작~종료 시간)와 요일을 알려주세요."}
""", String.join("\n", requestDto.getHistory()), requestDto.getCurrentPrompt());

        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-2.5-flash",
                        prompt,
                        null);

        String responseData = response.text();

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;
        try {
            jsonObject = (JSONObject) parser.parse(responseData.substring(8, responseData.length() - 4));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Object needMoreInfo = jsonObject.get("needMoreInfo");
        if (needMoreInfo != null && Boolean.TRUE.equals(Boolean.valueOf(needMoreInfo.toString()))) {
            return jsonObject.get("ask").toString();
        }

        return HabitRequestDto.builder()
                .icon(Long.valueOf(jsonObject.get("icon").toString()))
                .name(jsonObject.get("name").toString())
                .startTime(LocalTime.parse(jsonObject.get("startTime").toString()))
                .endTime(LocalTime.parse(jsonObject.get("endTime").toString()))
                .dayOfWeek((List<Integer>) jsonObject.get("dayOfWeek"))
                .build();
    }
}
