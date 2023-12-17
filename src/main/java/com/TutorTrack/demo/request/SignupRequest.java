package com.TutorTrack.demo.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    private String name;
    private String email;
    private String password;
    private String roles;
    private boolean isActive;
}
