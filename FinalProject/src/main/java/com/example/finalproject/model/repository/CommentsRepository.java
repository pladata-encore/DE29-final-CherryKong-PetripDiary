package com.example.finalproject.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.finalproject.model.entity.CommentsEntity;
import com.example.finalproject.model.entity.BoardEntity;

import java.util.List;

public interface CommentsRepository extends JpaRepository<CommentsEntity, Long> {
    List<CommentsEntity> findByBoard(BoardEntity board);
}