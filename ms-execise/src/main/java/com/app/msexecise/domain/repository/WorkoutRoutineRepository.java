package com.app.msexecise.domain.repository;

import com.app.msexecise.domain.model.WorkoutRoutine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutRoutineRepository extends JpaRepository<WorkoutRoutine, Long> {

    // Buscar rutina por nombre exacto
    Optional<WorkoutRoutine> findByName(String name);

    // Buscar rutinas que contengan el nombre (case insensitive)
    List<WorkoutRoutine> findByNameContainingIgnoreCase(String name);

    // Buscar rutinas por descripción (case insensitive)
    List<WorkoutRoutine> findByDescriptionContainingIgnoreCase(String description);

    // Buscar rutinas por duración
    List<WorkoutRoutine> findByDuration(String duration);

    // Buscar rutinas que contengan un ejercicio específico
    @Query("SELECT wr FROM WorkoutRoutine wr JOIN wr.exercises re WHERE re.exercise.id = :exerciseId")
    List<WorkoutRoutine> findByExercisesExerciseId(@Param("exerciseId") Long exerciseId);

    // Buscar rutinas por músculo objetivo (a través de los ejercicios)
    @Query("SELECT DISTINCT wr FROM WorkoutRoutine wr JOIN wr.exercises re WHERE re.exercise.muscle = :muscle")
    List<WorkoutRoutine> findByExercisesExerciseMuscle(@Param("muscle") String muscle);

    // Buscar rutinas por tipo de ejercicio
    @Query("SELECT DISTINCT wr FROM WorkoutRoutine wr JOIN wr.exercises re WHERE re.exercise.type = :type")
    List<WorkoutRoutine> findByExercisesExerciseType(@Param("type") String type);

    // Buscar rutinas creadas después de una fecha
    List<WorkoutRoutine> findByCreatedAtAfter(java.time.LocalDateTime date);

    // Buscar rutinas actualizadas después de una fecha
    List<WorkoutRoutine> findByUpdatedAtAfter(java.time.LocalDateTime date);

    // Contar rutinas por duración
    @Query("SELECT wr.duration, COUNT(wr) FROM WorkoutRoutine wr GROUP BY wr.duration")
    List<Object[]> countRoutinesByDuration();

    // Buscar rutinas con número de ejercicios
    @Query("SELECT wr, SIZE(wr.exercises) as exerciseCount FROM WorkoutRoutine wr")
    List<Object[]> findRoutinesWithExerciseCount();

    // Buscar rutinas con más de X ejercicios
    @Query("SELECT wr FROM WorkoutRoutine wr WHERE SIZE(wr.exercises) > :minExercises")
    List<WorkoutRoutine> findRoutinesWithMoreThanXExercises(@Param("minExercises") int minExercises);

    // Verificar si existe una rutina con el mismo nombre (excluyendo un ID específico)
    @Query("SELECT CASE WHEN COUNT(wr) > 0 THEN true ELSE false END FROM WorkoutRoutine wr WHERE wr.name = :name AND wr.id != :id")
    boolean existsByNameAndIdNot(@Param("name") String name, @Param("id") Long id);
    Optional<WorkoutRoutine> findByNameAndUsername(String name, String username);
    List<WorkoutRoutine> findByUsername(String username);
    List<WorkoutRoutine> findByNameContainingIgnoreCaseAndUsername(String name, String username);
    Optional<WorkoutRoutine> findByIdAndUsername(Long id, String username);
    boolean existsByIdAndUsername(Long id, String username);

}