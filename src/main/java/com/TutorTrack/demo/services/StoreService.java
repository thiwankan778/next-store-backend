package com.TutorTrack.demo.services;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.TutorTrack.demo.entity.Store;
import com.TutorTrack.demo.entity.UserInfo;
import com.TutorTrack.demo.repos.StoreRepository;
import com.TutorTrack.demo.repos.UserInfoRepository;
import com.TutorTrack.demo.request.StoreRequest;
import com.TutorTrack.demo.response.DefaultResponse;
import com.TutorTrack.demo.response.StoreResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StoreService {

    @Autowired
    JwtService jwtService;

    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    StoreRepository storeRepository;


    public DefaultResponse createStore(String authorizationHeader, List<Store> storeList) {
        String accessToken = authorizationHeader.substring(7);
        String email=jwtService.extractUsername(accessToken);
        Optional<UserInfo> optionalUserInfo= userInfoRepository.findByEmail(email);
        if(optionalUserInfo.isEmpty()){
            return DefaultResponse
                    .builder()
                    .message("User not found !")
                    .status(400)
                    .build();
        }

        UserInfo userInfo=optionalUserInfo.get();

        List<Store> storeList1=new ArrayList<>();

        for(Store store : storeList){
            Store duplicate=storeRepository.findByNameIgnoreCase(store.getName());
            if(duplicate==null){
                storeList1.add(store);
            }
//            store.setUser(userInfo);
        }
        if(!storeList1.isEmpty()){
            for(Store store:storeList1){
                store.setUser(userInfo);
            }
        }
        if(!storeList1.isEmpty()){
            storeRepository.saveAll(storeList1);
        }else{
            return DefaultResponse
                    .builder()
                    .message("Store name already exists !")
                    .status(400)
                    .build();
        }



        return DefaultResponse
                .builder()
                .message("Store created successfully !")
                .status(200)
                .build();
    }

    public StoreResponse getAllStores(String authorizationHeader) {
        String accessToken = authorizationHeader.substring(7);
        String email=jwtService.extractUsername(accessToken);
        Optional<UserInfo> optionalUserInfo= userInfoRepository.findByEmail(email);
        if(optionalUserInfo.isEmpty()){
            return StoreResponse
                    .builder()
                    .message("User not found !")
                    .storeList(Collections.emptyList())
                    .status(400)
                    .build();
        }

        UserInfo userInfo=optionalUserInfo.get();
        List<Store> storeList=storeRepository.findAllByUserAndIsDeletedIsFalse(userInfo);
        Long count = (long) storeList.size();
        if(storeList.isEmpty()){
            return StoreResponse
                    .builder()
                    .status(200)
                    .storeList(Collections.emptyList())
                    .totalCount(count)
                    .message("Store List fetch successfully !")
                    .build();
        }
        return StoreResponse
                .builder()
                .status(200)
                .storeList(storeList)
                .totalCount(count)
                .message("Store List fetch successfully !")
                .build();
    }

    public DefaultResponse updateStore(String authorizationHeader, StoreRequest storeRequest, Long id) {
        String accessToken = authorizationHeader.substring(7);
        String email=jwtService.extractUsername(accessToken);
        Optional<UserInfo> optionalUserInfo= userInfoRepository.findByEmail(email);
        if(optionalUserInfo.isEmpty()){
            return DefaultResponse
                    .builder()
                    .message("User not found !")
                    .status(400)
                    .build();
        }

        UserInfo userInfo=optionalUserInfo.get();
        Optional<Store> optionalStore=storeRepository.findById(id);
        if(optionalStore.isEmpty()){
            return DefaultResponse
                    .builder()
                    .message("Store not found !")
                    .status(400)
                    .build();
        }
        Store store=optionalStore.get();
        if(!Objects.equals(store.getUser().getId(), userInfo.getId())){
            return DefaultResponse
                    .builder()
                    .message("Unauthorized request !")
                    .status(400)
                    .build();
        }


            Store duplicate=storeRepository.findByNameIgnoreCase(storeRequest.getName());
            if(duplicate==null){
                store.setName(storeRequest.getName());
                store.setDescription(storeRequest.getDescription());
                store.setStoreImageUrl(storeRequest.getStoreImageUrl());
                store.setCity(storeRequest.getCity());
                store.setAddress(storeRequest.getAddress());
                store.setContact(storeRequest.getContact());
                storeRepository.save(store);
            } else if (Objects.equals(duplicate.getId(), store.getId())) {
                store.setName(storeRequest.getName());
                store.setDescription(storeRequest.getDescription());
                store.setStoreImageUrl(storeRequest.getStoreImageUrl());
                store.setCity(storeRequest.getCity());
                store.setAddress(storeRequest.getAddress());
                store.setContact(storeRequest.getContact());
                storeRepository.save(store);
            }else{
                return DefaultResponse
                        .builder()
                        .message("duplicate name !")
                        .status(400)
                        .build();
            }





        return DefaultResponse
                .builder()
                .message("Store Updated successfully !")
                .status(200)
                .build();



    }

    public DefaultResponse tempDelete(String authorizationHeader, Long id) {
        String accessToken = authorizationHeader.substring(7);
        String email=jwtService.extractUsername(accessToken);
        Optional<UserInfo> optionalUserInfo= userInfoRepository.findByEmail(email);
        if(optionalUserInfo.isEmpty()){
            return DefaultResponse
                    .builder()
                    .message("User not found !")
                    .status(400)
                    .build();
        }

        UserInfo userInfo=optionalUserInfo.get();
        Optional<Store> optionalStore=storeRepository.findById(id);
        if(optionalStore.isEmpty()){
            return DefaultResponse
                    .builder()
                    .message("Store not found !")
                    .status(400)
                    .build();
        }
        Store store=optionalStore.get();
        if(!Objects.equals(store.getUser().getId(), userInfo.getId())){
            return DefaultResponse
                    .builder()
                    .message("Unauthorized request !")
                    .status(400)
                    .build();
        }
        store.setDeleted(true);
        storeRepository.save(store);
        return DefaultResponse
                .builder()
                .message("Deleted successfully !")
                .status(200)
                .build();
    }

    public DefaultResponse recoverStore(String authorizationHeader, Long id) {

        String accessToken = authorizationHeader.substring(7);
        String email=jwtService.extractUsername(accessToken);
        Optional<UserInfo> optionalUserInfo= userInfoRepository.findByEmail(email);
        if(optionalUserInfo.isEmpty()){
            return DefaultResponse
                    .builder()
                    .message("User not found !")
                    .status(400)
                    .build();
        }

        UserInfo userInfo=optionalUserInfo.get();
        Optional<Store> optionalStore=storeRepository.findById(id);
        if(optionalStore.isEmpty()){
            return DefaultResponse
                    .builder()
                    .message("Store not found !")
                    .status(400)
                    .build();
        }
        Store store=optionalStore.get();
        if(!Objects.equals(store.getUser().getId(), userInfo.getId())){
            return DefaultResponse
                    .builder()
                    .message("Unauthorized request !")
                    .status(400)
                    .build();
        }
        store.setDeleted(false);
        storeRepository.save(store);
        return DefaultResponse
                .builder()
                .message("Recovered successfully !")
                .status(200)
                .build();
    }

    public DefaultResponse deleteStore(String authorizationHeader, Long id) {
        String accessToken = authorizationHeader.substring(7);
        String email=jwtService.extractUsername(accessToken);
        Optional<UserInfo> optionalUserInfo= userInfoRepository.findByEmail(email);
        if(optionalUserInfo.isEmpty()){
            return DefaultResponse
                    .builder()
                    .message("User not found !")
                    .status(400)
                    .build();
        }

        UserInfo userInfo=optionalUserInfo.get();
        Optional<Store> optionalStore=storeRepository.findById(id);
        if(optionalStore.isEmpty()){
            return DefaultResponse
                    .builder()
                    .message("Store not found !")
                    .status(400)
                    .build();
        }
        Store store=optionalStore.get();
        if(!Objects.equals(store.getUser().getId(), userInfo.getId())){
            return DefaultResponse
                    .builder()
                    .message("Unauthorized request !")
                    .status(400)
                    .build();
        }
        storeRepository.delete(store);
        return DefaultResponse
                .builder()
                .message("Store deleted successfully !")
                .status(200)
                .build();

    }

    public StoreResponse getAllStoresByPublic(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Store> storePage = storeRepository.findAllByIsDeletedIsFalse(pageable);
        List<Store> storeList=storeRepository.findAllByIsDeletedIsFalse();
        Long count = (long) storeList.size();
        if (storePage.isEmpty()) {
            return StoreResponse.builder()
                    .message("No stores found")
                    .status(200)
                    .totalCount(count)
                    .storeList(Collections.emptyList())
                    .build();
        }

        return StoreResponse.builder()
                .message("Fetch store successfully !")
                .status(200)
                .totalCount(count)
                .storeList(storePage.getContent())
                .build();
    }
}
