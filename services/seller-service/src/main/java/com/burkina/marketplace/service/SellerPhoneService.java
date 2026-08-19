package com.burkina.marketplace.service;

import com.burkina.marketplace.domain.entity.SellerPhone;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SellerPhoneService {

    public List<SellerPhone> createPhones(List<String> phones) {
        return phones.stream()
                .map(SellerPhone::new)
                .toList();
    }
}
