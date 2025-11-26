package com.app.msexecise.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "workout_routines")
public class WorkoutRoutine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    private String duration;

    // NUEVO CAMPO: username del usuario que creó la rutina
    @Column(name = "username", nullable = false)
    private String username;

    @OneToMany(mappedBy = "workoutRoutine", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RoutineExercise> exercises = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructor actualizado con username
    public WorkoutRoutine(String name, String description, String duration, String username) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.username = username; // Nuevo parámetro
        this.exercises = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void addExercise(RoutineExercise exercise) {
        if (this.exercises == null) {
            this.exercises = new ArrayList<>();
        }
        exercise.setWorkoutRoutine(this);
        this.exercises.add(exercise);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeExercise(RoutineExercise exercise) {
        if (this.exercises != null) {
            this.exercises.remove(exercise);
            exercise.setWorkoutRoutine(null);
            this.updatedAt = LocalDateTime.now();
        }
    }
}