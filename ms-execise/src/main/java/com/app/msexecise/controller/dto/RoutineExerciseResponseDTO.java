package com.app.msexecise.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoutineExerciseResponseDTO {
    private Long id;
    private ExerciseResponseDTO exercise;
    private WorkoutRoutineSimpleDTO workoutRoutine;
    private Integer sets;
    private Integer reps;
    private Integer restTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}