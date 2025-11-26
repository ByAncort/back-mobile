package com.app.msexecise.domain.repository;

import com.app.msexecise.domain.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    // Buscar ejercicio por nombre exacto (case insensitive)
    Optional<Exercise> findByName(String name);

    // Buscar ejercicios que contengan el nombre (case insensitive)
    List<Exercise> findByNameContainingIgnoreCase(String name);

    // Buscar ejercicios por grupo muscular
    List<Exercise> findByMuscle(String muscle);

    // Buscar ejercicios por tipo
    List<Exercise> findByType(String type);

    // Buscar ejercicios por dificultad
    List<Exercise> findByDifficulty(String difficulty);

    // Buscar ejercicios por equipo
    List<Exercise> findByEquipment(String equipment);

    // Buscar ejercicios por grupo muscular y tipo
    List<Exercise> findByMuscleAndType(String muscle, String type);

    // Buscar ejercicios por grupo muscular y dificultad
    List<Exercise> findByMuscleAndDifficulty(String muscle, String difficulty);

    // Buscar ejercicios por múltiples grupos musculares
    @Query("SELECT e FROM Exercise e WHERE e.muscle IN :muscles")
    List<Exercise> findByMusclesIn(@Param("muscles") List<String> muscles);

    // Buscar ejercicios por tipo y dificultad
    List<Exercise> findByTypeAndDifficulty(String type, String difficulty);

    // Contar ejercicios por grupo muscular
    @Query("SELECT e.muscle, COUNT(e) FROM Exercise e GROUP BY e.muscle")
    List<Object[]> countExercisesByMuscleGroup();

    // Verificar si existe un ejercicio con el mismo nombre (excluyendo un ID específico)
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Exercise e WHERE e.name = :name AND e.id != :id")
    boolean existsByNameAndIdNot(@Param("name") String name, @Param("id") Long id);

    // Buscar ejercicios con paginación (se puede combinar con Pageable)
    // Page<Exercise> findByNameContainingIgnoreCase(String name, Pageable pageable);
}