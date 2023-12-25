package com.TutorTrack.demo.controllers;

import com.TutorTrack.demo.entity.Otp;
import com.TutorTrack.demo.entity.Token;
import com.TutorTrack.demo.entity.UserInfo;
import com.TutorTrack.demo.repos.OtpRepository;
import com.TutorTrack.demo.repos.TokenRepository;
import com.TutorTrack.demo.repos.UserInfoRepository;
import com.TutorTrack.demo.request.*;
import com.TutorTrack.demo.response.*;
import com.TutorTrack.demo.services.EmailService;
import com.TutorTrack.demo.services.JwtService;
import com.TutorTrack.demo.services.UserInfoService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;


@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    UserInfoService service;

   @Autowired
   private JwtService jwtService;

   @Autowired
    UserInfoRepository userInfoRepository;

  @Autowired
    private AuthenticationManager authenticationManager;

  @Autowired
    TokenRepository tokenRepository;

  @Autowired
    UserDetailsService userDetailsService;

  @Autowired
    OtpRepository otpRepository;

  @Autowired
    EmailService emailService;

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome this endpoint is not secure";
    }

    @PostMapping("/addNewUser")
    public ResponseEntity<?> addNewUser(@RequestBody SignupRequest userInfo) {
        SignupResponse response=service.createUser(userInfo);
        if(response.getStatus()==400){
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        if(response.getStatus()==200){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
         response.setStatus(500);
         response.setMessage("Server error");
         return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/user/userProfile")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String userProfile() {
        return "Welcome to User Profile";
    }

    @GetMapping("/admin/adminProfile")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String adminProfile() {
        return "Welcome to Admin Profile";
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        AuthResponse response = new AuthResponse();

        if (authRequest.getEmail() == null || authRequest.getPassword() == null) {
            response.setMessage("All fields are required !");
            response.setStatus(400);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Optional<UserInfo> optionalUserInfo = userInfoRepository.findByEmail(authRequest.getEmail());

        if (optionalUserInfo.isEmpty()) {
            response.setMessage("User doesn't exist !");
            response.setStatus(400);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            // Attempt to authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );

            if (authentication.isAuthenticated()) {
                UserInfo existingUserInfo = optionalUserInfo.get();

                if(existingUserInfo.isDeleted()){
                    response.setMessage("Account is already deleted");
                    response.setStatus(400);
                    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
                }else{
                    if(existingUserInfo.isActive()){
                        String token = jwtService.generateToken(authRequest.getEmail());
                        String refreshToken=jwtService.generateRefreshToken(authRequest.getEmail());

                        List<Token> tokenList=tokenRepository.findAllByUserId(existingUserInfo.getId());
                        if(!tokenList.isEmpty()){
                            tokenRepository.deleteAll(tokenList);
                        }

                        Token newToken=new Token();
                        newToken.setAccessToken(token);
                        newToken.setUserId(existingUserInfo.getId());
                        newToken.setValid(true);
                        tokenRepository.save(newToken);

                        response.setStatus(200);
                        response.setToken(token);
                        response.setRefreshToken(refreshToken);
                        response.setId(existingUserInfo.getId());
                        response.setName(existingUserInfo.getName());
                        response.setMessage("User login successfully !");
                        response.setEmail(existingUserInfo.getEmail());
                        response.setRoles(existingUserInfo.getRoles());
                        response.setActive(existingUserInfo.isActive());
                        response.setDeleted(existingUserInfo.isDeleted());
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    }else{
                        // user exist but not activate
                        List<Otp> existingOtps=otpRepository.findAllByUserId(existingUserInfo.getId());
                        if(!existingOtps.isEmpty()){
                            otpRepository.deleteAll(existingOtps);
                        }
                        int randomCode=generateRandom4DigitNumber();
                        Otp newOtp=new Otp();
                        LocalDateTime currentDate=LocalDateTime.now();
                        newOtp.setCreatedDate(currentDate);
                        newOtp.setExpiredDate(currentDate.plusMinutes(1));
                        newOtp.setOtpCode(randomCode);
                        newOtp.setUserId(existingUserInfo.getId());
                        otpRepository.save(newOtp);
                        try {
                            emailService.sendOtpEmail(existingUserInfo.getEmail(), randomCode, existingUserInfo.getName());
                        } catch (MessagingException e) {
                            // Handle the exception
                        }
                        response.setStatus(400);
                        response.setId(existingUserInfo.getId());
                        response.setName(existingUserInfo.getName());
                        response.setMessage("Your account has not been activated yet. Otp code has sent to your email");
                        response.setEmail(existingUserInfo.getEmail());
                        response.setRoles(existingUserInfo.getRoles());
                        response.setActive(existingUserInfo.isActive());
                        response.setDeleted(existingUserInfo.isDeleted());
                        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

                    }
                }


            } else {
                response.setStatus(401); // You can keep 403 for unauthorized
                response.setMessage("Invalid email or password");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            response.setStatus(401);
            response.setMessage("Invalid email or password");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    private int generateRandom4DigitNumber() {
        Random random = new Random();
        return 1000 + random.nextInt(9000); // Generates a random number between 1000 and 9999
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpRequest otpRequest){
        OtpResponse response=service.verifyOtp(otpRequest);
        if(response.getStatus()==400){
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        if(response.getStatus()==200){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        response.setStatus(500);
        response.setMessage("Server error");
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);

    }


    @PostMapping("/resend-otp/{email}")
    public ResponseEntity<?> resendOtp(@PathVariable String email){
        DefaultResponse response=service.resendOtp(email);
        if(response.getStatus()==400){
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        if(response.getStatus()==200){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        response.setStatus(500);
        response.setMessage("Server error");
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @PostMapping("/find-user-exist/{email}")
    public ResponseEntity<?> findUserExistByEmail(@PathVariable String email){
        DefaultResponse response=service.findUserExistByEmail(email);
        if(response.getStatus()==400){
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        if(response.getStatus()==200){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        response.setStatus(500);
        response.setMessage("Server error");
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){
        DefaultResponse response=service.resetPassword(resetPasswordRequest);
        if(response.getStatus()==400){
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        if(response.getStatus()==200){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        response.setStatus(500);
        response.setMessage("Server error");
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/get-refresh-token")
    public ResponseEntity<?> getRefreshToken(@RequestHeader("Authorization") String authorizationHeader, @RequestBody RefreshTokenRequest refreshTokenRequest){
          RefreshTokenResponse response=new RefreshTokenResponse();
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.setMessage("Invalid or missing Bearer token.");
            response.setStatus(400);
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);

        }
        // Extract the access token from the Authorization header
        String accessToken = authorizationHeader.substring(7);
        if (accessToken.isEmpty()) {
            response.setMessage("Access token is required.");
            response.setStatus(400);

           return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        if(refreshTokenRequest.getRefreshToken()==null){
            response.setMessage("Refresh token is required.");
            response.setStatus(400);
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        String refreshToken = refreshTokenRequest.getRefreshToken();
        String emailR= jwtService.extractUsername(refreshToken);
        String emailA=jwtService.extractUsername(accessToken);

        if(!Objects.equals(emailA, emailR)){
            response.setMessage("Unauthorized request !");
            response.setStatus(400);
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);

        }
        String tokenType = jwtService.extractClaim(refreshToken, claims -> claims.get("tokenType", String.class));
        if (!"refreshToken".equals(tokenType)) {
            response.setMessage("Invalid refresh token");
            response.setStatus(400);
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);

        }

        UserDetails userDetails=userDetailsService.loadUserByUsername(emailA);

        if(userDetails==null){
            response.setMessage("Can't find userdetails !");
            response.setStatus(400);
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }


        if(!jwtService.validateToken(refreshToken,userDetails)){
            response.setMessage("Refresh token is invalid !");
            response.setStatus(400);
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);

        }

        Optional <UserInfo> optionalUserInfo =userInfoRepository.findByEmail(emailA);
        if(optionalUserInfo.isEmpty()){
            response.setMessage("Unauthorized request !");
            response.setStatus(400);
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);

        }
        UserInfo userInfo=optionalUserInfo.get();
        List<Token> tokenList= tokenRepository.findAllByUserId(userInfo.getId());
        if(!tokenList.isEmpty()){
            tokenRepository.deleteAll(tokenList);
        }

        String  newAccessToken= jwtService.generateToken(userInfo.getEmail());
        Token newToken=new Token();
        newToken.setAccessToken(newAccessToken);
        newToken.setUserId(userInfo.getId());
        newToken.setValid(true);
        tokenRepository.save(newToken);

        response.setMessage("Fetch access token successfully !");
        response.setStatus(200);
        response.setAccessToken(newAccessToken);
        return new ResponseEntity<>(response,HttpStatus.OK);


    }

    @PostMapping("/logout/{userId}")
    public ResponseEntity<?> logoutUser(@PathVariable Long userId){
        DefaultResponse response=service.logoutUser(userId);
        if(response.getStatus()==400){
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        if(response.getStatus()==200){
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        response.setStatus(500);
        response.setMessage("Server error");
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
