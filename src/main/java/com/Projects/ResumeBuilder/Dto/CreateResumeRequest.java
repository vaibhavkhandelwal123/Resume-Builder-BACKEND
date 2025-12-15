package com.Projects.ResumeBuilder.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class CreateResumeRequest {

    @NotBlank(message = "Title is required")
    private String title;

}
