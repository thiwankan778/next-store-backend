package com.TutorTrack.demo.repos;

import com.TutorTrack.demo.entity.MainCategory;
import com.TutorTrack.demo.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MainCategoryRepository extends JpaRepository<MainCategory,Long> {

    MainCategory findByUserAndAndNameIgnoreCase(UserInfo user, String name);

    MainCategory findByUserAndId(UserInfo user,Long id);

    List<MainCategory> findAllByUser(UserInfo userInfo);
}
