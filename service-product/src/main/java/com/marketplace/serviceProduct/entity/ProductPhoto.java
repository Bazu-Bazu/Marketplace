package com.marketplace.serviceProduct.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "products_photo")
@Data
public class ProductPhoto {

    @Id
    private String id;

    @Field(name = "product_id")
    private Long productId;

    @Field(name = "image_data")
    private byte[] imageData;

    @Field(name = "is_main")
    private Boolean isMain;

    @Field(name = "sort_order")
    private Integer sortOrder;

}
