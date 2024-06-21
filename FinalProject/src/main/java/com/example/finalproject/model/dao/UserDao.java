package com.example.finalproject.model.dao;

import com.example.finalproject.model.entity.UserEntity;

public interface UserDao {
    public void deleteById(String userid);
    public UserEntity findByUserId(String userid);
    public void insertUserId(UserEntity userEntity);
    public void updateUserId(UserEntity userEntity);
}
