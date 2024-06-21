package com.example.finalproject.model.dto;

import java.time.LocalDate;

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
public class DogDto {
    
    private String userid;
    private String dogname;
    private LocalDate dogbirth;
    private float dogweight;
    private String dogphotoUrl;
}