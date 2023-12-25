package com.TutorTrack.demo.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long ratingCount;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_id") // This refers to the foreign key in Rating table referencing Product table
    private Product product;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id") // This refers to the foreign key in Rating table referencing UserInfo table
    private UserInfo user;
}
