package com.app.msexecise.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutRoutineResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String duration;
    private String username;
    private List<RoutineExerciseResponseDTO> exercises = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}