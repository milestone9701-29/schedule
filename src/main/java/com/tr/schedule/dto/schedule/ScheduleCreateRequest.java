package com.tr.schedule.dto.schedule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 스케쥴 작성 시 필요한 입력 요구사항 : title, content
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class ScheduleCreateRequest {
    @NotBlank @Size(max=50)
    private String title;
    @NotBlank @Size(max=200)
    private String content;
}
