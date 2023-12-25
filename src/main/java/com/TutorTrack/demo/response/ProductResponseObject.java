package com.TutorTrack.demo.response;


import com.TutorTrack.demo.entity.ProductImage;
import com.TutorTrack.demo.entity.ProductVariant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseObject {

    private Long id;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private Long mainCategoryId;
    private Long subCategoryId;
    private String mainCategoryName;
    private String subCategoryName;
    private String storeName;
    private Long storeId;
    private boolean isDeleted=false;
    private LocalDateTime createdDate;
    private int status;
    private String message;

    private List<ProductImage> productImageList;
    private List<ProductVariant> productVariantList;
}
