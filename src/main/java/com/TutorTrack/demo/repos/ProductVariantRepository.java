package com.TutorTrack.demo.repos;

import com.TutorTrack.demo.entity.Product;
import com.TutorTrack.demo.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductVariantRepository extends JpaRepository<ProductVariant,Long> {

    List<ProductVariant> findAllByProduct(Product product);
}
