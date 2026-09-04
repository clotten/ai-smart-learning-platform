package com.ai.learning.user.dto;


import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileDTO {

    @Size(max = 20, message = "昵称最长20字")
    private String nickname;

    @Size(max = 50, message = "简历最长50字")
    private String bio;
}
