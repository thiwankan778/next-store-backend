package com.TutorTrack.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class MainCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserInfo user;

    @JsonIgnore
    @OneToMany(mappedBy = "mainCategory", cascade = CascadeType.ALL)
    private List<SubCategory> subCategories;

}
