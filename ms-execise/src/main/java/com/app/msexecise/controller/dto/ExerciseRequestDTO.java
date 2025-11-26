package com.app.msexecise.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseRequestDTO {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Type is required")
    @Size(max = 50, message = "Type cannot exceed 50 characters")
    private String type;

    @NotBlank(message = "Muscle is required")
    @Size(max = 50, message = "Muscle cannot exceed 50 characters")
    private String muscle;

    @Size(max = 50, message = "Equipment cannot exceed 50 characters")
    private String equipment;

    @Size(max = 20, message = "Difficulty cannot exceed 20 characters")
    private String difficulty;

    @Size(max = 2000, message = "Instructions cannot exceed 2000 characters")
    private String instructions;
}