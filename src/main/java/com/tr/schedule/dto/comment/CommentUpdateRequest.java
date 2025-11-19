package com.tr.schedule.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 댓글 수정 시 필요 입력 요구 사항 : content
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class CommentUpdateRequest {
    @NotBlank @Size(max=100)
    private String content;
    @NotNull
    private Long version;

    public CommentUpdateRequest(String content, Long version) {
        this.content=content;
        this.version=version;
    }
}
