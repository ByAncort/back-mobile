package com.app.msexecise.domain.service;

import com.app.msexecise.domain.model.Exercise;
import com.app.msexecise.domain.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    public Exercise createExercise(Exercise exercise) {
        if (exercise == null) {
            throw new IllegalArgumentException("Exercise cannot be null");
        }
        if (exercise.getName() == null || exercise.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Exercise name cannot be null or empty");
        }

        // Verificar si ya existe un ejercicio con el mismo nombre
        Optional<Exercise> existingExercise = exerciseRepository.findByName(exercise.getName());
        if (existingExercise.isPresent()) {
            throw new IllegalArgumentException("Exercise with name '" + exercise.getName() + "' already exists");
        }

        return exerciseRepository.save(exercise);
    }

    @Transactional(readOnly = true)
    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Exercise> getExerciseById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid exercise ID");
        }
        return exerciseRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Exercise> getExercisesByMuscleGroup(String muscle) {
        if (muscle == null || muscle.trim().isEmpty()) {
            throw new IllegalArgumentException("Muscle group cannot be null or empty");
        }
        return exerciseRepository.findByMuscle(muscle);
    }

    @Transactional(readOnly = true)
    public List<Exercise> getExercisesByType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Exercise type cannot be null or empty");
        }
        return exerciseRepository.findByType(type);
    }

    @Transactional(readOnly = true)
    public List<Exercise> getExercisesByDifficulty(String difficulty) {
        if (difficulty == null || difficulty.trim().isEmpty()) {
            throw new IllegalArgumentException("Difficulty cannot be null or empty");
        }
        return exerciseRepository.findByDifficulty(difficulty);
    }

    @Transactional(readOnly = true)
    public List<Exercise> searchExercisesByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Search name cannot be null or empty");
        }
        return exerciseRepository.findByNameContainingIgnoreCase(name);
    }

    public Exercise updateExercise(Long id, Exercise exerciseDetails) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid exercise ID");
        }
        if (exerciseDetails == null) {
            throw new IllegalArgumentException("Exercise details cannot be null");
        }

        Exercise existingExercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercise not found with id: " + id));

        // Verificar si el nuevo nombre ya existe (excluyendo el ejercicio actual)
        if (exerciseDetails.getName() != null && !exerciseDetails.getName().equals(existingExercise.getName())) {
            Optional<Exercise> exerciseWithSameName = exerciseRepository.findByName(exerciseDetails.getName());
            if (exerciseWithSameName.isPresent() && !exerciseWithSameName.get().getId().equals(id)) {
                throw new IllegalArgumentException("Exercise with name '" + exerciseDetails.getName() + "' already exists");
            }
        }

        // Actualizar campos
        if (exerciseDetails.getName() != null) {
            existingExercise.setName(exerciseDetails.getName());
        }
        if (exerciseDetails.getType() != null) {
            existingExercise.setType(exerciseDetails.getType());
        }
        if (exerciseDetails.getMuscle() != null) {
            existingExercise.setMuscle(exerciseDetails.getMuscle());
        }
        if (exerciseDetails.getEquipment() != null) {
            existingExercise.setEquipment(exerciseDetails.getEquipment());
        }
        if (exerciseDetails.getDifficulty() != null) {
            existingExercise.setDifficulty(exerciseDetails.getDifficulty());
        }
        if (exerciseDetails.getInstructions() != null) {
            existingExercise.setInstructions(exerciseDetails.getInstructions());
        }

        return exerciseRepository.save(existingExercise);
    }

    public void deleteExercise(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid exercise ID");
        }

        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercise not found with id: " + id));

        // Verificar si el ejercicio está siendo usado en alguna rutina
        if (!exercise.getRoutineExercises().isEmpty()) {
            throw new IllegalStateException("Cannot delete exercise because it is used in one or more workout routines");
        }

        exerciseRepository.delete(exercise);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        if (id == null || id <= 0) {
            return false;
        }
        return exerciseRepository.existsById(id);
    }
}