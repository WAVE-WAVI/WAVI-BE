package com.wave.wavi.common.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVertificationEmail(String toEmail, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[WAVI] 회원가입을 위한 이메일 인증 코드입니다.");
        message.setText("WAVI 서비스에 가입해 주셔서 감사합니다.\n인증 코드: [" + verificationCode + "]");
        mailSender.send(message);
    }
}
