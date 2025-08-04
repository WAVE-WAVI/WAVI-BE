package com.wave.wavi.habit.controller;

import com.wave.wavi.common.ResponseDto;
import com.wave.wavi.habit.dto.HabitSaveRequestDto;
import com.wave.wavi.habit.service.HabitService;
import com.wave.wavi.user.model.LoginType;
import com.wave.wavi.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/habit")
public class HabitController {

    private final HabitService habitService;

    // 습관 등록
    @PostMapping("")
    public ResponseDto<Object> save(@RequestBody HabitSaveRequestDto requestDto/*, @AuthenticationPrincipal PrincipalDetail principal*/) {
        // 더미 유저 정보
        User user = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .loginType(LoginType.NORMAL)
                .nickname("test")
                .password("1234")
                .profileImage(Long.valueOf(2))
                .build();

        Long habitId = habitService.saveHabit(requestDto, user /*principal.getUser()*/);
        habitService.saveWeekOfDays(requestDto, habitId);

        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("습관 등록 성공")
                .build();
    }
}
