package com.burkina.marketplace.service;

import com.burkina.marketplace.domain.entity.SellerEmail;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SellerEmailService {

    public List<SellerEmail> createEmails(List<String> emails) {
        return emails.stream()
                .map(SellerEmail::new)
                .toList();
    }
}
