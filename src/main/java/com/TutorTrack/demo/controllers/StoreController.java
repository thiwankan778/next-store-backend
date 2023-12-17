package com.TutorTrack.demo.controllers;

import com.TutorTrack.demo.entity.Store;
import com.TutorTrack.demo.entity.UserInfo;
import com.TutorTrack.demo.repos.StoreRepository;
import com.TutorTrack.demo.repos.UserInfoRepository;
import com.TutorTrack.demo.request.StoreRequest;
import com.TutorTrack.demo.response.DefaultResponse;
import com.TutorTrack.demo.response.StoreResponse;
import com.TutorTrack.demo.services.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/store")
public class StoreController {

    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    StoreService storeService;


    @PostMapping("/create-store")
    public ResponseEntity<?> createStore(@RequestHeader("Authorization") String authorizationHeader,@RequestBody List<Store> storeList){
        DefaultResponse response= storeService.createStore(authorizationHeader,storeList);
        if(response.getStatus()==400){
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

        if(response.getStatus()==200 || response.getStatus()==201){
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        response.setStatus(500);
        response.setMessage("Server error");
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/get-all-store")
    public ResponseEntity<?> getAllStores(@RequestHeader("Authorization") String authorizationHeader){
        StoreResponse response= storeService.getAllStores(authorizationHeader);
        if(response.getStatus()==400){
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

        if(response.getStatus()==200 || response.getStatus()==201){
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        response.setStatus(500);
        response.setMessage("Server error");
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/update-store/{id}")
    public ResponseEntity<?> updateStore(@RequestHeader("Authorization") String authorizationHeader,
                                         @RequestBody StoreRequest storeRequest,@PathVariable Long id){
        DefaultResponse response =storeService.updateStore(authorizationHeader,storeRequest,id);

        if(response.getStatus()==400){
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

        if(response.getStatus()==200 || response.getStatus()==201){
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        response.setStatus(500);
        response.setMessage("Server error");
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @PutMapping("/temp-delete/{id}")
    public ResponseEntity<?> tempDelete(@RequestHeader("Authorization") String authorizationHeader,
                                        @PathVariable Long id){
        DefaultResponse response=storeService.tempDelete(authorizationHeader,id);
        if(response.getStatus()==400){
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

        if(response.getStatus()==200 || response.getStatus()==201){
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        response.setStatus(500);
        response.setMessage("Server error");
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @PutMapping("/recover-store/{id}")
    public ResponseEntity<?> recoverStore(@RequestHeader("Authorization") String authorizationHeader,
                                        @PathVariable Long id){
        DefaultResponse response=storeService.recoverStore(authorizationHeader,id);
        if(response.getStatus()==400){
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

        if(response.getStatus()==200 || response.getStatus()==201){
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        response.setStatus(500);
        response.setMessage("Server error");
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @DeleteMapping("/delete-store/{id}")
    public ResponseEntity<?> deleteStore(@RequestHeader("Authorization") String authorizationHeader,
                                          @PathVariable Long id){
        DefaultResponse response=storeService.deleteStore(authorizationHeader,id);
        if(response.getStatus()==400){
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

        if(response.getStatus()==200 || response.getStatus()==201){
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        response.setStatus(500);
        response.setMessage("Server error");
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/get-all-stores-public")
    public ResponseEntity<?> getAllStoresByPublic(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size){
        StoreResponse response=storeService.getAllStoresByPublic(page,size);
        if(response.getStatus()==400){
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }

        if(response.getStatus()==200 || response.getStatus()==201){
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        response.setStatus(500);
        response.setMessage("Server error");
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);

    }









}
