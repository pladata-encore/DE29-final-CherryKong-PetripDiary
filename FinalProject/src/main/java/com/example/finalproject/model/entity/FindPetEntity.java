package com.example.finalproject.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity(name = "FindPetEntity")
@Table(name = "findPet")
public class FindPetEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @ManyToOne
    @JoinColumn(name = "userid", referencedColumnName = "userid")
    private UserEntity userid;

    private String title;

    private String petName;

    private String petBreed;

    private String petSex;

    private String petAge;

    private String lostPlace; 

    private String lostTime;

    private String contact; 

    private String dogphotoUrl;

    private String dogphotoUrl2;

    private String dogphotoUrl3;

    private String dogphotoUrl4;

    private String text;

    private LocalDateTime time;

}
