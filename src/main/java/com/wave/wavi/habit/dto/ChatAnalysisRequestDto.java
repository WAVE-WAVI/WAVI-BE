package com.wave.wavi.habit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatAnalysisRequestDto {
    private String currentPrompt;
    private List<String> history;
}
