package com.example.finalproject.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.finalproject.model.entity.DogEntity;

@Repository
public interface DogRepository extends JpaRepository<DogEntity, String> {
    
    Optional<DogEntity> findDogByUserid(String userid);

}