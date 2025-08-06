package com.wave.wavi.habit.controller;

import com.wave.wavi.common.ResponseDto;
import com.wave.wavi.habit.dto.HabitRequestDto;
import com.wave.wavi.habit.service.HabitService;
import com.wave.wavi.user.model.LoginType;
import com.wave.wavi.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/habit")
public class HabitController {

    private final HabitService habitService;

    // 습관 등록
    @PostMapping("")
    public ResponseDto<Object> save(@RequestBody HabitRequestDto requestDto/*, @AuthenticationPrincipal PrincipalDetail principal*/) {
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
        habitService.saveDaysOfWeek(requestDto, habitId);

        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("습관 등록 성공")
                .build();
    }

    // 습관 수정
    @PatchMapping("/{habitId}")
    public ResponseDto<Object> update(@RequestBody HabitRequestDto requestDto, @PathVariable Long habitId) {
        habitService.updateHabit(requestDto, habitId);
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("습관 수정 성공")
                .build();
    }

    // 습관 삭제
    @DeleteMapping("/{habitId}")
    public ResponseDto<Object> delete(@PathVariable Long habitId) {
        habitService.deleteHabit(habitId);
        return ResponseDto.builder()
                .status(HttpStatus.OK.value())
                .message("습관 삭제 성공")
                .build();
    }
}
