package com.TutorTrack.demo.request;

import com.TutorTrack.demo.entity.ProductImage;
import com.TutorTrack.demo.entity.ProductVariant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    private String name;
    private String description;
    private double price;
    private int quantity;
    private Long mainCategoryId;
    private Long subCategoryId;
    private Long storeId;

    private List<ProductImage> productImages;
    private List<ProductVariant> productVariants;
}
