package marketplace.serviceproduct.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "products_photo")
@Data
public class ProductPhoto {

    @Id
    private String id;

    @Field(name = "image_data")
    private byte[] imageData;

}
