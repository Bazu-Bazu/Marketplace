package marketplace.serviceproduct.dto.request;

import lombok.Data;

@Data
public class AddProductRequest {

    private String name;
    private String description;
    private Integer price;
    private Integer count;

}
