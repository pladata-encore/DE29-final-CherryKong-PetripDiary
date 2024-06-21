package com.example.finalproject.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommentsDto {

    private Long commentId;
    private Long boardId;
    private String userId;
    private String commentText;
    private String commentTime;
    private String nickname;
}
