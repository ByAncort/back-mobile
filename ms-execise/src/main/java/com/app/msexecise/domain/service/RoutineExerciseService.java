package com.app.msexecise.domain.service;

import com.app.msexecise.domain.model.RoutineExercise;
import com.app.msexecise.domain.repository.RoutineExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoutineExerciseService {

    private final RoutineExerciseRepository routineExerciseRepository;

    public RoutineExercise createRoutineExercise(RoutineExercise routineExercise) {
        if (routineExercise == null) {
            throw new IllegalArgumentException("Routine exercise cannot be null");
        }
        if (routineExercise.getWorkoutRoutine() == null) {
            throw new IllegalArgumentException("Workout routine cannot be null");
        }
        if (routineExercise.getExercise() == null) {
            throw new IllegalArgumentException("Exercise cannot be null");
        }
        if (routineExercise.getSets() == null || routineExercise.getSets() <= 0) {
            throw new IllegalArgumentException("Sets must be greater than 0");
        }
        if (routineExercise.getReps() == null || routineExercise.getReps() <= 0) {
            throw new IllegalArgumentException("Reps must be greater than 0");
        }

        return routineExerciseRepository.save(routineExercise);
    }

    @Transactional(readOnly = true)
    public List<RoutineExercise> getAllRoutineExercises() {
        return routineExerciseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<RoutineExercise> getRoutineExerciseById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid routine exercise ID");
        }
        return routineExerciseRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<RoutineExercise> getRoutineExercisesByWorkoutRoutine(Long workoutRoutineId) {
        if (workoutRoutineId == null || workoutRoutineId <= 0) {
            throw new IllegalArgumentException("Invalid workout routine ID");
        }
        return routineExerciseRepository.findByWorkoutRoutineId(workoutRoutineId);
    }

    @Transactional(readOnly = true)
    public List<RoutineExercise> getRoutineExercisesByExercise(Long exerciseId) {
        if (exerciseId == null || exerciseId <= 0) {
            throw new IllegalArgumentException("Invalid exercise ID");
        }
        return routineExerciseRepository.findByExerciseId(exerciseId);
    }

    public RoutineExercise updateRoutineExercise(Long id, RoutineExercise exerciseDetails) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid routine exercise ID");
        }
        if (exerciseDetails == null) {
            throw new IllegalArgumentException("Routine exercise details cannot be null");
        }

        RoutineExercise existingRoutineExercise = routineExerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Routine exercise not found with id: " + id));

        // Actualizar campos
        if (exerciseDetails.getSets() != null) {
            if (exerciseDetails.getSets() <= 0) {
                throw new IllegalArgumentException("Sets must be greater than 0");
            }
            existingRoutineExercise.setSets(exerciseDetails.getSets());
        }

        if (exerciseDetails.getReps() != null) {
            if (exerciseDetails.getReps() <= 0) {
                throw new IllegalArgumentException("Reps must be greater than 0");
            }
            existingRoutineExercise.setReps(exerciseDetails.getReps());
        }

        if (exerciseDetails.getRestTime() != null) {
            existingRoutineExercise.setRestTime(exerciseDetails.getRestTime());
        }

        return routineExerciseRepository.save(existingRoutineExercise);
    }

    public void deleteRoutineExercise(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid routine exercise ID");
        }

        RoutineExercise routineExercise = routineExerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Routine exercise not found with id: " + id));

        routineExerciseRepository.delete(routineExercise);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        if (id == null || id <= 0) {
            return false;
        }
        return routineExerciseRepository.existsById(id);
    }
}