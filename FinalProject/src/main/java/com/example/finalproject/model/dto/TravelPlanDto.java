package com.example.finalproject.model.dto;

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
public class TravelPlanDto {
    private Long courseid;
    private String userid;
    private String coursetitle;
    private String courses;
    private String time;
}
