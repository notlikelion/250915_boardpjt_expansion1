package com.example.boardpjt.model.dto;

public class CommentDTO {
    public record Request(
            Long postId,
            String content,
            String username
    ) {}
}
