package com.TutorTrack.demo.repos;

import com.TutorTrack.demo.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    Optional<UserInfo> findByName(String name);
    Optional<UserInfo> findByEmail(String email);
}
