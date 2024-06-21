package com.example.finalproject.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.finalproject.model.dao.DogDao;
import com.example.finalproject.model.entity.DogEntity;
import com.example.finalproject.model.repository.DogRepository;

@Repository
public class DogDaoImpl implements DogDao{
    
    @Autowired
    private DogRepository dogRepository;


    @Override
    public DogEntity findDogByUserid(String userid) {
        return dogRepository.findDogByUserid(userid).orElse(null);
    }


    @Override
    public void insertDogId(DogEntity dogEntity) {
        dogRepository.save(dogEntity);
    }

    @Override
    public void updateDogId(DogEntity dogEntity) {
        dogRepository.save(dogEntity);
    }
}