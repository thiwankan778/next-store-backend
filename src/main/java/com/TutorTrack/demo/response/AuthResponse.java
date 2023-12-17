package com.TutorTrack.demo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private Long id;
    private String name;
    private String email;
    private String roles;
    private String message;
    private String token;
    private boolean isActive;
    private boolean isDeleted;
    private int status;
    private String refreshToken;
}
