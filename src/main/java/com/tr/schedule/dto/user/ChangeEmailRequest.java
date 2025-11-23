package com.tr.schedule.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class ChangeEmailRequest {
    @NotBlank @Size(max=100)
    private String currentPassword;  // 이쪽이 더 실무스러움. 굿
    @NotBlank @Size(max=100)
    private String newEmail;

    public ChangeEmailRequest(String currentPassword, String newEmail) {
        this.currentPassword = currentPassword;
        this.newEmail = newEmail;
    }
}
