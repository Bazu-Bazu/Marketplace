package com.example.Marketplace.dto;

import lombok.Data;

@Data
public class AddProductDto {

    private String name;
    private String description;
    private Integer price;
    private Integer count;

}
