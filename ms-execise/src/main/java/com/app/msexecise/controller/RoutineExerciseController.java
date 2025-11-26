package com.app.msexecise.controller;

import com.app.msexecise.controller.dto.*;
import com.app.msexecise.domain.model.Exercise;
import com.app.msexecise.domain.model.RoutineExercise;
import com.app.msexecise.domain.model.WorkoutRoutine;
import com.app.msexecise.domain.service.ExerciseService;
import com.app.msexecise.domain.service.RoutineExerciseService;
import com.app.msexecise.domain.service.WorkoutRoutineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/routine-exercises")
@RequiredArgsConstructor
@Validated
@Tag(name = "Routine Exercises", description = "Routine Exercise management APIs")
public class RoutineExerciseController {

    private final RoutineExerciseService routineExerciseService;
    private final ExerciseService exerciseService;
    private final WorkoutRoutineService workoutRoutineService;
    private final ModelMapper modelMapper;

    @PostMapping
    @Operation(summary = "Create a new routine exercise", description = "Creates a new routine exercise linking an exercise to a workout routine")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Routine exercise created successfully",
                    content = @Content(schema = @Schema(implementation = RoutineExerciseResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Exercise or workout routine not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<RoutineExerciseResponseDTO> createRoutineExercise(
            @Valid @RequestBody RoutineExerciseRequestDTO requestDTO) {
        try {
            RoutineExercise routineExercise = convertToEntity(requestDTO);
            RoutineExercise createdRoutineExercise = routineExerciseService.createRoutineExercise(routineExercise);
            RoutineExerciseResponseDTO response = convertToDTO(createdRoutineExercise);
                return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/routine-exercises"
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    @Operation(summary = "Get all routine exercises", description = "Retrieves a list of all routine exercises")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved routine exercises")
    public ResponseEntity<List<RoutineExerciseResponseDTO>> getAllRoutineExercises() {
        List<RoutineExercise> routineExercises = routineExerciseService.getAllRoutineExercises();
        List<RoutineExerciseResponseDTO> response = routineExercises.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get routine exercise by ID", description = "Retrieves a specific routine exercise by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Routine exercise found",
                    content = @Content(schema = @Schema(implementation = RoutineExerciseResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Routine exercise not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<RoutineExerciseResponseDTO> getRoutineExerciseById(
            @Parameter(description = "ID of the routine exercise to be retrieved")
            @PathVariable @NotNull(message = "ID cannot be null")
            @Positive(message = "ID must be positive") Long id) {
        try {
            return routineExerciseService.getRoutineExerciseById(id)
                    .map(this::convertToDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/routine-exercises/" + id
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/workout-routine/{workoutRoutineId}")
    @Operation(summary = "Get routine exercises by workout routine", description = "Retrieves all routine exercises for a specific workout routine")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved routine exercises"),
            @ApiResponse(responseCode = "400", description = "Invalid workout routine ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<RoutineExerciseResponseDTO>> getRoutineExercisesByWorkoutRoutine(
            @Parameter(description = "Workout routine ID to filter by")
            @PathVariable @NotNull(message = "Workout routine ID cannot be null")
            @Positive(message = "Workout routine ID must be positive") Long workoutRoutineId) {
        try {
            List<RoutineExercise> routineExercises = routineExerciseService.getRoutineExercisesByWorkoutRoutine(workoutRoutineId);
            List<RoutineExerciseResponseDTO> response = routineExercises.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/routine-exercises/workout-routine/" + workoutRoutineId
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/exercise/{exerciseId}")
    @Operation(summary = "Get routine exercises by exercise", description = "Retrieves all routine exercises for a specific exercise")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved routine exercises"),
            @ApiResponse(responseCode = "400", description = "Invalid exercise ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<RoutineExerciseResponseDTO>> getRoutineExercisesByExercise(
            @Parameter(description = "Exercise ID to filter by")
            @PathVariable @NotNull(message = "Exercise ID cannot be null")
            @Positive(message = "Exercise ID must be positive") Long exerciseId) {
        try {
            List<RoutineExercise> routineExercises = routineExerciseService.getRoutineExercisesByExercise(exerciseId);
            List<RoutineExerciseResponseDTO> response = routineExercises.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/routine-exercises/exercise/" + exerciseId
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a routine exercise", description = "Updates an existing routine exercise with the provided details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Routine exercise updated successfully",
                    content = @Content(schema = @Schema(implementation = RoutineExerciseResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Routine exercise not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<RoutineExerciseResponseDTO> updateRoutineExercise(
            @Parameter(description = "ID of the routine exercise to be updated")
            @PathVariable @NotNull(message = "ID cannot be null")
            @Positive(message = "ID must be positive") Long id,
            @Valid @RequestBody RoutineExerciseRequestDTO exerciseDetails) {
        try {
            RoutineExercise routineExercise = convertToEntity(exerciseDetails);
            RoutineExercise updatedRoutineExercise = routineExerciseService.updateRoutineExercise(id, routineExercise);
            RoutineExerciseResponseDTO response = convertToDTO(updatedRoutineExercise);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/routine-exercises/" + id
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    e.getMessage(),
                    "/api/v1/routine-exercises/" + id
            );
            return new ResponseEntity(error, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a routine exercise", description = "Deletes a specific routine exercise by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Routine exercise deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Routine exercise not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> deleteRoutineExercise(
            @Parameter(description = "ID of the routine exercise to be deleted")
            @PathVariable @NotNull(message = "ID cannot be null")
            @Positive(message = "ID must be positive") Long id) {
        try {
            routineExerciseService.deleteRoutineExercise(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/routine-exercises/" + id
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    e.getMessage(),
                    "/api/v1/routine-exercises/" + id
            );
            return new ResponseEntity(error, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}/exists")
    @Operation(summary = "Check if routine exercise exists", description = "Checks if a routine exercise exists by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Existence check completed"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Boolean> routineExerciseExists(
            @Parameter(description = "ID of the routine exercise to check")
            @PathVariable @NotNull(message = "ID cannot be null")
            @Positive(message = "ID must be positive") Long id) {
        try {
            boolean exists = routineExerciseService.existsById(id);
            return ResponseEntity.ok(exists);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/routine-exercises/" + id + "/exists"
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        }
    }

    // Métodos de conversión
    private RoutineExercise convertToEntity(RoutineExerciseRequestDTO dto) {
        RoutineExercise routineExercise = new RoutineExercise();

        // Buscar y asignar el ejercicio
        Exercise exercise = exerciseService.getExerciseById(dto.getExerciseId())
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found with id: " + dto.getExerciseId()));

        // Buscar y asignar la rutina de ejercicio
        WorkoutRoutine workoutRoutine = workoutRoutineService.getWorkoutRoutineById(dto.getWorkoutRoutineId())
                .orElseThrow(() -> new IllegalArgumentException("Workout routine not found with id: " + dto.getWorkoutRoutineId()));

        routineExercise.setExercise(exercise);
        routineExercise.setWorkoutRoutine(workoutRoutine);
        routineExercise.setSets(dto.getSets());
        routineExercise.setReps(dto.getReps());
        routineExercise.setRestTime(dto.getRestTime());

        return routineExercise;
    }

    private RoutineExerciseResponseDTO convertToDTO(RoutineExercise routineExercise) {
        RoutineExerciseResponseDTO dto = new RoutineExerciseResponseDTO();
        dto.setId(routineExercise.getId());
        dto.setSets(routineExercise.getSets());
        dto.setReps(routineExercise.getReps());
        dto.setRestTime(routineExercise.getRestTime());

        // Convertir Exercise a DTO
        if (routineExercise.getExercise() != null) {
            ExerciseResponseDTO exerciseDTO = modelMapper.map(routineExercise.getExercise(), ExerciseResponseDTO.class);
            dto.setExercise(exerciseDTO);
        }

        // Convertir WorkoutRoutine a DTO simple
        if (routineExercise.getWorkoutRoutine() != null) {
            WorkoutRoutineSimpleDTO routineDTO = modelMapper.map(routineExercise.getWorkoutRoutine(), WorkoutRoutineSimpleDTO.class);
            dto.setWorkoutRoutine(routineDTO);
        }

        return dto;
    }
}