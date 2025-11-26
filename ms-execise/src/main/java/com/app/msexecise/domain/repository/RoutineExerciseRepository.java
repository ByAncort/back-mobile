package com.app.msexecise.domain.repository;

import com.app.msexecise.domain.model.RoutineExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoutineExerciseRepository extends JpaRepository<RoutineExercise, Long> {

    // Buscar ejercicios de rutina por ID de rutina
    List<RoutineExercise> findByWorkoutRoutineId(Long workoutRoutineId);

    // Buscar ejercicios de rutina por ID de ejercicio
    List<RoutineExercise> findByExerciseId(Long exerciseId);

    // Buscar ejercicio específico en una rutina
    @Query("SELECT re FROM RoutineExercise re WHERE re.workoutRoutine.id = :routineId AND re.exercise.id = :exerciseId")
    Optional<RoutineExercise> findByWorkoutRoutineIdAndExerciseId(@Param("routineId") Long routineId, @Param("exerciseId") Long exerciseId);

    // Buscar ejercicios de rutina por número de series
    List<RoutineExercise> findBySets(Integer sets);

    // Buscar ejercicios de rutina con series mayores a un valor
    List<RoutineExercise> findBySetsGreaterThan(Integer sets);

    // Buscar ejercicios de rutina por número de repeticiones
    List<RoutineExercise> findByReps(Integer reps);

    // Buscar ejercicios de rutina con repeticiones mayores a un valor
    List<RoutineExercise> findByRepsGreaterThan(Integer reps);

    // Buscar ejercicios de rutina por tiempo de descanso
    List<RoutineExercise> findByRestTime(Integer restTime);

    // Buscar ejercicios de rutina por músculo (a través del ejercicio)
    @Query("SELECT re FROM RoutineExercise re WHERE re.exercise.muscle = :muscle")
    List<RoutineExercise> findByExerciseMuscle(@Param("muscle") String muscle);

    // Buscar ejercicios de rutina por tipo de ejercicio
    @Query("SELECT re FROM RoutineExercise re WHERE re.exercise.type = :type")
    List<RoutineExercise> findByExerciseType(@Param("type") String type);

    // Contar ejercicios por rutina
    @Query("SELECT re.workoutRoutine.id, COUNT(re) FROM RoutineExercise re GROUP BY re.workoutRoutine.id")
    List<Object[]> countExercisesByRoutine();

    // Calcular el total de series por rutina
    @Query("SELECT re.workoutRoutine.id, SUM(re.sets) FROM RoutineExercise re GROUP BY re.workoutRoutine.id")
    List<Object[]> sumSetsByRoutine();

    // Buscar ejercicios de rutina con información completa (join fetch)
    @Query("SELECT re FROM RoutineExercise re JOIN FETCH re.workoutRoutine JOIN FETCH re.exercise WHERE re.workoutRoutine.id = :routineId")
    List<RoutineExercise> findByWorkoutRoutineIdWithDetails(@Param("routineId") Long routineId);

    // Verificar si existe un ejercicio en una rutina
    @Query("SELECT CASE WHEN COUNT(re) > 0 THEN true ELSE false END FROM RoutineExercise re WHERE re.workoutRoutine.id = :routineId AND re.exercise.id = :exerciseId")
    boolean existsByWorkoutRoutineIdAndExerciseId(@Param("routineId") Long routineId, @Param("exerciseId") Long exerciseId);

    // Eliminar todos los ejercicios de una rutina
    void deleteByWorkoutRoutineId(Long workoutRoutineId);

    // Eliminar un ejercicio específico de todas las rutinas
    void deleteByExerciseId(Long exerciseId);
}