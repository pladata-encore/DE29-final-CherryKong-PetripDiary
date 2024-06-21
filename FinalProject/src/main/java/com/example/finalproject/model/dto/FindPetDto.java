package com.example.finalproject.model.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

public class FindPetDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    private String userid;

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

    private String time;

    private String nickname;

}
