package com.example.finalproject.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.finalproject.model.dto.FindPetDto;
import com.example.finalproject.model.entity.FindPetEntity;
import com.example.finalproject.model.entity.UserEntity;
import com.example.finalproject.model.repository.FindPetRepository;
import com.example.finalproject.model.repository.UserRepository;

@Service
public class FindPetService {
    
    @Autowired
    private FindPetRepository findPetRepository;

    @Autowired
    private UserRepository userRepository;

    private FindPetService(FindPetRepository findPetRepository) {
        this.findPetRepository = findPetRepository;
    }

    public void insert(FindPetDto findPetDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userid = authentication.getName();

        Optional<UserEntity> userOptional = userRepository.findByUserid(userid);
        UserEntity user = userOptional.get();
        
        LocalDateTime currentTime = LocalDateTime.now();

        FindPetEntity findPetEntity = new FindPetEntity();
        findPetEntity.setBoardId(findPetDto.getBoardId());
        findPetEntity.setUserid(user);
        findPetEntity.setTitle(findPetDto.getTitle());
        findPetEntity.setPetName(findPetDto.getPetName());
        findPetEntity.setPetBreed(findPetDto.getPetBreed());
        findPetEntity.setPetAge(findPetDto.getPetAge());
        findPetEntity.setPetSex(findPetDto.getPetSex());
        findPetEntity.setLostPlace(findPetDto.getLostPlace());
        findPetEntity.setLostTime(findPetDto.getLostTime());
        findPetEntity.setContact(findPetDto.getContact());
        findPetEntity.setDogphotoUrl(findPetDto.getDogphotoUrl());
        findPetEntity.setDogphotoUrl2(findPetDto.getDogphotoUrl2());
        findPetEntity.setDogphotoUrl3(findPetDto.getDogphotoUrl3());
        findPetEntity.setDogphotoUrl4(findPetDto.getDogphotoUrl4());
        findPetEntity.setText(findPetDto.getText());
        findPetEntity.setTime(currentTime);

        findPetRepository.save(findPetEntity);
    }

    public List<FindPetDto> searchFindPet(String searchType, String keyword) {
        Specification<FindPetEntity> spec = Specification.where(null);
        
        if (StringUtils.hasText(keyword)) {
            switch (searchType) {
                case "title":
                    spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(root.get("title"), "%" + keyword + "%"));
                    break;
                case "userid":
                    spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(root.get("userid").get("userid"), "%" + keyword + "%"));
                    break;
                case "petBreed":
                    spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(root.get("petBreed"), "%" + keyword + "%"));
                    break;
                case "petName":
                    spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(root.get("petName"), "%" + keyword + "%"));
                    break;
                case "lostPlace":
                    spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(root.get("lostPlace"), "%" + keyword + "%"));
                break;
            }
        }

        List<FindPetEntity> findPetEntities = findPetRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "boardId"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

        return findPetEntities.stream().map(entity -> {
            FindPetDto dto = new FindPetDto();
            dto.setBoardId(entity.getBoardId());
            dto.setUserid(entity.getUserid().getUserid());
            dto.setNickname(entity.getUserid().getNickname());
            dto.setTitle(entity.getTitle());
            dto.setPetName(entity.getPetName());
            dto.setPetAge(entity.getPetAge());
            dto.setPetBreed(entity.getPetBreed());
            dto.setPetSex(entity.getPetSex());
            dto.setLostPlace(entity.getLostPlace());
            dto.setLostTime(entity.getLostTime());
            dto.setContact(entity.getContact());
            dto.setDogphotoUrl(entity.getDogphotoUrl());
            dto.setDogphotoUrl2(entity.getDogphotoUrl2());
            dto.setDogphotoUrl3(entity.getDogphotoUrl3());
            dto.setDogphotoUrl4(entity.getDogphotoUrl4());
            dto.setText(entity.getText());
            String formattedTime = entity.getTime().format(formatter).toString();
            dto.setTime(formattedTime);
            return dto;
        }).collect(Collectors.toList());
    }

    public List<FindPetDto> getAllFindPet() {
        List<FindPetEntity> findPetEntities = findPetRepository.findAll(Sort.by(Sort.Direction.DESC, "boardId"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

        return findPetEntities.stream().map(entity -> {
                FindPetDto dto = new FindPetDto();
                dto.setBoardId(entity.getBoardId());
                dto.setUserid(entity.getUserid().getUserid());
                dto.setNickname(entity.getUserid().getNickname());
                dto.setTitle(entity.getTitle());
                dto.setPetName(entity.getPetName());
                dto.setPetAge(entity.getPetAge());
                dto.setPetBreed(entity.getPetBreed());
                dto.setPetSex(entity.getPetSex());
                dto.setLostPlace(entity.getLostPlace());
                dto.setLostTime(entity.getLostTime());
                dto.setContact(entity.getContact());
                dto.setDogphotoUrl(entity.getDogphotoUrl());
                dto.setDogphotoUrl2(entity.getDogphotoUrl2());
                dto.setDogphotoUrl3(entity.getDogphotoUrl3());
                dto.setDogphotoUrl4(entity.getDogphotoUrl4());
                dto.setText(entity.getText());
                String formattedTime = entity.getTime().format(formatter).toString();
                dto.setTime(formattedTime);
                return dto;
            }).collect(Collectors.toList());
    }

    public Optional<FindPetDto> getFindPetById(Long boardId) {
        Optional<FindPetEntity> findPetEntityOptional = findPetRepository.findById(boardId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

        if (findPetEntityOptional.isPresent()) {
            FindPetEntity entity = findPetEntityOptional.get();
            FindPetDto dto = new FindPetDto();
            dto.setBoardId(entity.getBoardId());
            dto.setUserid(entity.getUserid().getUserid());
            dto.setNickname(entity.getUserid().getNickname());
            dto.setTitle(entity.getTitle());
            dto.setPetName(entity.getPetName());
            dto.setPetAge(entity.getPetAge());
            dto.setPetBreed(entity.getPetBreed());
            dto.setPetSex(entity.getPetSex());
            dto.setLostPlace(entity.getLostPlace());
            dto.setLostTime(entity.getLostTime());
            dto.setContact(entity.getContact());
            dto.setDogphotoUrl(entity.getDogphotoUrl());
            dto.setDogphotoUrl2(entity.getDogphotoUrl2());
            dto.setDogphotoUrl3(entity.getDogphotoUrl3());
            dto.setDogphotoUrl4(entity.getDogphotoUrl4());
            dto.setText(entity.getText());
            String formattedTime = entity.getTime().format(formatter).toString();
            dto.setTime(formattedTime);
            
            return Optional.of(dto);
        } else {
            return Optional.empty();
        }
    }

    public void delete(Long boardId) {
        Optional<FindPetEntity> entityOptional = findPetRepository.findById(boardId);
        if (entityOptional.isPresent()) {
            findPetRepository.delete(entityOptional.get());
        } else {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다.");
        }
    }

}
