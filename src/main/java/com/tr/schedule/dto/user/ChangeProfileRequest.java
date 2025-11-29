package com.tr.schedule.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class ChangeProfileRequest {
    @NotBlank @Size(max=30)
    private String username;
    @Size(max=254)
    private String profileImageUrl;
    @Size(max=200)
    private String bio;

    public ChangeProfileRequest(String username, String profileImageUrl, String bio){
        this.username = username;
        this.profileImageUrl = profileImageUrl;
        this.bio = bio;
    }
}
