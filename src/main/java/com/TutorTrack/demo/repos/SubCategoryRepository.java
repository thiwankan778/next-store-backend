package com.TutorTrack.demo.repos;

import com.TutorTrack.demo.entity.MainCategory;
import com.TutorTrack.demo.entity.SubCategory;
import com.TutorTrack.demo.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory,Long> {


    SubCategory findByUserAndNameIgnoreCase(UserInfo user,String name);

    List<SubCategory> findAllByMainCategory(MainCategory mainCategory);
}
