package com.TutorTrack.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String city;
    private String address;
    private String contact;
    @Column(columnDefinition = "TEXT")
    private String storeImageUrl;
    private boolean isDeleted=false;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id") // Name of the foreign key column in Store table
    private UserInfo user;



}
