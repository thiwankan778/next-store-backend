package com.TutorTrack.demo.response;

import com.TutorTrack.demo.entity.SubCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MainSubCategoryResponse {

    private Long mainCategoryId;
    private String mainCategoryName;
    private String mainCategoryDescription;
    private List<SubCategory> subCategoryList;
}
