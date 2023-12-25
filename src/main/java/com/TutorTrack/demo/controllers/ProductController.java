package com.TutorTrack.demo.controllers;


import com.TutorTrack.demo.entity.Store;
import com.TutorTrack.demo.request.ProductRequest;
import com.TutorTrack.demo.request.ProductVariantRequest;
import com.TutorTrack.demo.response.DefaultResponse;
import com.TutorTrack.demo.response.ProductResponse;
import com.TutorTrack.demo.response.ProductResponseObject;
import com.TutorTrack.demo.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

     @Autowired
     ProductService productService;

      @PostMapping
      public ResponseEntity<?> createProduct(@RequestHeader("Authorization") String authorizationHeader,@RequestBody ProductRequest productRequest){
           DefaultResponse response= productService.createProduct(authorizationHeader,productRequest);
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

      @GetMapping("/private")
      public ResponseEntity<?> getAllProductsPrivate(
              @RequestHeader("Authorization") String authorizationHeader,
              @RequestParam(defaultValue = "1") int page,
              @RequestParam(defaultValue = "10") int size
      ){
          ProductResponse response=productService.getAllProductsPrivate(authorizationHeader,page,size);
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

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(

            @PathVariable Long productId
    ){
        ProductResponseObject response=productService.getProductById(productId);
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

    @DeleteMapping("/image-delete/{imageId}")
    public ResponseEntity<?> deleteProductImage( @RequestHeader("Authorization") String authorizationHeader,
                                                 @PathVariable Long imageId){

          DefaultResponse response = productService.deleteProductImage(authorizationHeader,imageId);
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
    @PutMapping("/update/{productId}")
    public ResponseEntity<?> updateProduct( @RequestHeader("Authorization") String authorizationHeader,
                                                 @PathVariable Long productId,
                                            @RequestBody ProductRequest productRequest){

        DefaultResponse response = productService.updateProduct(authorizationHeader,productId,productRequest);
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

    @PutMapping("/update-product-variant/{productId}")
    public ResponseEntity<?> updateProductVariant(@RequestHeader("Authorization") String authorizationHeader,
                                                  @RequestBody ProductVariantRequest productVariantRequest,
                                                  @PathVariable Long productId){
          DefaultResponse response= productService.updateProductVariant(authorizationHeader,productVariantRequest,productId);
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

    @GetMapping("/public")
    public ResponseEntity<?> getAllProductsPublic(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size){
        ProductResponse response=productService.getAllProductsPublic(page,size);
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
