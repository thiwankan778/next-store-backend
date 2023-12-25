package com.TutorTrack.demo.repos;

import com.TutorTrack.demo.entity.Product;
import com.TutorTrack.demo.entity.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    List<Product> findAllByUser(UserInfo user);

    List<Product> findAllByUserAndIsDeletedFalse(UserInfo user);

    Page<Product> findAllByUserAndIsDeletedFalse(UserInfo user, Pageable pageable);

    Page<Product> findAllByIsDeletedFalse(Pageable pageable);

    List<Product> findAllByIsDeletedFalse();

}
