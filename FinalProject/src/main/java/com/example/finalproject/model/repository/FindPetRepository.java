package com.example.finalproject.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.finalproject.model.entity.FindPetEntity;
import com.example.finalproject.model.entity.UserEntity;

import java.util.List;

public interface FindPetRepository extends JpaRepository<FindPetEntity, Long>, JpaSpecificationExecutor<FindPetEntity>{
    List<FindPetEntity> findByUserid(UserEntity userEntity);
}
