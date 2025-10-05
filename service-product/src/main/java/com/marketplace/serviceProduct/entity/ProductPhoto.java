package com.marketplace.serviceProduct.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "product_photos")
@Data
public class ProductPhoto {

    @Id
    private String id;

    @Field(name = "product_id")
    private Long productId;

    @Field(name = "file_name")
    private String fileName;

    @Field(name = "content_type")
    private String contentType;

    private Long size;

    private String url;

}
