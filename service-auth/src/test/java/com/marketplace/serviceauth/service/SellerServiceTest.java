package com.marketplace.serviceauth.service;

import com.marketplace.serviceauth.dto.request.RegisterSellerRequest;
import com.marketplace.serviceauth.dto.request.UpdateSellerRequest;
import com.marketplace.serviceauth.dto.response.SellerResponse;
import com.marketplace.serviceauth.entity.Seller;
import com.marketplace.serviceauth.entity.User;
import com.marketplace.serviceauth.enums.Role;
import com.marketplace.serviceauth.exception.SellerException;
import com.marketplace.serviceauth.repository.SellerRepository;
import com.marketplace.serviceauth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private SellerService sellerService;

    private User testUser;
    private Seller testSeller;
    private RegisterSellerRequest registerRequest;
    private UpdateSellerRequest updateRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.ROLE_USER);

        testSeller = new Seller();
        testSeller.setId(1L);
        testSeller.setName("Test Seller");
        testSeller.setEmail("test@example.com");
        testSeller.setUser(testUser);

        registerRequest = new RegisterSellerRequest();
        registerRequest.setName("New Seller");

        updateRequest = new UpdateSellerRequest();
        updateRequest.setName("Updated Seller");
        updateRequest.setEmail("updated@example.com");
    }

    @Test
    void registerSeller_WithValidUser_ShouldCreateSeller() {
        when(userService.findUserByEmail(testUser.getEmail())).thenReturn(testUser);
        when(sellerRepository.save(any(Seller.class))).thenReturn(testSeller);

        SellerResponse response = sellerService.registerSeller(testUser.getEmail(), registerRequest);

        assertNotNull(response);
        assertEquals(testSeller.getEmail(), response.getEmail());
        assertEquals(testSeller.getEmail(), response.getEmail());

        verify(userService).findUserByEmail(testUser.getEmail());
        verify(sellerRepository).save(any(Seller.class));
        verify(userService).updateIfNotNullAndSave(eq(Role.ROLE_SELLER), any(), any());
    }

    @Test
    void registerSeller_WithExistingSeller_ShouldThrowException() {
        testUser.setRole(Role.ROLE_SELLER);
        when(userService.findUserByEmail(testUser.getEmail())).thenReturn(testUser);

        SellerException exception = assertThrows(SellerException.class,
                () -> sellerService.registerSeller(testUser.getEmail(), registerRequest));

        assertEquals("Seller with this email already exists.", exception.getMessage());
        verify(sellerRepository, never()).save(any(Seller.class));
    }

    @Test
    void updateSeller_WithAllFields_ShouldUpdateSellerAndUser() {
        when(sellerRepository.findByEmail(testSeller.getEmail())).thenReturn(Optional.of(testSeller));
        when(userService.findUserByEmail(testSeller.getEmail())).thenReturn(testUser);
        when(sellerRepository.save(any(Seller.class))).thenReturn(testSeller);

        SellerResponse response = sellerService.updateSeller(testSeller.getEmail(), updateRequest);

        assertNotNull(response);
        assertEquals(updateRequest.getName(), response.getName());
        assertEquals(updateRequest.getEmail(), response.getEmail());

        verify(sellerRepository).save(testSeller);
        verify(userService).updateIfNotNullAndSave(eq(updateRequest.getEmail()), any(), any());
    }

    @Test
    void updateSeller_WithOnlyName_ShouldUpdateOnlyName() {
        UpdateSellerRequest nameOnlyUpdate = new UpdateSellerRequest();
        nameOnlyUpdate.setName("New Name Only");

        when(sellerRepository.findByEmail(testSeller.getEmail())).thenReturn(Optional.of(testSeller));
        when(userService.findUserByEmail(testSeller.getEmail())).thenReturn(testUser);
        when(sellerRepository.save(any(Seller.class))).thenReturn(testSeller);

        SellerResponse response = sellerService.updateSeller(testSeller.getEmail(), nameOnlyUpdate);

        assertNotNull(response);
        assertEquals(nameOnlyUpdate.getName(), response.getName());
        assertEquals(testSeller.getEmail(), response.getEmail());

        verify(sellerRepository).findByEmail(testSeller.getEmail());
        verify(userService).findUserByEmail(testSeller.getEmail());
        verify(sellerRepository).save(testSeller);
        verify(userService, never()).updateIfNotNullAndSave(any(), any(), any());
    }

    @Test
    void updateSeller_WithOnlyEmail_ShouldUpdateEmail() {
        UpdateSellerRequest emailOnlyUpdate = new UpdateSellerRequest();
        emailOnlyUpdate.setEmail("newemail@example.com");

        when(sellerRepository.findByEmail(testSeller.getEmail())).thenReturn(Optional.of(testSeller));
        when(userService.findUserByEmail(testSeller.getEmail())).thenReturn(testUser);
        when(sellerRepository.save(any(Seller.class))).thenReturn(testSeller);

        SellerResponse response = sellerService.updateSeller(testSeller.getEmail(), emailOnlyUpdate);

        assertNotNull(response);
        assertEquals(emailOnlyUpdate.getEmail(), response.getEmail());
        assertEquals(testSeller.getName(), response.getName());

        verify(sellerRepository).save(testSeller);
        verify(userService).updateIfNotNullAndSave(eq(emailOnlyUpdate.getEmail()), any(), any());
    }

    @Test
    void updateSeller_WithNonExistentSeller_ShouldThrowException() {
        when(sellerRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        SellerException exception = assertThrows(SellerException.class,
                () -> sellerService.updateSeller("nonexistent@example.com", updateRequest));

        assertEquals("Seller not found.", exception.getMessage());
        verify(sellerRepository, never()).save(any(Seller.class));
    }

    @Test
    void getSeller_WithExistingSeller_ShouldReturnSellerResponse() {
        when(sellerRepository.findByEmail(testSeller.getEmail())).thenReturn(Optional.of(testSeller));

        SellerResponse response = sellerService.getSeller(testSeller.getEmail());

        assertNotNull(response);
        assertEquals(testSeller.getId(), response.getId());
        assertEquals(testSeller.getName(), response.getName());
        assertEquals(testSeller.getEmail(), response.getEmail());

        verify(sellerRepository).findByEmail(testSeller.getEmail());
    }

    @Test
    void getSeller_WithNonExistentSeller_ShouldThrowException() {
        when(sellerRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        SellerException exception = assertThrows(SellerException.class,
                () -> sellerService.getSeller("nonexistent@example.com"));

        assertEquals("Seller not found.", exception.getMessage());
    }

    @Test
    void deleteSeller_WithExistingSeller_ShouldDeleteSellerAndUpdateUserRole() {
        when(sellerRepository.findByEmail(testSeller.getEmail())).thenReturn(Optional.of(testSeller));
        when(userService.findUserByEmail(testSeller.getEmail())).thenReturn(testUser);

        sellerService.deleteSeller(testSeller.getEmail());

        verify(sellerRepository).findByEmail(testSeller.getEmail());
        verify(userService).findUserByEmail(testSeller.getEmail());
        verify(sellerRepository).delete(testSeller);
        verify(userService).updateIfNotNullAndSave(eq(Role.ROLE_USER), any(), any());
    }

    @Test
    void deleteSeller_WithNonExistentSeller_ShouldThrowException() {
        when(sellerRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        SellerException exception = assertThrows(SellerException.class,
                () -> sellerService.deleteSeller("nonexistent@example.com"));

        assertEquals("Seller not found.", exception.getMessage());
        verify(sellerRepository, never()).delete(any(Seller.class));
    }

    @Test
    void findSellerByEmail_WithExistingSeller_ShouldReturnSeller() {
        when(sellerRepository.findByEmail(testSeller.getEmail())).thenReturn(Optional.of(testSeller));

        Seller result = sellerService.findSellerByEmail(testSeller.getEmail());

        assertNotNull(result);
        assertEquals(testSeller, result);
        verify(sellerRepository).findByEmail(testSeller.getEmail());
    }

    @Test
    void findSellerByEmail_WithNonExistentSeller_ShouldThrowException() {
        when(sellerRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        SellerException exception = assertThrows(SellerException.class,
                () -> sellerService.findSellerByEmail("nonexistent@example.com"));

        assertEquals("Seller not found.", exception.getMessage());
    }

    @Test
    void buildSellerResponse_ShouldBuildCorrectResponse() throws Exception {
        Method method = SellerService.class.getDeclaredMethod("buildSellerResponse", Seller.class);
        method.setAccessible(true);

        SellerResponse response = (SellerResponse) method.invoke(sellerService, testSeller);

        assertNotNull(response);
        assertEquals(testSeller.getId(), response.getId());
        assertEquals(testSeller.getName(), response.getName());
        assertEquals(testSeller.getEmail(), response.getEmail());
    }

}