package com.example.finalproject.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.finalproject.model.dao.DogDao;
import com.example.finalproject.model.dto.DogDto;
import com.example.finalproject.model.entity.DogEntity;
import com.example.finalproject.model.entity.UserEntity;
import com.example.finalproject.model.repository.DogRepository;
import com.example.finalproject.model.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;


@Service
public class DogService {

    @Autowired
    private DogRepository dogRepository;

    private UserRepository userRepository;
    
    @Autowired
    private DogDao dogDao;
    
    public DogService(DogRepository dogRepository, UserRepository userRepository) {
        this.dogRepository = dogRepository;
        this.userRepository = userRepository;
    }

    public DogEntity findDogByUserId(String userid) {
        return dogRepository.findDogByUserid(userid)
                .orElseThrow(() -> new EntityNotFoundException("Dog not found for the user: " + userid));
    }

    public DogDto findByDogid(String userid) {
        DogEntity dogEntity = dogDao.findDogByUserid(userid);
        if (dogEntity != null) {
            DogDto dogDto = new DogDto();
            dogDto.setUserid(dogEntity.getUserid());
            dogDto.setDogname(dogEntity.getDogname());
            dogDto.setDogbirth(dogEntity.getDogbirth());
            dogDto.setDogweight(dogEntity.getDogweight());
            dogDto.setDogphotoUrl(dogEntity.getDogphotoUrl());
            return dogDto;
        } else {
            return null;
        }
    }
    

    public void insertDog(@RequestPart("image") MultipartFile multipartFile,
        @RequestParam("InputName") String dogname,
        @RequestParam("InputBirthday") String inputBirthday,
        @RequestParam("InputWeight") float dogweight,
        RedirectAttributes redirectAttributes) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userid = authentication.getName();
    
        Optional<UserEntity> userOptional = userRepository.findByUserid(userid);
        UserEntity user = userOptional.get();
    
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dogbirth = LocalDate.parse(inputBirthday, formatter);
    
        DogEntity dogEntity = new DogEntity();
        dogEntity.setDogname(dogname);
        dogEntity.setDogbirth(dogbirth);
        dogEntity.setDogweight(dogweight);
        dogEntity.setUser(user);
        dogRepository.save(dogEntity);
        }

    public void updateDogProfile(String userid, String newDogname, LocalDate newDogbirth, float newdogweight, String dogphotourl) {
        DogEntity dogEntity = dogRepository.findDogByUserid(userid)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        dogEntity.setDogname(newDogname);
        dogEntity.setDogbirth(newDogbirth);
        dogEntity.setDogweight(newdogweight);
        dogEntity.setDogphotoUrl(dogphotourl);
        dogRepository.save(dogEntity);
    }

}