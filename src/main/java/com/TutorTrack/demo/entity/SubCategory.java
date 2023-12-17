package com.TutorTrack.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class SubCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "main_category_id")
    private MainCategory mainCategory;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserInfo user;
}
