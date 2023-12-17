package com.TutorTrack.demo.controllers;

import com.TutorTrack.demo.entity.MainCategory;
import com.TutorTrack.demo.entity.SubCategory;
import com.TutorTrack.demo.response.CategoryResponse;
import com.TutorTrack.demo.response.DefaultResponse;
import com.TutorTrack.demo.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    CategoryService categoryService;


    @PostMapping("/create-main-category")
    public ResponseEntity<?> createMainCategory(@RequestHeader("Authorization") String authorizationHeader,
                                                @RequestBody List<MainCategory> mainCategoryList){
        DefaultResponse response=categoryService.createMainCategory(authorizationHeader,mainCategoryList);
        if(response.getStatus()==400){
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if(response.getStatus()==200 || response.getStatus()==201){
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        response.setStatus(500);
        response.setMessage("Server error");
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @PutMapping("/update-main-category/{id}")
    public ResponseEntity<?> updateMainCategory(@RequestHeader("Authorization") String authorizationHeader,
                                                @RequestBody MainCategory mainCategory,
                                                @PathVariable Long id){
        DefaultResponse response= categoryService.updateMainCategory(authorizationHeader,mainCategory,id);

        if(response.getStatus()==400){
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if(response.getStatus()==200 || response.getStatus()==201){
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        response.setStatus(500);
        response.setMessage("Server error");
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @PostMapping("/create-sub-category/{mainCategoryId}")
    public ResponseEntity<?> createSubCategory(@RequestHeader("Authorization") String authorizationHeader,
                                                @RequestBody List<SubCategory> subCategoryList,
                                               @PathVariable Long mainCategoryId){
        DefaultResponse response=categoryService.createSubCategory(authorizationHeader,subCategoryList,mainCategoryId);
        if(response.getStatus()==400){
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if(response.getStatus()==200 || response.getStatus()==201){
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        response.setStatus(500);
        response.setMessage("Server error");
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @PutMapping("/update-sub-category/{subCategoryId}")
    public ResponseEntity<?> updateSubCategory(@RequestHeader("Authorization") String authorizationHeader,
                                               @RequestBody SubCategory subCategory,
                                               @PathVariable Long subCategoryId){

        DefaultResponse response=categoryService.updateSubCategory(authorizationHeader,subCategory,subCategoryId);
        if(response.getStatus()==400){
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if(response.getStatus()==200 || response.getStatus()==201){
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        response.setStatus(500);
        response.setMessage("Server error");
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);

    }

   @GetMapping("/get-category-list")
    public ResponseEntity<?> getCategoryList(@RequestHeader("Authorization") String authorizationHeader){
       CategoryResponse response=categoryService.getCategoryList(authorizationHeader);
       if(response.getStatus()==400){
           return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
       }

       if(response.getStatus()==200 || response.getStatus()==201){
           return new ResponseEntity<>(response,HttpStatus.OK);
       }
       response.setStatus(500);
       response.setMessage("Server error");
       return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
   }


    @GetMapping("/get-category-list/public")
    public ResponseEntity<?> getCategoryListPublic(){
        CategoryResponse response=categoryService.getCategoryListPublic();
        if(response.getStatus()==400){
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if(response.getStatus()==200 || response.getStatus()==201){
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        response.setStatus(500);
        response.setMessage("Server error");
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
