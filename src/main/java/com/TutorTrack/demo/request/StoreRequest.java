package com.TutorTrack.demo.request;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreRequest {

    private String name;
    private String description;
    private String storeImageUrl;
    private String city;
    private String address;
    private String contact;

}
