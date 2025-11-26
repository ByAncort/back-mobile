package com.app.msexecise.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoutineExerciseRequestDTO {

    @NotNull(message = "Exercise ID is required")
    @Positive(message = "Exercise ID must be positive")
    private Long exerciseId;

    @NotNull(message = "Workout routine ID is required")
    @Positive(message = "Workout routine ID must be positive")
    private Long workoutRoutineId;

    @NotNull(message = "Sets are required")
    @Positive(message = "Sets must be greater than 0")
    private Integer sets;

    @NotNull(message = "Reps are required")
    @Positive(message = "Reps must be greater than 0")
    private Integer reps;

    @Positive(message = "Rest time must be positive")
    private Integer restTime;
}