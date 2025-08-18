package com.example.Marketplace.model;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products_photo")
@Data
public class ProductPhoto {

    @Id
    private Long id;

    private Long productId;
    private byte[] imageData;
    private String contentType;
    private Integer sortOder;

}
