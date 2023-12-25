package com.TutorTrack.demo.services;


import com.TutorTrack.demo.entity.*;
import com.TutorTrack.demo.repos.*;
import com.TutorTrack.demo.request.ProductRequest;
import com.TutorTrack.demo.request.ProductVariantRequest;
import com.TutorTrack.demo.response.DefaultResponse;
import com.TutorTrack.demo.response.ProductResponse;
import com.TutorTrack.demo.response.ProductResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    MainCategoryRepository mainCategoryRepository;

    @Autowired
    SubCategoryRepository subCategoryRepository;

    @Autowired
    StoreRepository storeRepository;



    @Autowired
    JwtService jwtService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserInfoRepository userInfoRepository;

    @Autowired
    ProductImageRepository productImageRepository;

    @Autowired
    ProductVariantRepository productVariantRepository;


    public DefaultResponse createProduct(String authorizationHeader, ProductRequest productRequest) {
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

        Product newProduct = new Product();

        newProduct.setName(productRequest.getName());
        newProduct.setDescription(productRequest.getDescription());
        newProduct.setMainCategoryId(productRequest.getMainCategoryId());
        newProduct.setSubCategoryId(productRequest.getSubCategoryId());
        newProduct.setStoreId(productRequest.getStoreId());
        newProduct.setPrice(productRequest.getPrice());
        newProduct.setQuantity(productRequest.getQuantity());
        newProduct.setCreatedDate(LocalDateTime.now());
        newProduct.setUser(userInfo);

        Product savedProduct= productRepository.save(newProduct);

        List<ProductImage> productImageList = productRequest.getProductImages();
        List<ProductVariant> productVariantList= productRequest.getProductVariants();

        for(ProductImage productImage: productImageList){
            productImage.setProduct(savedProduct);
        }

        for(ProductVariant productVariant:productVariantList){
            productVariant.setProduct(savedProduct);
        }
        if(!productImageList.isEmpty()){
            productImageRepository.saveAll(productImageList);
        }
        if(!productVariantList.isEmpty()){
            productVariantRepository.saveAll(productVariantList);
        }

        return DefaultResponse
                .builder()
                .message("Product saved successfully")
                .status(201)
                .build();


    }

    public ProductResponse getAllProductsPrivate(String authorizationHeader, int page, int size) {
        String accessToken = authorizationHeader.substring(7);
        String email=jwtService.extractUsername(accessToken);
        Optional<UserInfo> optionalUserInfo= userInfoRepository.findByEmail(email);
        if(optionalUserInfo.isEmpty()){
            return ProductResponse
                    .builder()
                    .message("User not found !")
                    .status(400)
                    .build();
        }

        UserInfo userInfo=optionalUserInfo.get();
        PageRequest pageRequest = PageRequest.of(page-1,size);
        List<Product> productListCount=productRepository.findAllByUserAndIsDeletedFalse(userInfo);
        Long count= (long) productListCount.size();
        Page<Product> productPage = productRepository.findAllByUserAndIsDeletedFalse(userInfo, pageRequest);
        List<Product> productList = productPage.getContent();


        List<ProductResponseObject> finalArray=new ArrayList<>();
//        List<Product> productList=productRepository.findAllByUserAndIsDeletedFalse(userInfo);
        if(productList.isEmpty()){
            return ProductResponse
                    .builder()
                    .status(200)
                    .message("Product list is empty")
                    .productList(Collections.emptyList())
                    .build();
        }

        for(Product product: productList){

            Optional<MainCategory> mainCategory= mainCategoryRepository.findById(product.getMainCategoryId());
            Optional<SubCategory> subCategory= subCategoryRepository.findById(product.getSubCategoryId());
            Optional<Store> store = storeRepository.findById(product.getStoreId());


            ProductResponseObject productResponseObject = new ProductResponseObject();
            productResponseObject.setId(product.getId());
            productResponseObject.setName(product.getName());
            productResponseObject.setDescription(product.getDescription());
            productResponseObject.setPrice(product.getPrice());
            productResponseObject.setCreatedDate(product.getCreatedDate());
            productResponseObject.setQuantity(product.getQuantity());
            productResponseObject.setMainCategoryId(product.getMainCategoryId());
            productResponseObject.setSubCategoryId(product.getSubCategoryId());
            if(mainCategory.isPresent()){
                productResponseObject.setMainCategoryName(mainCategory.get().getName());
            }else{
                productResponseObject.setMainCategoryName(null);
            }

            if(subCategory.isPresent()){
                productResponseObject.setSubCategoryName(subCategory.get().getName());
            }else{
                productResponseObject.setSubCategoryName(null);
            }


            productResponseObject.setStoreId(product.getStoreId());
            if(store.isPresent()){
                productResponseObject.setStoreName(store.get().getName());
            }else{
                productResponseObject.setStoreName(null);
            }

            productResponseObject.setDeleted(product.isDeleted());
            productResponseObject.setProductImageList(product.getProductImages());
            productResponseObject.setProductVariantList(product.getProductVariants());
            finalArray.add(productResponseObject);

        }

        return ProductResponse
                .builder()
                .message("Fetched product list successfully !")
                .status(200)
                .totalCount(count)
                .productList(finalArray)
                .build();


    }

    public ProductResponse getAllProductsPublic(int page, int size) {
       List<Product> productListCount= productRepository.findAllByIsDeletedFalse();
       Long count = (long) productListCount.size();
        PageRequest pageRequest = PageRequest.of(page-1,size);
        Page<Product> productPage = productRepository.findAllByIsDeletedFalse(pageRequest);
        List<Product> productList = productPage.getContent();

        List<ProductResponseObject> finalArray=new ArrayList<>();
//        List<Product> productList=productRepository.findAllByIsDeletedFalse();
        if(productList.isEmpty()){
            return ProductResponse
                    .builder()
                    .status(200)
                    .message("Product list is empty")
                    .productList(Collections.emptyList())
                    .build();
        }

        for(Product product: productList){

            Optional<MainCategory> mainCategory= mainCategoryRepository.findById(product.getMainCategoryId());
            Optional<SubCategory> subCategory= subCategoryRepository.findById(product.getSubCategoryId());
            Optional<Store> store = storeRepository.findById(product.getStoreId());


            ProductResponseObject productResponseObject = new ProductResponseObject();

            productResponseObject.setId(product.getId());
            productResponseObject.setName(product.getName());
            productResponseObject.setDescription(product.getDescription());
            productResponseObject.setPrice(product.getPrice());
            productResponseObject.setCreatedDate(product.getCreatedDate());
            productResponseObject.setQuantity(product.getQuantity());
            productResponseObject.setMainCategoryId(product.getMainCategoryId());
            productResponseObject.setSubCategoryId(product.getSubCategoryId());
            if(mainCategory.isPresent()){
                productResponseObject.setMainCategoryName(mainCategory.get().getName());
            }else{
                productResponseObject.setMainCategoryName(null);
            }

            if(subCategory.isPresent()){
                productResponseObject.setSubCategoryName(subCategory.get().getName());
            }else{
                productResponseObject.setSubCategoryName(null);
            }


            productResponseObject.setStoreId(product.getStoreId());
            if(store.isPresent()){
                productResponseObject.setStoreName(store.get().getName());
            }else{
                productResponseObject.setStoreName(null);
            }

            productResponseObject.setDeleted(product.isDeleted());
            productResponseObject.setProductImageList(product.getProductImages());
            productResponseObject.setProductVariantList(product.getProductVariants());
            finalArray.add(productResponseObject);

        }

        return ProductResponse
                .builder()
                .message("Fetched product list successfully !")
                .status(200)
                .totalCount(count)
                .productList(finalArray)
                .build();
    }

    public ProductResponseObject getProductById(Long productId) {


        Optional<Product> productOptional = productRepository.findById(productId);
        if(productOptional.isEmpty()){
            return ProductResponseObject
                    .builder()
                    .message("Product not found")
                    .status(400)
                    .build();
        }
        Product product = productOptional.get();
        if(product.isDeleted()){
            return ProductResponseObject
                    .builder()
                    .message("Product is deleted")
                    .status(400)
                    .build();
        }
        Optional<MainCategory> mainCategoryOptional= mainCategoryRepository.findById(product.getMainCategoryId());
        Optional<SubCategory> subCategoryOptional= subCategoryRepository.findById(product.getSubCategoryId());
        Optional<Store> storeOptional = storeRepository.findById(product.getStoreId());

        MainCategory mainCategory= new MainCategory();
        SubCategory subCategory= new SubCategory();
        Store store= new Store();

        if(mainCategoryOptional.isPresent()){
             mainCategory= mainCategoryOptional.get();
        }
        if(subCategoryOptional.isPresent()){
             subCategory= subCategoryOptional.get();
        }

        if(storeOptional.isPresent()){
             store = storeOptional.get();
        }

        return ProductResponseObject
                .builder()
                .status(200)
                .message("Product found !")
                .id(product.getId())
                .price(product.getPrice())
                .name(product.getName())
                .quantity(product.getQuantity())
                .isDeleted(product.isDeleted())
                .createdDate(product.getCreatedDate())
                .description(product.getDescription())
                .mainCategoryId(product.getMainCategoryId())
                .subCategoryId(product.getSubCategoryId())
                .mainCategoryName(mainCategory.getName())
                .subCategoryName(subCategory.getName())
                .storeName(store.getName())
                .storeId(product.getStoreId())
                .productImageList(product.getProductImages())
                .productVariantList(product.getProductVariants())
                .build();

    }

    public DefaultResponse deleteProductImage(String authorizationHeader, Long imageId) {
        Optional<ProductImage> productImageOptional =productImageRepository.findById(imageId);
       if(productImageOptional.isEmpty()){
           return DefaultResponse
                   .builder()
                   .message("Product Image not found")
                   .status(400)
                   .build();
       }

       ProductImage productImage = productImageOptional.get();
       productImageRepository.delete(productImage);
        return DefaultResponse
                .builder()
                .message("Product Image Deleted successfully !")
                .status(200)
                .build();
    }

    public DefaultResponse updateProduct(String authorizationHeader, Long productId, ProductRequest productRequest) {
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

        Optional<Product> productOptional = productRepository.findById(productId);
        if(productOptional.isEmpty()){
            return DefaultResponse
                    .builder()
                    .message("Product not found !")
                    .status(400)
                    .build();
        }
        Product product= productOptional.get();
        if(product.getUser()!=userInfo){
            return DefaultResponse
                    .builder()
                    .message("Unauthorized request !")
                    .status(400)
                    .build();
        }
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setMainCategoryId(productRequest.getMainCategoryId());
        product.setSubCategoryId(productRequest.getSubCategoryId());
        product.setStoreId(productRequest.getStoreId());
        product.setQuantity(productRequest.getQuantity());
        productRepository.save(product);

        List<ProductImage> productImageList= productRequest.getProductImages();
        for(ProductImage productImage: productImageList){
             productImage.setProduct(product);
        }
        if(!productImageList.isEmpty()){
            productImageRepository.saveAll(productImageList);
        }

        return DefaultResponse
                .builder()
                .message("Product updated successfully !")
                .status(200)
                .build();


    }

    public DefaultResponse updateProductVariant(String authorizationHeader, ProductVariantRequest productVariantRequest, Long productId) {
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

        Optional<Product> productOptional =productRepository.findById(productId);
        if(productOptional.isEmpty()){
            return DefaultResponse
                    .builder()
                    .message("Product not found !")
                    .status(400)
                    .build();
        }
        Product product = productOptional.get();
        if(product.getUser()!=userInfo){
            return DefaultResponse
                    .builder()
                    .message("Unauthorized request !")
                    .status(400)
                    .build();
        }

        List<ProductVariant> productVariantList= productVariantRepository.findAllByProduct(product);
        if(!productVariantList.isEmpty()){
            productVariantRepository.deleteAll(productVariantList);
        }
        List<ProductVariant> productVariantsToBeSaved= productVariantRequest.getProductVariantList();
        if(!productVariantsToBeSaved.isEmpty()){
            for(ProductVariant productVariant : productVariantsToBeSaved){
                productVariant.setProduct(product);
            }
        }


        if(!productVariantsToBeSaved.isEmpty()){
            productVariantRepository.saveAll(productVariantsToBeSaved);
        }
        return DefaultResponse
                .builder()
                .message("Product variant updated !")
                .status(200)
                .build();
    }
}
