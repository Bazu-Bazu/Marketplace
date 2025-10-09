package com.marketplace.serviceauth.service;

import com.marketplace.serviceauth.dto.CustomUserDetails;
import com.marketplace.serviceauth.dto.event.UserEvent;
import com.marketplace.serviceauth.dto.request.LoginUserRequest;
import com.marketplace.serviceauth.dto.request.RefreshTokenRequest;
import com.marketplace.serviceauth.dto.request.RegisterUserRequest;
import com.marketplace.serviceauth.dto.request.VerifyUserRequest;
import com.marketplace.serviceauth.dto.response.AuthResponse;
import com.marketplace.serviceauth.entity.RefreshToken;
import com.marketplace.serviceauth.entity.Seller;
import com.marketplace.serviceauth.entity.User;
import com.marketplace.serviceauth.enums.Role;
import com.marketplace.serviceauth.exception.AuthenticationException;
import com.marketplace.serviceauth.exception.RefreshTokenException;
import com.marketplace.serviceauth.exception.VerificationCodeException;
import com.marketplace.serviceauth.repository.RefreshTokenRepository;
import com.marketplace.serviceauth.service.event.UserEventPublisher;
import com.marketplace.serviceauth.service.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private VerificationCodeService verificationCodeService;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private SellerService sellerService;

    @Mock
    private UserEventPublisher userEventPublisher;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private Seller testSeller;
    private LoginUserRequest loginRequest;
    private RegisterUserRequest registerRequest;
    private VerifyUserRequest verifyRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setEnabled(false);
        testUser.setRole(Role.ROLE_USER);

        testSeller = new Seller();
        testSeller.setId(1L);
        testSeller.setEmail("seller@example.com");
        testSeller.setName("Test Company");

        loginRequest = new LoginUserRequest();
        loginRequest.setEmail("email");
        loginRequest.setPassword("password");

        registerRequest = new RegisterUserRequest();
        registerRequest.setEmail("email");
        registerRequest.setPassword("password");
        registerRequest.setFirstName("FirstName");
        registerRequest.setLastName("LastName");
        registerRequest.setBirthDate(LocalDate.now());

        verifyRequest = new VerifyUserRequest();
        verifyRequest.setEmail("email");
        verifyRequest.setVerificationCode("123456");
    }

    @Test
    void signUp_ShouldRegisterUserAndSendVerificationCode() {
        authService.signUp(registerRequest);

        verify(userService).registerUser(registerRequest);
        verify(verificationCodeService).sendVerificationCode(registerRequest.getEmail());
    }

    @Test
    void authenticate_WithValidCredentialsAndVerifiedUser_ShouldReturnTokens() {
        testUser.setEnabled(true);
        String deviceInfo = "Chrome";
        String ipAddress = "192.168.1.1";
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";

        when(userService.findUserByEmail(loginRequest.getEmail())).thenReturn(testUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));
        when(jwtService.generateAccessToken(any(UserDetails.class))).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn(refreshToken);

        AuthResponse response = authService.authenticate(loginRequest, deviceInfo, ipAddress);

        assertNotNull(response);
        assertEquals(testUser.getId(), response.getUserId());
        assertEquals(accessToken, response.getAccessToken());
        assertEquals(refreshToken, response.getRefreshToken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateAccessToken(any(UserDetails.class));
        verify(jwtService).generateRefreshToken(any(UserDetails.class));
        verify(refreshTokenService).addRefreshToken(testUser, refreshToken, deviceInfo, ipAddress);
    }

    @Test
    void authenticate_WithUnverifiedUser_ShouldThrowException() {
        testUser.setEnabled(false);
        when(userService.findUserByEmail(loginRequest.getEmail())).thenReturn(testUser);

        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> authService.authenticate(loginRequest, "device", "ip")
        );

        assertEquals("Account not verified.", exception.getMessage());
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void authenticate_WithInvalidCredentials_ShouldThrowException() {
        testUser.setEnabled(true);
        when(userService.findUserByEmail(loginRequest.getEmail())).thenReturn(testUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(
                BadCredentialsException.class,
                () -> authService.authenticate(loginRequest, "device", "ip")
        );
    }

    @Test
    void createUserDetails_ForRegularUser_ShouldCreateUserDetails()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Method method = AuthService.class.getDeclaredMethod("createUserDetails", User.class);
        method.setAccessible(true);

        UserDetails userDetails = (UserDetails) method.invoke(authService, testUser);

        assertNotNull(userDetails);
        assertTrue(userDetails instanceof CustomUserDetails);
        verify(sellerService, never()).findSellerByEmail(anyString());
    }

    @Test
    void createUserDetails_ForSeller_ShouldCreateSellerUserDetails()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        testUser.setRole(Role.ROLE_SELLER);
        testUser.setEmail("seller");
        when(sellerService.findSellerByEmail("seller")).thenReturn(testSeller);

        Method method = AuthService.class.getDeclaredMethod("createUserDetails", User.class);
        method.setAccessible(true);

        UserDetails userDetails = (UserDetails) method.invoke(authService, testUser);

        assertNotNull(userDetails);
        assertTrue(userDetails instanceof CustomUserDetails);
        verify(sellerService).findSellerByEmail("seller");
    }

    @Test
    void verifyUser_WithValidCode_ShouldEnableUserAndReturnResponse() {
        when(userService.findUserByEmail(verifyRequest.getEmail())).thenReturn(testUser);
        when(verificationCodeService.verifyCode(verifyRequest.getEmail(), verifyRequest.getVerificationCode()))
                .thenReturn(true);

        AuthResponse response = authService.verifyUser(verifyRequest);

        assertNotNull(response);
        assertEquals(testUser.getId(), response.getUserId());
        assertNull(response.getAccessToken());
        assertNull(response.getRefreshToken());

        verify(userService).updateIfNotNullAndSave(eq(true), any(), any());
        verify(verificationCodeService).verifyCode(verifyRequest.getEmail(), verifyRequest.getVerificationCode());
        verify(userEventPublisher).sendUserToKafka(any(UserEvent.class));
    }

    @Test
    void verifyUser_WithInvalidCode_ShouldThrowException() {
        when(userService.findUserByEmail(verifyRequest.getEmail())).thenReturn(testUser);
        when(verificationCodeService.verifyCode(verifyRequest.getEmail(), verifyRequest.getVerificationCode()))
                .thenReturn(false);

        VerificationCodeException exception = assertThrows(
                VerificationCodeException.class,
                () -> authService.verifyUser(verifyRequest)
        );

        assertEquals("Invalid verification code.", exception.getMessage());
        verify(userService, never()).updateIfNotNullAndSave(any(), any(), any());
        verify(userEventPublisher, never()).sendUserToKafka(any(UserEvent.class));
    }

    @Test
    void resendVerificationCode_ForUnverifiedUser_ShouldSendCode() {
        testUser.setEnabled(false);
        when(userService.findUserByEmail("test@example.com")).thenReturn(testUser);

        AuthResponse response = authService.resendVerificationCode("test@example.com");

        assertNotNull(response);
        assertEquals(testUser.getId(), response.getUserId());
        verify(verificationCodeService).sendVerificationCode("test@example.com");
    }

    @Test
    void resendVerificationCode_ForVerifiedUser_ShouldThrowException() {
        testUser.setEnabled(true);
        when(userService.findUserByEmail("test@example.com")).thenReturn(testUser);

        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> authService.resendVerificationCode("test@example.com")
        );

        assertEquals("Account is already verified.", exception.getMessage());
        verify(verificationCodeService, never()).sendVerificationCode(anyString());
    }

    @Test
    void refresh_WithValidToken_ShouldReturnNewTokens() {
        String oldRefreshToken = "oldRefreshToken";
        String deviceInfo = "Chrome";
        String ipAddress = "192.168.1.1";
        String newAccessToken = "newAccessToken";
        String newRefreshToken = "newRefreshToken";
        String email = "email";

        RefreshToken storedToken = new RefreshToken();
        storedToken.setToken(oldRefreshToken);
        storedToken.setDeviceInfo(deviceInfo);

        when(jwtService.isRefreshToken(oldRefreshToken)).thenReturn(true);
        when(jwtService.extractUsername(oldRefreshToken)).thenReturn(email);
        when(userService.findUserByEmail(email)).thenReturn(testUser);
        when(refreshTokenRepository.findByToken(oldRefreshToken)).thenReturn(Optional.of(storedToken));
        when(jwtService.generateAccessToken(any(UserDetails.class))).thenReturn(newAccessToken);
        when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn(newRefreshToken);

        AuthResponse response = authService.refresh(oldRefreshToken, deviceInfo, ipAddress);

        assertNotNull(response);
        assertEquals(testUser.getId(), response.getUserId());
        assertEquals(newAccessToken, response.getAccessToken());
        assertEquals(newRefreshToken, response.getRefreshToken());

        verify(refreshTokenService).verifyActivity(storedToken);
        verify(refreshTokenService).revokeToken(oldRefreshToken);
        verify(refreshTokenService).addRefreshToken(testUser, newRefreshToken, deviceInfo, ipAddress);
    }

    @Test
    void refresh_WithInvalidDevice_ShouldThrowException() {
        String refreshToken = "refresh-token";
        String deviceInfo = "Chrome";
        String differentDeviceInfo = "Firefox";
        String email = "test@example.com";

        RefreshToken storedToken = new RefreshToken();
        storedToken.setToken(refreshToken);
        storedToken.setDeviceInfo(differentDeviceInfo);

        when(jwtService.isRefreshToken(refreshToken)).thenReturn(true);
        when(jwtService.extractUsername(refreshToken)).thenReturn(email);
        when(userService.findUserByEmail(email)).thenReturn(testUser);
        when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(Optional.of(storedToken));

        AuthenticationException exception = assertThrows(
                AuthenticationException.class,
                () -> authService.refresh(refreshToken, deviceInfo, "ip")
        );

        assertEquals("Invalid device for refresh token.", exception.getMessage());
        verify(refreshTokenService, never()).addRefreshToken(any(), any(), any(), any());
    }

    @Test
    void refresh_WithNonRefreshToken_ShouldThrowException() {
        String accessToken = "access-token";
        when(jwtService.isRefreshToken(accessToken)).thenReturn(false);

        RefreshTokenException exception = assertThrows(
                RefreshTokenException.class,
                () -> authService.refresh(accessToken, "device", "ip")
        );

        assertEquals("Provided token is not a refresh token.", exception.getMessage());
    }

    @Test
    void refresh_WithNotFoundToken_ShouldThrowException() {
        String refreshToken = "non-existent-token";
        String email = "test@example.com";

        when(jwtService.isRefreshToken(refreshToken)).thenReturn(true);
        when(jwtService.extractUsername(refreshToken)).thenReturn(email);
        when(userService.findUserByEmail(email)).thenReturn(testUser);
        when(refreshTokenRepository.findByToken(refreshToken)).thenReturn(Optional.empty());

        RefreshTokenException exception = assertThrows(
                RefreshTokenException.class,
                () -> authService.refresh(refreshToken, "device", "ip")
        );

        assertEquals("Refresh token not found.", exception.getMessage());
    }

    @Test
    void logout_ShouldRevokeToken() {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("refreshToken");

        authService.logout(request);

        verify(refreshTokenService).revokeToken(request.getRefreshToken());
    }

    @Test
    void logoutAll_ShouldRevokeAllUserTokens() {
        String email = "email";
        when(userService.findUserByEmail(email)).thenReturn(testUser);

        authService.logoutAll(email);

        verify(refreshTokenService).revokeAllUserTokens(testUser);
    }

}