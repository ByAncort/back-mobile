package com.app.msexecise.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutRoutineSimpleDTO {
    private Long id;
    private String name;
    private String description;
    private String duration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}