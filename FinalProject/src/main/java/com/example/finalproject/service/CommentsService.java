package com.example.finalproject.service;

import com.example.finalproject.model.dto.CommentsDto;
import com.example.finalproject.model.entity.BoardEntity;
import com.example.finalproject.model.entity.CommentsEntity;
import com.example.finalproject.model.entity.UserEntity;
import com.example.finalproject.model.repository.BoardRepository;
import com.example.finalproject.model.repository.CommentsRepository;
import com.example.finalproject.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentsService {

    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    public List<CommentsDto> getCommentsByBoardId(Long boardId) {
        Optional<BoardEntity> boardEntityOptional = boardRepository.findById(boardId);
        if (boardEntityOptional.isPresent()) {
            List<CommentsEntity> comments = commentsRepository.findByBoard(boardEntityOptional.get());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            return comments.stream().map(comment -> {
                CommentsDto dto = new CommentsDto();
                dto.setCommentId(comment.getCommentId());
                dto.setBoardId(comment.getBoard().getBoardid());
                dto.setUserId(comment.getUser().getUserid());
                dto.setNickname(comment.getUser().getNickname());
                dto.setCommentText(comment.getCommentText());
                dto.setCommentTime(comment.getCommentTime().format(formatter));
                return dto;
            }).collect(Collectors.toList());
        }
        return List.of();
    }

    public void addComment(CommentsDto commentsDto) {
        Optional<BoardEntity> boardEntityOptional = boardRepository.findById(commentsDto.getBoardId());
        Optional<UserEntity> userEntityOptional = userRepository.findByUserid(commentsDto.getUserId());
        if (boardEntityOptional.isPresent() && userEntityOptional.isPresent()) {
            CommentsEntity commentsEntity = new CommentsEntity();
            commentsEntity.setBoard(boardEntityOptional.get());
            commentsEntity.setUser(userEntityOptional.get());
            commentsEntity.setNickname(userEntityOptional.get().getNickname());
            commentsEntity.setCommentText(commentsDto.getCommentText());
            commentsEntity.setCommentTime(LocalDateTime.now());
            commentsRepository.save(commentsEntity);
        }
    }

    public boolean deleteComment(Long commentId, String userId) {
        Optional<CommentsEntity> commentOptional = commentsRepository.findById(commentId);
        if (commentOptional.isPresent()) {
            CommentsEntity comment = commentOptional.get();
            if (comment.getUser().getUserid().equals(userId)) {
                commentsRepository.delete(comment);
                return true;
            } else {
                return false; // 권한이 없는 경우
            }
        } else {
            return false; // 댓글을 찾을 수 없는 경우
        }
    }

}
