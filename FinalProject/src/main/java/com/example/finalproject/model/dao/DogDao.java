package com.example.finalproject.model.dao;


import com.example.finalproject.model.entity.DogEntity;


public interface DogDao {
    public DogEntity findDogByUserid(String userid);
    public void insertDogId(DogEntity dogEntity);
    public void updateDogId(DogEntity dogEntity);
}