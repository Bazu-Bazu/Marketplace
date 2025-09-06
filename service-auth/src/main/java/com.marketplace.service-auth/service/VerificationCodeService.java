package marketplace.User.Auth.Service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class VerificationCodeService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JavaMailSender javaMailSender;

    public void sendVerificationCode(String email) {
        String code = String.format("%06d", new Random().nextInt(999999));
        saveCodeToRedis(email, code);
        sendEmail(email, code);
    }

    public boolean verifyCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get(email);

        return code != null && code.equals(storedCode);
    }

    private void saveCodeToRedis(String email, String code) {
        redisTemplate.opsForValue().set(
                email,
                code,
                15,
                TimeUnit.MINUTES
        );
    }

    private void sendEmail(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Код подтверждения");
        message.setText("Ваш код " + code);
        javaMailSender.send(message);
    }

}
