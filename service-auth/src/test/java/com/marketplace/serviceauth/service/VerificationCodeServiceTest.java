package com.marketplace.serviceauth.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.lang.reflect.Method;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationCodeServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private Random random;

    @InjectMocks
    private VerificationCodeService verificationCodeService;

    @Test
    void sendVerificationCode_ShouldGenerateCodeAndSendEmail() {
        String email = "test@example.com";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(eq(email), anyString(), eq(15L), eq(TimeUnit.MINUTES));
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        verificationCodeService.sendVerificationCode(email);

        verify(valueOperations).set(eq(email), anyString(), eq(15L), eq(TimeUnit.MINUTES));
        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void verifyCode_WithValidCode_ShouldReturnTrue() {
        String email = "test@example.com";
        String code = "123456";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(email)).thenReturn(code);

        boolean result = verificationCodeService.verifyCode(email, code);

        assertTrue(result);
        verify(redisTemplate.opsForValue()).get(email);
    }

    @Test
    void verifyCode_WithInvalidCode_ShouldReturnFalse() {
        String email = "test@example.com";
        String storedCode = "123456";
        String providedCode = "654321";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(email)).thenReturn(storedCode);

        boolean result = verificationCodeService.verifyCode(email, providedCode);

        assertFalse(result);
        verify(redisTemplate.opsForValue()).get(email);
    }

    @Test
    void verifyCode_WithNoStoredCode_ShouldReturnFalse() {
        String email = "test@example.com";
        String code = "123456";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(email)).thenReturn(null);

        boolean result = verificationCodeService.verifyCode(email, code);

        assertFalse(result);
        verify(redisTemplate.opsForValue()).get(email);
    }

    @Test
    void saveCodeToRedis_ShouldSaveCodeWithExpiration() throws Exception {
        String email = "test@example.com";
        String code = "123456";

        Method method = VerificationCodeService.class.getDeclaredMethod("saveCodeToRedis", String.class, String.class);
        method.setAccessible(true);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(eq(email), eq(code), eq(15L), eq(TimeUnit.MINUTES));

        method.invoke(verificationCodeService, email, code);

        verify(redisTemplate.opsForValue()).set(email, code, 15, TimeUnit.MINUTES);
    }

    @Test
    void sendEmail_ShouldSendEmailWithCode() throws Exception {
        String email = "test@example.com";
        String code = "123456";

        Method method = VerificationCodeService.class.getDeclaredMethod("sendEmail", String.class, String.class);
        method.setAccessible(true);

        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        method.invoke(verificationCodeService, email, code);

        verify(javaMailSender).send(argThat((SimpleMailMessage message) ->
                message.getTo()[0].equals(email) &&
                        message.getSubject().equals("Код подтверждения") &&
                        message.getText().equals("Ваш код " + code)
        ));
    }

    @Test
    void sendVerificationCode_ShouldGenerateSixDigitCode() {
        String email = "test@example.com";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(eq(email), anyString(), eq(15L), eq(TimeUnit.MINUTES));
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        verificationCodeService.sendVerificationCode(email);

        verify(valueOperations).set(eq(email), argThat(code ->
                code != null &&
                        code.length() == 6 &&
                        code.matches("\\d{6}")
        ), eq(15L), eq(TimeUnit.MINUTES));
    }

    @Test
    void verifyCode_WithEmptyCode_ShouldReturnFalse() {
        String email = "test@example.com";
        String storedCode = "123456";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(email)).thenReturn(storedCode);

        boolean result = verificationCodeService.verifyCode(email, "");

        assertFalse(result);
    }

    @Test
    void verifyCode_WithWhitespaceCode_ShouldReturnFalse() {
        String email = "test@example.com";
        String storedCode = "123456";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(email)).thenReturn(storedCode);

        boolean result = verificationCodeService.verifyCode(email, "   ");

        assertFalse(result);
    }

}