package com.tr.schedule.dto.comment;


import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


// 댓글 생성 시 필요 입력 요구사항 : content
@Getter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class CommentCreateRequest {
    @NotBlank @Size(max=100)
    private String content;
}
