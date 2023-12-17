package com.TutorTrack.demo.services;

import com.TutorTrack.demo.entity.Otp;
import com.TutorTrack.demo.entity.Token;
import com.TutorTrack.demo.entity.UserInfo;
import com.TutorTrack.demo.repos.OtpRepository;
import com.TutorTrack.demo.repos.TokenRepository;
import com.TutorTrack.demo.repos.UserInfoRepository;
import com.TutorTrack.demo.request.*;
import com.TutorTrack.demo.response.*;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.header.writers.CrossOriginEmbedderPolicyHeaderWriter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Service
public class UserInfoService implements UserDetailsService {

    @Autowired
    private UserInfoRepository repository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    JwtService jwtService;

    @Autowired
    EmailService emailService;

    @Autowired
    OtpRepository otpRepository;

//    @Autowired
//    UserDetailsService userDetailsService;

    @Autowired
    TokenRepository tokenRepository;




    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<UserInfo> userDetail = repository.findByEmail(email);

        // Converting userDetail to UserDetails
        return userDetail.map(UserInfoDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + email));
    }

    public String addUser(UserInfo userInfo) {
        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        repository.save(userInfo);
        return "User Added Successfully";
    }

    public SignupResponse createUser(SignupRequest userInfo) {
        if(userInfo.getPassword()==null || userInfo.getName()==null ||
                userInfo.getEmail()==null || userInfo.getRoles()==null){
             return SignupResponse
                     .builder()
                     .status(400)
                     .message("All fields are required !")
                     .build();
        }
       Optional <UserInfo> optionalUserInfo=repository.findByEmail(userInfo.getEmail());
        if(optionalUserInfo.isPresent()){
            return SignupResponse
                    .builder()
                    .status(400)
                    .message("User already exist. please login..!")
                    .build();
        }

        UserInfo newUser = new UserInfo();
        newUser.setEmail(userInfo.getEmail());
        newUser.setName(userInfo.getName());
        newUser.setRoles(userInfo.getRoles());
        newUser.setActive(userInfo.isActive());
        newUser.setPassword(encoder.encode(userInfo.getPassword()));
        repository.save(newUser);

        Optional<UserInfo> createdUser=repository.findByEmail(userInfo.getEmail());
        if(createdUser.isEmpty()){
            return SignupResponse
                    .builder()
                    .status(400)
                    .message("Something went wrong.. try again !")
                    .build();
        }
        if(createdUser.get().isActive()){
            return SignupResponse
                    .builder()
                    .status(200)
                    .message("Account created successfully !")
                    .build();
        }

        int randomCode=generateRandom4DigitNumber();
        Otp createdOtp = new Otp();
        LocalDateTime currentDate=LocalDateTime.now();

        createdOtp.setCreatedDate(currentDate);
        createdOtp.setExpiredDate(currentDate.plusMinutes(1));
        createdOtp.setOtpCode(randomCode);
        createdOtp.setUserId(createdUser.get().getId());
        otpRepository.save(createdOtp);


        if(Objects.equals(createdUser.get().getEmail(), userInfo.getEmail())
        ){


            try {
                emailService.sendOtpEmail(createdUser.get().getEmail(), randomCode,createdUser.get().getName());
            } catch (MessagingException e) {
                return SignupResponse
                        .builder()
                        .message("Invalid Email")
                        .status(400)
                        .build();
            }
            return SignupResponse
                    .builder()
                    .status(200)
                    .message("Otp code has been sent")
                    .build();
        }else{
            return SignupResponse
                    .builder()
                    .status(400)
                    .message("Failed to create user..try again !")
                    .build();
        }

    }

    private int generateRandom4DigitNumber() {
        Random random = new Random();
        return 1000 + random.nextInt(9000); // Generates a random number between 1000 and 9999
    }

    public OtpResponse verifyOtp(OtpRequest otpRequest) {
        LocalDateTime currentDateTime= LocalDateTime.now();
          if(otpRequest.getEmail()==null){
              return OtpResponse
                      .builder()
                      .message("All fields are required !")
                      .status(400)
                      .build();
          }
          Optional<UserInfo> optionalUserInfo =repository.findByEmail(otpRequest.getEmail());
          if(optionalUserInfo.isEmpty()){
              return OtpResponse
                      .builder()
                      .message("User doesn't exist !")
                      .status(400)
                      .build();
          }
//          if(optionalUserInfo.get().isActive()){
//              return OtpResponse
//                      .builder()
//                      .message("Your account is already activated. please login ..!")
//                      .status(400)
//                      .build();
//          }
          Optional<Otp> existingOtp = otpRepository.findByUserIdAndOtpCode(optionalUserInfo.get().getId(), otpRequest.getOtpCode());
          if(existingOtp.isEmpty()){
              return OtpResponse
                      .builder()
                      .message("Invalid OTP")
                      .status(400)
                      .build();
          }


          Otp otp=existingOtp.get();
          if(otp.getExpiredDate().isBefore(currentDateTime)){
              return OtpResponse
                      .builder()
                      .message("Otp code is expired !")
                      .status(400)
                      .build();
          }
          if(otp.getExpiredDate().isAfter(currentDateTime)){
               if(otp.getOtpCode()== otpRequest.getOtpCode()){
                   UserInfo userInfo=optionalUserInfo.get();
                   userInfo.setActive(true);
                   repository.save(userInfo);
                   List<Otp> otpList=otpRepository.findAllByUserId(userInfo.getId());
                   if(!otpList.isEmpty()){
                       otpRepository.deleteAll(otpList);
                   }
                   return OtpResponse
                           .builder()
                           .message("Your account has been activated successfully !")
                           .status(200)
                           .build();
               }else{
                   return OtpResponse
                           .builder()
                           .message("Invalid Otp code !")
                           .status(400)
                           .build();
               }
          }else{
              return OtpResponse
                      .builder()
                      .message("Otp code is expired !")
                      .status(400)
                      .build();
          }

    }


    public DefaultResponse resendOtp(String email) {
        Optional<UserInfo>optionalUserInfo=repository.findByEmail(email);
        if(optionalUserInfo.isEmpty()){
            return DefaultResponse
                    .builder()
                    .message("User doesn't exist !")
                    .status(400)
                    .build();
        }
        UserInfo userInfo=optionalUserInfo.get();
        List<Otp> existingOtps=otpRepository.findAllByUserId(userInfo.getId());
        if(!existingOtps.isEmpty()){
            otpRepository.deleteAll(existingOtps);
        }

        int randomCode=generateRandom4DigitNumber();
        LocalDateTime currentDateTime=LocalDateTime.now();
        Otp newOtp=new Otp();
        newOtp.setOtpCode(randomCode);
        newOtp.setUserId(userInfo.getId());
        newOtp.setCreatedDate(currentDateTime);
        newOtp.setExpiredDate(currentDateTime.plusMinutes(1));
        otpRepository.save(newOtp);

        try {
            emailService.sendOtpEmail(userInfo.getEmail(), randomCode,userInfo.getName());
        } catch (MessagingException e) {
            // Handle the exception
        }
        return DefaultResponse
                .builder()
                .message("Otp code has sent to your email "+userInfo.getEmail())
                .status(200)
                .build();


    }

    public DefaultResponse findUserExistByEmail(String email) {
        Optional<UserInfo> optionalUserInfo=repository.findByEmail(email);
        if(optionalUserInfo.isEmpty()){
            return DefaultResponse
                    .builder()
                    .message("User doesn't exist !")
                    .status(400)
                    .build();
        }else{
            return DefaultResponse
                    .builder()
                    .message("User exist !")
                    .status(200)
                    .build();
        }

    }

    public DefaultResponse resetPassword(ResetPasswordRequest resetPasswordRequest) {
        LocalDateTime currentDateTime=LocalDateTime.now();
        if(resetPasswordRequest.getEmail()==null ||
        resetPasswordRequest.getPassword()==null ){
            return DefaultResponse
                    .builder()
                    .message("All fields are required !")
                    .status(400)
                    .build();
        }
        Optional<UserInfo> optionalUserInfo=repository.findByEmail(resetPasswordRequest.getEmail());
        if(optionalUserInfo.isEmpty()){
            return DefaultResponse
                    .builder()
                    .message("User doesn't exist")
                    .status(400)
                    .build();
        }
        UserInfo userInfo=optionalUserInfo.get();
       Optional <Otp> optionalOtp=otpRepository.findByUserId(userInfo.getId());
       if(optionalOtp.isEmpty()){
           return DefaultResponse
                   .builder()
                   .message("Otp is expired !")
                   .status(400)
                   .build();
       }
       Otp existingOtp=optionalOtp.get();
       if(existingOtp.getExpiredDate().isBefore(currentDateTime)){
           return DefaultResponse
                   .builder()
                   .message("Otp is expired !")
                   .status(400)
                   .build();
       }else{
           if(existingOtp.getExpiredDate().isAfter(currentDateTime)){
               //not expired
               if(existingOtp.getOtpCode()== resetPasswordRequest.getOtpCode()){

                   userInfo.setPassword(encoder.encode(resetPasswordRequest.getPassword()));
                   repository.save(userInfo);
                  List<Otp> otpList=otpRepository.findAllByUserId(userInfo.getId());
                  if(!otpList.isEmpty()){
                      otpRepository.deleteAll(otpList);
                  }
                   return DefaultResponse
                           .builder()
                           .message("Password was reset !")
                           .status(200)
                           .build();
               }else{
                   return DefaultResponse
                           .builder()
                           .message("Invalid otp code !")
                           .status(400)
                           .build();
               }
           }else{
               return DefaultResponse
                       .builder()
                       .message("Otp code is expired !")
                       .status(400)
                       .build();
           }
       }


    }


    public DefaultResponse logoutUser(Long userId) {
        Optional<UserInfo> optionalUserInfo = repository.findById(userId);
        if(optionalUserInfo.isEmpty()){
            return DefaultResponse
                    .builder()
                    .message("User not found !")
                    .status(400)
                    .build();
        }
        UserInfo userInfo=optionalUserInfo.get();
        List<Token> tokenList=tokenRepository.findAllByUserId(userInfo.getId());
        if(!tokenList.isEmpty()){
            tokenRepository.deleteAll(tokenList);
        }
        return DefaultResponse
                .builder()
                .message("Logout successfully !")
                .status(200)
                .build();
    }
}
