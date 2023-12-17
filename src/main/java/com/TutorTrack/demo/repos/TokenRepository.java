package com.TutorTrack.demo.repos;

import com.TutorTrack.demo.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token,Long> {

    List<Token> findAllByUserId(Long userId);

    Optional<Token> findByAccessToken(String token);
}
