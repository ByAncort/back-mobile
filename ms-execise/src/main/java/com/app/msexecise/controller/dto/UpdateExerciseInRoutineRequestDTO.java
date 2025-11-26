package com.app.msexecise.controller.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateExerciseInRoutineRequestDTO {

    @Positive(message = "Sets must be greater than 0")
    private Integer sets;

    @Positive(message = "Reps must be greater than 0")
    private Integer reps;

    @Positive(message = "Rest time must be positive")
    private Integer restTime;
}