package com.TutorTrack.demo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

    private String message;
    private int status;
    private Long totalCount;
    private List<ProductResponseObject> productList;
}
