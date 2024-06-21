package com.example.finalproject.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.finalproject.model.entity.BoardEntity;
import com.example.finalproject.model.entity.UserEntity;

public interface BoardRepository extends JpaRepository<BoardEntity, Long>, JpaSpecificationExecutor<BoardEntity> {

    List<BoardEntity> findByUserid(UserEntity userid);

}

