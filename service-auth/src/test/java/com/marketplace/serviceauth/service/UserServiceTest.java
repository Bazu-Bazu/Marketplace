package com.marketplace.serviceauth.service;

import com.marketplace.serviceauth.dto.request.RegisterUserRequest;
import com.marketplace.serviceauth.dto.request.UpdateUserRequest;
import com.marketplace.serviceauth.dto.response.UserResponse;
import com.marketplace.serviceauth.entity.User;
import com.marketplace.serviceauth.enums.Role;
import com.marketplace.serviceauth.exception.UserException;
import com.marketplace.serviceauth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RegisterUserRequest registerRequest;
    private UpdateUserRequest updateRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setBirthDate(LocalDate.of(1990, 1, 1));
        testUser.setPhone("1234567890");
        testUser.setRole(Role.ROLE_USER);
        testUser.setCreatedAt(LocalDate.now());

        registerRequest = new RegisterUserRequest();
        registerRequest.setFirstName("Jane");
        registerRequest.setLastName("Smith");
        registerRequest.setEmail("jane.smith@example.com");
        registerRequest.setPassword("password");
        registerRequest.setBirthDate(LocalDate.of(1995, 5, 15));

        updateRequest = new UpdateUserRequest();
        updateRequest.setFirstName("UpdatedJohn");
        updateRequest.setLastName("UpdatedDoe");
        updateRequest.setEmail("updated@example.com");
        updateRequest.setPassword("newPassword");
        updateRequest.setBirthDate(LocalDate.of(1991, 2, 2));
        updateRequest.setPhone("0987654321");
    }

    @Test
    void registerUser_WithNewEmail_ShouldSaveUser() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        assertDoesNotThrow(() -> userService.registerUser(registerRequest));

        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_WithExistingEmail_ShouldThrowException() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        UserException exception = assertThrows(UserException.class,
                () -> userService.registerUser(registerRequest));

        assertEquals("The user with this email already exists.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WithAllFields_ShouldUpdateAllFields() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(updateRequest.getPassword())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.updateUser(testUser.getEmail(), updateRequest);

        assertNotNull(response);
        assertEquals(updateRequest.getFirstName(), response.getFirstName());
        assertEquals(updateRequest.getLastName(), response.getLastName());
        assertEquals(updateRequest.getEmail(), response.getEmail());
        assertEquals(updateRequest.getBirthDate(), response.getBirthDate());
        assertEquals(updateRequest.getPhone(), response.getPhone());

        verify(passwordEncoder).encode(updateRequest.getPassword());
        verify(userRepository).save(testUser);
    }

    @Test
    void updateUser_WithPartialFields_ShouldUpdateOnlyProvidedFields() {
        UpdateUserRequest partialUpdate = new UpdateUserRequest();
        partialUpdate.setFirstName("PartialUpdate");
        partialUpdate.setEmail("partial@example.com");

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.updateUser(testUser.getEmail(), partialUpdate);

        assertNotNull(response);
        assertEquals(partialUpdate.getFirstName(), response.getFirstName());
        assertEquals(partialUpdate.getEmail(), response.getEmail());
        assertEquals(testUser.getLastName(), response.getLastName());
        assertEquals(testUser.getBirthDate(), response.getBirthDate());
        assertEquals(testUser.getPhone(), response.getPhone());

        verify(userRepository).save(testUser);
    }

    @Test
    void updateUser_WithOnlyPassword_ShouldEncodePassword() {
        UpdateUserRequest passwordUpdate = new UpdateUserRequest();
        passwordUpdate.setPassword("newPassword");

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.updateUser(testUser.getEmail(), passwordUpdate);

        assertNotNull(response);
        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).save(testUser);
    }

    @Test
    void updateUser_WithNonExistentUser_ShouldThrowException() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.updateUser("nonexistent@example.com", updateRequest));

        assertEquals("User not found.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateIfNotNullAndSave_WithNonNullValue_ShouldExecuteSetterAndSave() {
        TestConsumer consumer = new TestConsumer();
        TestSaveAction saveAction = new TestSaveAction();

        userService.updateIfNotNullAndSave("testValue", consumer, saveAction);

        assertTrue(consumer.wasCalled);
        assertTrue(saveAction.wasCalled);
    }

    @Test
    void updateIfNotNullAndSave_WithNullValue_ShouldNotExecuteSetterOrSave() {
        TestConsumer consumer = new TestConsumer();
        TestSaveAction saveAction = new TestSaveAction();

        userService.updateIfNotNullAndSave(null, consumer, saveAction);

        assertFalse(consumer.wasCalled);
        assertFalse(saveAction.wasCalled);
    }

    @Test
    void getUser_WithExistingUser_ShouldReturnUserResponse() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        UserResponse response = userService.getUser(testUser.getEmail());

        assertNotNull(response);
        assertEquals(testUser.getId(), response.getId());
        assertEquals(testUser.getFirstName(), response.getFirstName());
        assertEquals(testUser.getLastName(), response.getLastName());
        assertEquals(testUser.getEmail(), response.getEmail());
        assertEquals(testUser.getPhone(), response.getPhone());
        assertEquals(testUser.getRole(), response.getRole());
        assertEquals(testUser.getBirthDate(), response.getBirthDate());

        verify(userRepository).findByEmail(testUser.getEmail());
    }

    @Test
    void getUser_WithNonExistentUser_ShouldThrowException() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.getUser("nonexistent@example.com"));

        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void deleteUser_WithExistingUser_ShouldDeleteUser() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        assertDoesNotThrow(() -> userService.deleteUser(testUser.getEmail()));

        verify(userRepository).findByEmail(testUser.getEmail());
        verify(userRepository).delete(testUser);
    }

    @Test
    void deleteUser_WithNonExistentUser_ShouldThrowException() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.deleteUser("nonexistent@example.com"));

        assertEquals("User not found.", exception.getMessage());
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void findUserByEmail_WithExistingUser_ShouldReturnUser() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        User result = userService.findUserByEmail(testUser.getEmail());

        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository).findByEmail(testUser.getEmail());
    }

    @Test
    void findUserByEmail_WithNonExistentUser_ShouldThrowException() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.findUserByEmail("nonexistent@example.com"));

        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void updateIfNotNull_WithNonNullValue_ShouldCallSetter() throws Exception {
        Method method = UserService.class.getDeclaredMethod("updateIfNotNull", Object.class, Consumer.class);
        method.setAccessible(true);

        TestConsumer consumer = new TestConsumer();

        method.invoke(userService, "testValue", consumer);

        assertTrue(consumer.wasCalled);
        assertEquals("testValue", consumer.receivedValue);
    }

    @Test
    void updateIfNotNull_WithNullValue_ShouldNotCallSetter() throws Exception {
        Method method = UserService.class.getDeclaredMethod("updateIfNotNull", Object.class, Consumer.class);
        method.setAccessible(true);

        TestConsumer consumer = new TestConsumer();

        method.invoke(userService, null, consumer);

        assertFalse(consumer.wasCalled);
    }

    @Test
    void buildUserResponse_ShouldBuildCorrectResponse() throws Exception {
        Method method = UserService.class.getDeclaredMethod("buildUserResponse", User.class);
        method.setAccessible(true);

        UserResponse response = (UserResponse) method.invoke(userService, testUser);

        assertNotNull(response);
        assertEquals(testUser.getId(), response.getId());
        assertEquals(testUser.getFirstName(), response.getFirstName());
        assertEquals(testUser.getLastName(), response.getLastName());
        assertEquals(testUser.getEmail(), response.getEmail());
        assertEquals(testUser.getPhone(), response.getPhone());
        assertEquals(testUser.getRole(), response.getRole());
        assertEquals(testUser.getBirthDate(), response.getBirthDate());
    }

    private static class TestConsumer implements Consumer<String> {
        boolean wasCalled = false;
        String receivedValue = null;

        @Override
        public void accept(String value) {
            wasCalled = true;
            receivedValue = value;
        }
    }

    private static class TestSaveAction implements Runnable {
        boolean wasCalled = false;

        @Override
        public void run() {
            wasCalled = true;
        }
    }

}