package com.TutorTrack.demo.repos;

import com.TutorTrack.demo.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    List<Otp> findAllByUserId(Long id);
    Optional<Otp> findByUserIdAndOtpCode(Long userId,int otpCode);

    Optional<Otp> findByUserId(Long id);
}
