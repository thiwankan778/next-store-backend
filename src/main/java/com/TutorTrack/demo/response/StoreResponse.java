package com.TutorTrack.demo.response;

import com.TutorTrack.demo.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreResponse {

    private  String message;
    private  List<Store> storeList;
    private int status;
    private Long totalCount;
}
