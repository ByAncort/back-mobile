package com.app.msexecise.domain.service;

import com.app.msexecise.domain.model.Exercise;
import com.app.msexecise.domain.model.RoutineExercise;
import com.app.msexecise.domain.model.WorkoutRoutine;
import com.app.msexecise.domain.repository.WorkoutRoutineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class WorkoutRoutineService {

    private final WorkoutRoutineRepository workoutRoutineRepository;
    private final ExerciseService exerciseService;

    public WorkoutRoutine createWorkoutRoutine(WorkoutRoutine workoutRoutine) {
        if (workoutRoutine == null) {
            throw new IllegalArgumentException("Workout routine cannot be null");
        }
        if (workoutRoutine.getName() == null || workoutRoutine.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Workout routine name cannot be null or empty");
        }
        if (workoutRoutine.getUsername() == null || workoutRoutine.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        // Verificar si ya existe una rutina con el mismo nombre para el mismo usuario
        Optional<WorkoutRoutine> existingRoutine = workoutRoutineRepository.findByNameAndUsername(
                workoutRoutine.getName(), workoutRoutine.getUsername());
        if (existingRoutine.isPresent()) {
            throw new IllegalArgumentException("Workout routine with name '" + workoutRoutine.getName() + "' already exists for this user");
        }

        return workoutRoutineRepository.save(workoutRoutine);
    }

    @Transactional(readOnly = true)
    public List<WorkoutRoutine> getAllWorkoutRoutines() {
        return workoutRoutineRepository.findAll();
    }

    // NUEVO MÉTODO: Obtener rutinas por username
    @Transactional(readOnly = true)
    public List<WorkoutRoutine> getWorkoutRoutinesByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        return workoutRoutineRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<WorkoutRoutine> getWorkoutRoutineById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid workout routine ID");
        }
        return workoutRoutineRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<WorkoutRoutine> getWorkoutRoutinesByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        return workoutRoutineRepository.findByNameContainingIgnoreCase(name);
    }

    // NUEVO MÉTODO: Obtener rutinas por nombre y username
    @Transactional(readOnly = true)
    public List<WorkoutRoutine> getWorkoutRoutinesByNameAndUsername(String name, String username) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        return workoutRoutineRepository.findByNameContainingIgnoreCaseAndUsername(name, username);
    }

    public WorkoutRoutine updateWorkoutRoutine(Long id, WorkoutRoutine routineDetails) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid workout routine ID");
        }
        if (routineDetails == null) {
            throw new IllegalArgumentException("Workout routine details cannot be null");
        }

        WorkoutRoutine existingRoutine = workoutRoutineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workout routine not found with id: " + id));

        // Verificar si el nuevo nombre ya existe para el mismo usuario (excluyendo la rutina actual)
        if (routineDetails.getName() != null && !routineDetails.getName().equals(existingRoutine.getName())) {
            Optional<WorkoutRoutine> routineWithSameName = workoutRoutineRepository.findByNameAndUsername(
                    routineDetails.getName(), existingRoutine.getUsername());
            if (routineWithSameName.isPresent() && !routineWithSameName.get().getId().equals(id)) {
                throw new IllegalArgumentException("Workout routine with name '" + routineDetails.getName() + "' already exists for this user");
            }
        }

        // Actualizar campos
        if (routineDetails.getName() != null) {
            existingRoutine.setName(routineDetails.getName());
        }
        if (routineDetails.getDescription() != null) {
            existingRoutine.setDescription(routineDetails.getDescription());
        }
        if (routineDetails.getDuration() != null) {
            existingRoutine.setDuration(routineDetails.getDuration());
        }
        // El username no se puede actualizar (es el propietario de la rutina)

        return workoutRoutineRepository.save(existingRoutine);
    }

    public void deleteWorkoutRoutine(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid workout routine ID");
        }

        WorkoutRoutine workoutRoutine = workoutRoutineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Workout routine not found with id: " + id));

        workoutRoutineRepository.delete(workoutRoutine);
    }

    // NUEVO MÉTODO: Eliminar rutina por ID y username (para seguridad)
    public void deleteWorkoutRoutineByIdAndUsername(Long id, String username) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid workout routine ID");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        WorkoutRoutine workoutRoutine = workoutRoutineRepository.findByIdAndUsername(id, username)
                .orElseThrow(() -> new RuntimeException("Workout routine not found with id: " + id + " for user: " + username));

        workoutRoutineRepository.delete(workoutRoutine);
    }

    public WorkoutRoutine addExerciseToRoutine(Long routineId, Long exerciseId, Integer sets, Integer reps, Integer restTime) {
        if (routineId == null || routineId <= 0) {
            throw new IllegalArgumentException("Invalid workout routine ID");
        }
        if (exerciseId == null || exerciseId <= 0) {
            throw new IllegalArgumentException("Invalid exercise ID");
        }
        if (sets == null || sets <= 0) {
            throw new IllegalArgumentException("Sets must be greater than 0");
        }
        if (reps == null || reps <= 0) {
            throw new IllegalArgumentException("Reps must be greater than 0");
        }

        WorkoutRoutine routine = workoutRoutineRepository.findById(routineId)
                .orElseThrow(() -> new RuntimeException("Workout routine not found with id: " + routineId));

        Exercise exercise = exerciseService.getExerciseById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Exercise not found with id: " + exerciseId));

        // Verificar si el ejercicio ya está en la rutina
        boolean exerciseAlreadyExists = routine.getExercises().stream()
                .anyMatch(re -> re.getExercise().getId().equals(exerciseId));

        if (exerciseAlreadyExists) {
            throw new IllegalArgumentException("Exercise is already in the workout routine");
        }

        RoutineExercise routineExercise = RoutineExercise.builder()
                .exercise(exercise)
                .sets(sets)
                .reps(reps)
                .restTime(restTime)
                .build();

        routine.addExercise(routineExercise);
        return workoutRoutineRepository.save(routine);
    }

    public WorkoutRoutine removeExerciseFromRoutine(Long routineId, Long exerciseId) {
        if (routineId == null || routineId <= 0) {
            throw new IllegalArgumentException("Invalid workout routine ID");
        }
        if (exerciseId == null || exerciseId <= 0) {
            throw new IllegalArgumentException("Invalid exercise ID");
        }

        WorkoutRoutine routine = workoutRoutineRepository.findById(routineId)
                .orElseThrow(() -> new RuntimeException("Workout routine not found with id: " + routineId));

        RoutineExercise exerciseToRemove = routine.getExercises().stream()
                .filter(re -> re.getExercise().getId().equals(exerciseId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Exercise not found in workout routine"));

        routine.removeExercise(exerciseToRemove);
        return workoutRoutineRepository.save(routine);
    }

    public WorkoutRoutine updateExerciseInRoutine(Long routineId, Long exerciseId, Integer sets, Integer reps, Integer restTime) {
        if (routineId == null || routineId <= 0) {
            throw new IllegalArgumentException("Invalid workout routine ID");
        }
        if (exerciseId == null || exerciseId <= 0) {
            throw new IllegalArgumentException("Invalid exercise ID");
        }
        if (sets != null && sets <= 0) {
            throw new IllegalArgumentException("Sets must be greater than 0");
        }
        if (reps != null && reps <= 0) {
            throw new IllegalArgumentException("Reps must be greater than 0");
        }

        WorkoutRoutine routine = workoutRoutineRepository.findById(routineId)
                .orElseThrow(() -> new RuntimeException("Workout routine not found with id: " + routineId));

        RoutineExercise routineExercise = routine.getExercises().stream()
                .filter(re -> re.getExercise().getId().equals(exerciseId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Exercise not found in workout routine"));

        if (sets != null) {
            routineExercise.setSets(sets);
        }
        if (reps != null) {
            routineExercise.setReps(reps);
        }
        if (restTime != null) {
            routineExercise.setRestTime(restTime);
        }

        return workoutRoutineRepository.save(routine);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        if (id == null || id <= 0) {
            return false;
        }
        return workoutRoutineRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public List<WorkoutRoutine> getWorkoutRoutinesByExercise(Long exerciseId) {
        if (exerciseId == null || exerciseId <= 0) {
            throw new IllegalArgumentException("Invalid exercise ID");
        }
        return workoutRoutineRepository.findByExercisesExerciseId(exerciseId);
    }

    // NUEVO MÉTODO: Verificar si el usuario es el propietario de la rutina
    @Transactional(readOnly = true)
    public boolean isOwner(Long routineId, String username) {
        if (routineId == null || routineId <= 0) {
            return false;
        }
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return workoutRoutineRepository.existsByIdAndUsername(routineId, username);
    }
}