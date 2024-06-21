package com.example.finalproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.finalproject.model.dto.TravelPlanDto;
import com.example.finalproject.service.TravelPlanService;


@RestController
@RequestMapping("/api")
public class TravelPlanController {


    @Autowired
    private TravelPlanService travelPlanService;

    @PostMapping("/saveCourses")
    public String saveCourses(@RequestBody TravelPlanDto travelPlanDto) {

        try {
            travelPlanService.insertCourse(travelPlanDto);
            // 저장 성공 메시지를 반환합니다.
            return "저장이 완료되었습니다.";
        } catch (Exception e) {
            // 예외가 발생한 경우 에러 메시지를 반환합니다.
            return "Error occurred while saving courses: " + e.getMessage();
        }
    }

    @GetMapping("/deleteCourse/{courseid}")
    public String deleteCourses(@PathVariable(name = "courseid") Long courseid) {
        try {
            travelPlanService.deleteCourse(courseid);
            // 저장 성공 메시지를 반환합니다.
            return "삭제가 완료되었습니다.";
        } catch (Exception e) {
            // 예외가 발생한 경우 에러 메시지를 반환합니다.
            return "Error occurred while saving courses: " + e.getMessage();
        }
    }
    
}
