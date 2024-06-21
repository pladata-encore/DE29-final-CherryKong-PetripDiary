package com.example.finalproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.security.core.Authentication;
import com.example.finalproject.model.dao.UserDao;
import com.example.finalproject.model.dto.UserDto;
import com.example.finalproject.model.entity.UserEntity;
import com.example.finalproject.model.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder; 
    
    @Autowired
    UserRepository userRepository;

    @Autowired
    private UserDao userDao;




    public UserDto findByUserId(String userid) {
        UserEntity userEntity = userDao.findByUserId(userid);
        UserDto userDto = new UserDto();
        userDto.setUserid(userid);
        userDto.setPassword(userEntity.getPassword());
        userDto.setNickname(userEntity.getNickname());
        return userDto;
    }


    public void insertUserId(UserDto userDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserid(userDto.getUserid());
        userEntity.setPassword(userDto.getPassword());
        userEntity.setNickname(userDto.getNickname());
        
    }

    public void updateUserId(UserDto userDto) {
        UserEntity userEntity = userDao.findByUserId(userDto.getUserid());
        userEntity.setPassword(userDto.getPassword());
        userEntity.setNickname(userDto.getNickname());
        userDao.updateUserId(userEntity);
    }

    public void updateUserNickname(String newNickname) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userid = authentication.getName(); 
        UserEntity userEntity = userRepository.findById(userid)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        userEntity.setNickname(newNickname);
        userRepository.save(userEntity);
    }


    public void deleteById(String userid) {
        userDao.deleteById(userid);
    }
    

    // 로그인 성공시 유무 저장
    public void updateIsLogin(String userid, Boolean isLogin) {
        UserEntity userEntity = userDao.findByUserId(userid);
        userEntity.setIsLogin(isLogin);
        userDao.updateUserId(userEntity);
    }
    
    public void joinUserDto(UserEntity userEntity) {
        
        // 권한 적용 
        userEntity.setRole("USER");
        if(userEntity.getRole().equals("admin")) {
            userEntity.setRole("ADMIN");
        } else if(userEntity.getRole().equals("manager")) {
            userEntity.setRole("MANAGER");
        }

        // 비밀번호 암호화 적용
        String rawPassword = userEntity.getPassword();
        String encodedPwd = bCryptPasswordEncoder.encode(rawPassword);
        userEntity.setPassword(encodedPwd);

        userEntity.setIsLogin(false);

        // 신규 유저 database에 저장!!
        if (userDao.findByUserId(userEntity.getUserid()) == null){
            userRepository.save(userEntity);
        }
    }

    public String getUserNickname(String userid) {
        UserEntity entity = userDao.findByUserId(userid);
        return entity.getNickname();    
    }



    public UserDto getUserDto(String userid) {
        UserEntity userEntity = userDao.findByUserId(userid);
        UserDto dto = new UserDto();

        dto.setUserid(userEntity.getUserid());
        dto.setPassword(userEntity.getPassword());
        dto.setNickname(userEntity.getNickname());
        dto.setRole(userEntity.getRole());

        return dto;
    }
}