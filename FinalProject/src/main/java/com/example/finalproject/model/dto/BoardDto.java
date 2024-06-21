package com.example.finalproject.model.dto;

import java.time.LocalDate;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

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
public class BoardDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardid;

    @NotBlank
    private String title;

    @NotBlank
    private String text;

    private String userid;

    private String nickname;

    private String dogname;

    private String breed;

    private LocalDate dogbirth;

    private float dogweight;

    private String dogphotoUrl;

    private String dogphotoUrl2;

    private String dogphotoUrl3;

    private String dogphotoUrl4;

    private String time;

    private int likes;

    private int views;
}

