package com.TutorTrack.demo.repos;

import com.TutorTrack.demo.entity.Store;
import com.TutorTrack.demo.entity.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store,Long> {

    List<Store> findAllByUser(UserInfo user);

    List<Store> findAllByUserAndIsDeletedIsFalse(UserInfo user);

    List<Store> findAllByIsDeletedIsFalse();
    Page<Store> findAllByIsDeletedIsFalse(Pageable pageable);

    Store findByUserAndNameIgnoreCaseAndDescriptionIgnoreCase(UserInfo user,String name, String des);
    Store findByNameIgnoreCaseAndDescriptionIgnoreCase(String name,String des);
    Store findByNameIgnoreCase(String name);

    Store findByContact(String contact);


}
