package com.example.finalproject.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository; // 수정됨

import com.example.finalproject.model.dao.UserDao;
import com.example.finalproject.model.entity.UserEntity;
import com.example.finalproject.model.repository.UserRepository;

@Repository // 수정됨
public class UserDaoImpl implements UserDao{

    @Autowired
    private UserRepository userRepository;

    @Override
    public void deleteById(String userid) {
        userRepository.deleteById(userid);
    }

    @Override
    public UserEntity findByUserId(String userid) {
        // 수정됨: 안전한 데이터 접근을 위해 findById를 사용하고, 결과가 없는 경우 처리
        return userRepository.findById(userid).orElse(null); // 필요에 따라 예외 처리 변경 가능
    }

    @Override
    public void insertUserId(UserEntity userEntity) {
        userRepository.save(userEntity);
    }

    @Override
    public void updateUserId(UserEntity userEntity) {
        userRepository.save(userEntity);
    }
    
}
