package com.example.finalproject.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.finalproject.model.entity.TravelPlanEntity;
import com.example.finalproject.model.entity.UserEntity;

public interface TravelPlanRepository extends JpaRepository<TravelPlanEntity, Long>{
    
    List<TravelPlanEntity> findTravelPlanByUserid(UserEntity userid);
}
