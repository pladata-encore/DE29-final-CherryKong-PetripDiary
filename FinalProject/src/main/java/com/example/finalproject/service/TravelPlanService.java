package com.example.finalproject.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.finalproject.model.dto.TravelPlanDto;
import com.example.finalproject.model.entity.TravelPlanEntity;
import com.example.finalproject.model.entity.UserEntity;
import com.example.finalproject.model.repository.TravelPlanRepository;
import com.example.finalproject.model.repository.UserRepository;

@Service
public class TravelPlanService {

    @Autowired
    private TravelPlanRepository travelPlanRepository;

    @Autowired
    private UserRepository userRepository;

    public TravelPlanService(TravelPlanRepository travelPlanRepository) {
        this.travelPlanRepository = travelPlanRepository;
    }

    public void insertCourse(TravelPlanDto travelPlanDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userid = authentication.getName();

        Optional<UserEntity> userOptional = userRepository.findByUserid(userid);
        UserEntity user = userOptional.get();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        LocalDateTime saveTime = LocalDateTime.now();

        String courses = travelPlanDto.getCourses();
        courses = courses.replace("[\"", "");
        courses = courses.replace("\"]", "");
        courses = courses.replace("\",\"", "  /  ");
        System.out.println(courses);

        TravelPlanEntity travelPlanEntity = new TravelPlanEntity();
        travelPlanEntity.setCoursetitle(travelPlanDto.getCoursetitle());
        travelPlanEntity.setCourses(courses);
        travelPlanEntity.setTime(saveTime.format(formatter));
        travelPlanEntity.setUserid((user));
        travelPlanRepository.save(travelPlanEntity);
    }

    public void deleteCourse(Long courseid) {
        travelPlanRepository.deleteById(courseid);
    }
    
}
