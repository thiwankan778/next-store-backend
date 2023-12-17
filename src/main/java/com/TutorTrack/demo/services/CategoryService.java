package com.TutorTrack.demo.services;

import com.TutorTrack.demo.entity.MainCategory;
import com.TutorTrack.demo.entity.SubCategory;
import com.TutorTrack.demo.entity.UserInfo;
import com.TutorTrack.demo.repos.MainCategoryRepository;
import com.TutorTrack.demo.repos.SubCategoryRepository;
import com.TutorTrack.demo.repos.UserInfoRepository;
import com.TutorTrack.demo.response.CategoryResponse;
import com.TutorTrack.demo.response.DefaultResponse;
import com.TutorTrack.demo.response.MainSubCategoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryService {

    @Autowired
    UserInfoRepository userInfoRepository;
    @Autowired
    MainCategoryRepository mainCategoryRepository;

    @Autowired
    SubCategoryRepository subCategoryRepository;

    @Autowired
    JwtService jwtService;


    public DefaultResponse createMainCategory(String authorizationHeader, List<MainCategory> mainCategoryList) {
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

       for(MainCategory mainCategory:mainCategoryList){
           if(mainCategory.getName()==null){
               return  DefaultResponse
                       .builder()
                       .message("Category name is required !")
                       .status(400)
                       .build();
           }
       }

        UserInfo userInfo=optionalUserInfo.get();
       List<MainCategory> mainCategoryList1=new ArrayList<>();

       for(MainCategory mainCategory:mainCategoryList){
           MainCategory duplicate=mainCategoryRepository.findByUserAndAndNameIgnoreCase(userInfo,mainCategory.getName());
           SubCategory duplicate2=subCategoryRepository.findByUserAndNameIgnoreCase(userInfo,mainCategory.getName());

           if(duplicate==null && duplicate2==null){
               mainCategoryList1.add(mainCategory);
           }
       }

       if(!mainCategoryList1.isEmpty()){
           for(MainCategory mainCategory:mainCategoryList1){
               mainCategory.setUser(userInfo);
           }
       }

       if(!mainCategoryList1.isEmpty()){
           mainCategoryRepository.saveAll(mainCategoryList1);
       }else{
           return DefaultResponse
                   .builder()
                   .status(400)
                   .message("Main category already exist in main or sub !")
                   .build();
       }


       return DefaultResponse
               .builder()
               .status(200)
               .message("Main category created successfully !")
               .build();

    }

    public DefaultResponse updateMainCategory(String authorizationHeader, MainCategory mainCategory, Long id) {
        String accessToken = authorizationHeader.substring(7);
        String email = jwtService.extractUsername(accessToken);
        Optional<UserInfo> optionalUserInfo = userInfoRepository.findByEmail(email);

        if (optionalUserInfo.isEmpty()) {
            return DefaultResponse.builder()
                    .message("User not found!")
                    .status(400)
                    .build();
        }

        if (mainCategory.getName() == null) {
            return DefaultResponse.builder()
                    .message("Category name is required!")
                    .status(400)
                    .build();
        }

        UserInfo userInfo = optionalUserInfo.get();
        Optional<MainCategory> optionalMainCategory = mainCategoryRepository.findById(id);

        if (optionalMainCategory.isEmpty()) {
            return DefaultResponse.builder()
                    .message("Main Category not found!")
                    .status(400)
                    .build();
        }

        MainCategory existingMainCategory = optionalMainCategory.get();

        if (!existingMainCategory.getUser().equals(userInfo)) {
            return DefaultResponse.builder()
                    .message("Unauthorized request!")
                    .status(400)
                    .build();
        }

        MainCategory duplicate = mainCategoryRepository.findByUserAndAndNameIgnoreCase(userInfo, mainCategory.getName());
        SubCategory duplicate2=subCategoryRepository.findByUserAndNameIgnoreCase(userInfo,mainCategory.getName());
        if(duplicate2!=null){
            return DefaultResponse.builder()
                    .message("The provided main category name matches an existing subcategory name. Please use a different name for the main category.")
                    .status(400)
                    .build();
        }
        if (duplicate == null || existingMainCategory.getId().equals(duplicate.getId())) {
            existingMainCategory.setName(mainCategory.getName());
            existingMainCategory.setDescription(mainCategory.getDescription());
            mainCategoryRepository.save(existingMainCategory);

            return DefaultResponse.builder()
                    .message("Main category updated successfully!")
                    .status(200)
                    .build();
        }

        return DefaultResponse.builder()
                .message("Duplicate category found! Cannot update.")
                .status(400)
                .build();
    }

    public DefaultResponse createSubCategory(String authorizationHeader, List<SubCategory> subCategoryList, Long mainCategoryId) {
        String accessToken = authorizationHeader.substring(7);
        String email = jwtService.extractUsername(accessToken);
        Optional<UserInfo> optionalUserInfo = userInfoRepository.findByEmail(email);

        if (optionalUserInfo.isEmpty()) {
            return DefaultResponse.builder()
                    .message("User not found!")
                    .status(400)
                    .build();
        }

       for(SubCategory subCategory:subCategoryList){
           if(subCategory.getName()==null){
               return DefaultResponse
                       .builder()
                       .message("Category name is required !")
                       .status(400)
                       .build();
           }
       }

        UserInfo userInfo = optionalUserInfo.get();
       MainCategory mainCategory=mainCategoryRepository.findByUserAndId(userInfo,mainCategoryId);
       if(mainCategory==null){
           return DefaultResponse
                   .builder()
                   .message("Main category not found !")
                   .status(400)
                   .build();
       }



       List<SubCategory> subCategoryList1=new ArrayList<>();

       for(SubCategory subCategory:subCategoryList){
           SubCategory duplicate=subCategoryRepository.findByUserAndNameIgnoreCase(userInfo,subCategory.getName());
           MainCategory duplicate2=mainCategoryRepository.findByUserAndAndNameIgnoreCase(userInfo,subCategory.getName());
           if(duplicate==null && duplicate2==null){
               subCategoryList1.add(subCategory);
           }
       }

       if(!subCategoryList1.isEmpty()){
           for(SubCategory subCategory:subCategoryList1){
               subCategory.setUser(userInfo);
               subCategory.setMainCategory(mainCategory);
           }
       }

      if(!subCategoryList1.isEmpty()){
          subCategoryRepository.saveAll(subCategoryList1);
      }else{
          return DefaultResponse
                  .builder()
                  .message("Sub category already exist in main or sub !")
                  .status(400)
                  .build();
      }


       return DefaultResponse
               .builder()
               .message("Sub category created successfully !")
               .status(200)
               .build();



    }

    public DefaultResponse updateSubCategory(String authorizationHeader, SubCategory subCategory, Long subCategoryId) {
        String accessToken = authorizationHeader.substring(7);
        String email = jwtService.extractUsername(accessToken);
        Optional<UserInfo> optionalUserInfo = userInfoRepository.findByEmail(email);

        if (optionalUserInfo.isEmpty()) {
            return DefaultResponse.builder()
                    .message("User not found!")
                    .status(400)
                    .build();
        }



        UserInfo userInfo = optionalUserInfo.get();
        Optional<SubCategory> optionalSubCategory=subCategoryRepository.findById(subCategoryId);
        if(optionalSubCategory.isEmpty()){
            return DefaultResponse.builder()
                    .message("Sub category not found !")
                    .status(400)
                    .build();
        }
        SubCategory existingSubCategory= optionalSubCategory.get();
        if(existingSubCategory.getUser()!=userInfo){
            return DefaultResponse.builder()
                    .message("Unauthorized request !")
                    .status(400)
                    .build();
        }

        MainCategory mainCategory=mainCategoryRepository.findByUserAndAndNameIgnoreCase(userInfo,subCategory.getName());
        if(mainCategory!=null){
            return DefaultResponse.builder()
                    .message("The provided sub category name matches an existing main category name. Please use a different name for the sub category.")
                    .status(400)
                    .build();
        }

        SubCategory duplicate=subCategoryRepository.findByUserAndNameIgnoreCase(userInfo,subCategory.getName());
        if(duplicate==null){
            existingSubCategory.setName(subCategory.getName());
            existingSubCategory.setDescription(subCategory.getDescription());
            subCategoryRepository.save(existingSubCategory);
        }else if(Objects.equals(duplicate.getId(), existingSubCategory.getId())){
            existingSubCategory.setName(subCategory.getName());
            existingSubCategory.setDescription(subCategory.getDescription());
            subCategoryRepository.save(existingSubCategory);
        }else{
            return DefaultResponse.builder()
                    .message("Duplicate sub category !")
                    .status(400)
                    .build();
        }
        return DefaultResponse.builder()
                .message("Sub category updated successfully !")
                .status(200)
                .build();
    }

    public CategoryResponse getCategoryList(String authorizationHeader) {
        String accessToken = authorizationHeader.substring(7);
        String email = jwtService.extractUsername(accessToken);
        Optional<UserInfo> optionalUserInfo = userInfoRepository.findByEmail(email);

        if (optionalUserInfo.isEmpty()) {
            return CategoryResponse.builder()
                    .message("User not found!")
                    .status(400)
                    .categoryList(Collections.emptyList())
                    .build();
        }

        UserInfo userInfo = optionalUserInfo.get();
        List<MainSubCategoryResponse> finalArray=new ArrayList<>();
        List<MainCategory> mainCategoryList=mainCategoryRepository.findAllByUser(userInfo);
        if(mainCategoryList.isEmpty()){
            return CategoryResponse
                    .builder()
                    .message("Categories are empty !")
                    .status(200)
                    .categoryList(Collections.emptyList())
                    .build();
        }

        for(MainCategory mainCategory:mainCategoryList){
            MainSubCategoryResponse mainSubCategoryResponse=new MainSubCategoryResponse();
            List<SubCategory> subCategoryList=subCategoryRepository.findAllByMainCategory(mainCategory);

            mainSubCategoryResponse.setMainCategoryId(mainCategory.getId());
            mainSubCategoryResponse.setMainCategoryDescription(mainCategory.getDescription());
            mainSubCategoryResponse.setMainCategoryName(mainCategory.getName());
            mainSubCategoryResponse.setSubCategoryList(subCategoryList);

            finalArray.add(mainSubCategoryResponse);

        }
        return CategoryResponse
                .builder()
                .message("Fetch Categories successfully !")
                .status(200)
                .categoryList(finalArray)
                .build();
    }

    public CategoryResponse getCategoryListPublic() {


        List<MainSubCategoryResponse> finalArray=new ArrayList<>();
        List<MainCategory> mainCategoryList=mainCategoryRepository.findAll();
        if(mainCategoryList.isEmpty()){
            return CategoryResponse
                    .builder()
                    .message("Categories are empty !")
                    .status(200)
                    .categoryList(Collections.emptyList())
                    .build();
        }

        for(MainCategory mainCategory:mainCategoryList){
            MainSubCategoryResponse mainSubCategoryResponse=new MainSubCategoryResponse();
            List<SubCategory> subCategoryList=subCategoryRepository.findAllByMainCategory(mainCategory);

            mainSubCategoryResponse.setMainCategoryId(mainCategory.getId());
            mainSubCategoryResponse.setMainCategoryDescription(mainCategory.getDescription());
            mainSubCategoryResponse.setMainCategoryName(mainCategory.getName());
            mainSubCategoryResponse.setSubCategoryList(subCategoryList);

            finalArray.add(mainSubCategoryResponse);

        }
        return CategoryResponse
                .builder()
                .message("Fetch Categories successfully !")
                .status(200)
                .categoryList(finalArray)
                .build();

    }
}
