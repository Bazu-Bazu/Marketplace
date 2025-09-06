package marketplace.User.Auth.Service.controller;

import lombok.RequiredArgsConstructor;
import marketplace.User.Auth.Service.dto.request.RegisterSellerRequest;
import marketplace.User.Auth.Service.dto.request.UpdateSellerRequest;
import marketplace.User.Auth.Service.dto.response.SellerResponse;
import marketplace.User.Auth.Service.service.SellerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @PostMapping("/register")
    public ResponseEntity<SellerResponse> registerSeller(@AuthenticationPrincipal UserDetails userDetails,
                                                         @RequestBody RegisterSellerRequest request) {
        String email = userDetails.getUsername();

        SellerResponse response = sellerService.registerSeller(email, request);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<SellerResponse> updateSeller(@AuthenticationPrincipal UserDetails userDetails,
                                                       @RequestBody UpdateSellerRequest request) {
        String email = userDetails.getUsername();

        SellerResponse response = sellerService.updateSeller(email, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/get")
    public ResponseEntity<SellerResponse> getSeller(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();

        SellerResponse response = sellerService.getSeller(email);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<SellerResponse> deleteSeller(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();

        SellerResponse response = sellerService.deleteSeller(email);

        return ResponseEntity.ok(response);
    }

}
