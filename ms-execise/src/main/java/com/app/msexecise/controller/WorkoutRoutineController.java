package com.app.msexecise.controller;

import com.app.msexecise.controller.dto.*;
import com.app.msexecise.domain.model.RoutineExercise;
import com.app.msexecise.domain.model.WorkoutRoutine;
import com.app.msexecise.domain.service.WorkoutRoutineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
@RequestMapping("/api/v1/workout-routines")
@RequiredArgsConstructor
@Validated
@Tag(name = "Workout Routines", description = "Workout Routine management APIs")
public class WorkoutRoutineController {

    private final WorkoutRoutineService workoutRoutineService;
    private final ModelMapper modelMapper;

    @PostConstruct
    public void configureModelMapper() {
        // Configurar mapeo específico para evitar problemas con colecciones Hibernate
        modelMapper.createTypeMap(WorkoutRoutine.class, WorkoutRoutineResponseDTO.class)
                .addMappings(mapper -> {
                    mapper.skip(WorkoutRoutineResponseDTO::setExercises);
                });
    }

    @PostMapping
    @Operation(summary = "Create a new workout routine", description = "Creates a new workout routine with the provided details")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Workout routine created successfully",
                    content = @Content(schema = @Schema(implementation = WorkoutRoutineResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Workout routine with this name already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<WorkoutRoutineResponseDTO> createWorkoutRoutine(
            @Valid @RequestBody WorkoutRoutineRequestDTO workoutRoutineRequest) {
        try {
            WorkoutRoutine workoutRoutine = convertToEntity(workoutRoutineRequest);
            WorkoutRoutine createdRoutine = workoutRoutineService.createWorkoutRoutine(workoutRoutine);
            WorkoutRoutineResponseDTO response = convertToDTO(createdRoutine);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/workout-routines"
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    @Operation(summary = "Get all workout routines", description = "Retrieves a list of all workout routines")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved workout routines")
    public ResponseEntity<List<WorkoutRoutineResponseDTO>> getAllWorkoutRoutines() {
        List<WorkoutRoutine> workoutRoutines = workoutRoutineService.getAllWorkoutRoutines();
        List<WorkoutRoutineResponseDTO> response = workoutRoutines.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get workout routine by ID", description = "Retrieves a specific workout routine by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Workout routine found",
                    content = @Content(schema = @Schema(implementation = WorkoutRoutineResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Workout routine not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<WorkoutRoutineResponseDTO> getWorkoutRoutineById(
            @Parameter(description = "ID of the workout routine to be retrieved")
            @PathVariable @NotNull(message = "ID cannot be null")
            @Positive(message = "ID must be positive") Long id) {
        try {
            return workoutRoutineService.getWorkoutRoutineById(id)
                    .map(this::convertToDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/workout-routines/" + id
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search workout routines by name", description = "Searches workout routines by name (case insensitive)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved workout routines"),
            @ApiResponse(responseCode = "400", description = "Invalid search term",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<WorkoutRoutineResponseDTO>> getWorkoutRoutinesByName(
            @Parameter(description = "Name to search for")
            @RequestParam @NotBlank(message = "Name cannot be blank") String name) {
        try {
            List<WorkoutRoutine> workoutRoutines = workoutRoutineService.getWorkoutRoutinesByName(name);
            List<WorkoutRoutineResponseDTO> response = workoutRoutines.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/workout-routines/search?name=" + name
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a workout routine", description = "Updates an existing workout routine with the provided details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Workout routine updated successfully",
                    content = @Content(schema = @Schema(implementation = WorkoutRoutineResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Workout routine not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Workout routine with this name already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<WorkoutRoutineResponseDTO> updateWorkoutRoutine(
            @Parameter(description = "ID of the workout routine to be updated")
            @PathVariable @NotNull(message = "ID cannot be null")
            @Positive(message = "ID must be positive") Long id,
            @Valid @RequestBody WorkoutRoutineRequestDTO routineDetails) {
        try {
            WorkoutRoutine workoutRoutine = convertToEntity(routineDetails);
            WorkoutRoutine updatedRoutine = workoutRoutineService.updateWorkoutRoutine(id, workoutRoutine);
            WorkoutRoutineResponseDTO response = convertToDTO(updatedRoutine);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/workout-routines/" + id
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    e.getMessage(),
                    "/api/v1/workout-routines/" + id
            );
            return new ResponseEntity(error, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a workout routine", description = "Deletes a specific workout routine by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Workout routine deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Workout routine not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> deleteWorkoutRoutine(
            @Parameter(description = "ID of the workout routine to be deleted")
            @PathVariable @NotNull(message = "ID cannot be null")
            @Positive(message = "ID must be positive") Long id) {
        try {
            workoutRoutineService.deleteWorkoutRoutine(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/workout-routines/" + id
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    e.getMessage(),
                    "/api/v1/workout-routines/" + id
            );
            return new ResponseEntity(error, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{routineId}/exercises")
    @Operation(summary = "Add exercise to workout routine", description = "Adds an exercise to a specific workout routine")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exercise added successfully",
                    content = @Content(schema = @Schema(implementation = WorkoutRoutineResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Workout routine or exercise not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Exercise is already in the workout routine",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<WorkoutRoutineResponseDTO> addExerciseToRoutine(
            @Parameter(description = "ID of the workout routine")
            @PathVariable @NotNull(message = "Routine ID cannot be null")
            @Positive(message = "Routine ID must be positive") Long routineId,
            @Valid @RequestBody AddExerciseToRoutineRequestDTO request) {
        try {
            WorkoutRoutine updatedRoutine = workoutRoutineService.addExerciseToRoutine(
                    routineId,
                    request.getExerciseId(),
                    request.getSets(),
                    request.getReps(),
                    request.getRestTime()
            );
            WorkoutRoutineResponseDTO response = convertToDTO(updatedRoutine);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/workout-routines/" + routineId + "/exercises"
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    e.getMessage(),
                    "/api/v1/workout-routines/" + routineId + "/exercises"
            );
            return new ResponseEntity(error, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{routineId}/exercises/{exerciseId}")
    @Operation(summary = "Remove exercise from workout routine", description = "Removes an exercise from a specific workout routine")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exercise removed successfully",
                    content = @Content(schema = @Schema(implementation = WorkoutRoutineResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid IDs",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Workout routine or exercise not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<WorkoutRoutineResponseDTO> removeExerciseFromRoutine(
            @Parameter(description = "ID of the workout routine")
            @PathVariable @NotNull(message = "Routine ID cannot be null")
            @Positive(message = "Routine ID must be positive") Long routineId,
            @Parameter(description = "ID of the exercise to remove")
            @PathVariable @NotNull(message = "Exercise ID cannot be null")
            @Positive(message = "Exercise ID must be positive") Long exerciseId) {
        try {
            WorkoutRoutine updatedRoutine = workoutRoutineService.removeExerciseFromRoutine(routineId, exerciseId);
            WorkoutRoutineResponseDTO response = convertToDTO(updatedRoutine);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/workout-routines/" + routineId + "/exercises/" + exerciseId
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    e.getMessage(),
                    "/api/v1/workout-routines/" + routineId + "/exercises/" + exerciseId
            );
            return new ResponseEntity(error, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{routineId}/exercises/{exerciseId}")
    @Operation(summary = "Update exercise in workout routine", description = "Updates an exercise's details in a specific workout routine")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exercise updated successfully",
                    content = @Content(schema = @Schema(implementation = WorkoutRoutineResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Workout routine or exercise not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<WorkoutRoutineResponseDTO> updateExerciseInRoutine(
            @Parameter(description = "ID of the workout routine")
            @PathVariable @NotNull(message = "Routine ID cannot be null")
            @Positive(message = "Routine ID must be positive") Long routineId,
            @Parameter(description = "ID of the exercise to update")
            @PathVariable @NotNull(message = "Exercise ID cannot be null")
            @Positive(message = "Exercise ID must be positive") Long exerciseId,
            @Valid @RequestBody UpdateExerciseInRoutineRequestDTO request) {
        try {
            WorkoutRoutine updatedRoutine = workoutRoutineService.updateExerciseInRoutine(
                    routineId,
                    exerciseId,
                    request.getSets(),
                    request.getReps(),
                    request.getRestTime()
            );
            WorkoutRoutineResponseDTO response = convertToDTO(updatedRoutine);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/workout-routines/" + routineId + "/exercises/" + exerciseId
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    e.getMessage(),
                    "/api/v1/workout-routines/" + routineId + "/exercises/" + exerciseId
            );
            return new ResponseEntity(error, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}/exists")
    @Operation(summary = "Check if workout routine exists", description = "Checks if a workout routine exists by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Existence check completed"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Boolean> workoutRoutineExists(
            @Parameter(description = "ID of the workout routine to check")
            @PathVariable @NotNull(message = "ID cannot be null")
            @Positive(message = "ID must be positive") Long id) {
        try {
            boolean exists = workoutRoutineService.existsById(id);
            return ResponseEntity.ok(exists);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/workout-routines/" + id + "/exists"
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/exercise/{exerciseId}")
    @Operation(summary = "Get workout routines by exercise", description = "Retrieves all workout routines that contain a specific exercise")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved workout routines"),
            @ApiResponse(responseCode = "400", description = "Invalid exercise ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<WorkoutRoutineResponseDTO>> getWorkoutRoutinesByExercise(
            @Parameter(description = "Exercise ID to filter by")
            @PathVariable @NotNull(message = "Exercise ID cannot be null")
            @Positive(message = "Exercise ID must be positive") Long exerciseId) {
        try {
            List<WorkoutRoutine> workoutRoutines = workoutRoutineService.getWorkoutRoutinesByExercise(exerciseId);
            List<WorkoutRoutineResponseDTO> response = workoutRoutines.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/workout-routines/exercise/" + exerciseId
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user/{username}")
    @Operation(summary = "Get workout routines by username", description = "Retrieves all workout routines for a specific user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved workout routines",
                    content = @Content(schema = @Schema(implementation = WorkoutRoutineResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid username",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<WorkoutRoutineResponseDTO>> getWorkoutRoutinesByUsername(
            @Parameter(description = "Username to filter by")
            @PathVariable @NotBlank(message = "Username cannot be blank") String username) {
        try {
            List<WorkoutRoutine> workoutRoutines = workoutRoutineService.getWorkoutRoutinesByUsername(username);
            List<WorkoutRoutineResponseDTO> response = workoutRoutines.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/workout-routines/user/" + username
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/search/user")
    @Operation(summary = "Search workout routines by name and username", description = "Searches workout routines by name for a specific user (case insensitive)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved workout routines"),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<WorkoutRoutineResponseDTO>> getWorkoutRoutinesByNameAndUsername(
            @Parameter(description = "Name to search for")
            @RequestParam @NotBlank(message = "Name cannot be blank") String name,
            @Parameter(description = "Username to filter by")
            @RequestParam @NotBlank(message = "Username cannot be blank") String username) {
        try {
            List<WorkoutRoutine> workoutRoutines = workoutRoutineService.getWorkoutRoutinesByNameAndUsername(name, username);
            List<WorkoutRoutineResponseDTO> response = workoutRoutines.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/workout-routines/search/user?name=" + name + "&username=" + username
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}/user/{username}")
    @Operation(summary = "Get workout routine by ID and username", description = "Retrieves a specific workout routine by its ID and username (for security validation)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Workout routine found",
                    content = @Content(schema = @Schema(implementation = WorkoutRoutineResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID or username",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Workout routine not found for this user",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<WorkoutRoutineResponseDTO> getWorkoutRoutineByIdAndUsername(
            @Parameter(description = "ID of the workout routine to be retrieved")
            @PathVariable @NotNull(message = "ID cannot be null")
            @Positive(message = "ID must be positive") Long id,
            @Parameter(description = "Username of the routine owner")
            @PathVariable @NotBlank(message = "Username cannot be blank") String username) {
        try {
            return workoutRoutineService.getWorkoutRoutineById(id)
                    .filter(routine -> routine.getUsername().equals(username))
                    .map(this::convertToDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/workout-routines/" + id + "/user/" + username
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}/user/{username}")
    @Operation(summary = "Delete workout routine by ID and username", description = "Deletes a specific workout routine by its ID and username (secure deletion)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Workout routine deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID or username",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Workout routine not found for this user",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> deleteWorkoutRoutineByIdAndUsername(
            @Parameter(description = "ID of the workout routine to be deleted")
            @PathVariable @NotNull(message = "ID cannot be null")
            @Positive(message = "ID must be positive") Long id,
            @Parameter(description = "Username of the routine owner")
            @PathVariable @NotBlank(message = "Username cannot be blank") String username) {
        try {
            workoutRoutineService.deleteWorkoutRoutineByIdAndUsername(id, username);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/workout-routines/" + id + "/user/" + username
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    e.getMessage(),
                    "/api/v1/workout-routines/" + id + "/user/" + username
            );
            return new ResponseEntity(error, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}/owner/{username}")
    @Operation(summary = "Check if user is owner of workout routine", description = "Checks if a specific user is the owner of a workout routine")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ownership check completed"),
            @ApiResponse(responseCode = "400", description = "Invalid ID or username",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Boolean> isWorkoutRoutineOwner(
            @Parameter(description = "ID of the workout routine to check")
            @PathVariable @NotNull(message = "ID cannot be null")
            @Positive(message = "ID must be positive") Long id,
            @Parameter(description = "Username to check ownership")
            @PathVariable @NotBlank(message = "Username cannot be blank") String username) {
        try {
            boolean isOwner = workoutRoutineService.isOwner(id, username);
            return ResponseEntity.ok(isOwner);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/workout-routines/" + id + "/owner/" + username
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        }
    }

    // Métodos de conversión
    private WorkoutRoutine convertToEntity(WorkoutRoutineRequestDTO dto) {
        return modelMapper.map(dto, WorkoutRoutine.class);
    }

    private WorkoutRoutineResponseDTO convertToDTO(WorkoutRoutine workoutRoutine) {
        // Usar ModelMapper para campos básicos, excluyendo ejercicios
        WorkoutRoutineResponseDTO dto = modelMapper.map(workoutRoutine, WorkoutRoutineResponseDTO.class);

        // Convertir ejercicios manualmente para evitar problemas con PersistentBag
        if (workoutRoutine.getExercises() != null) {
            List<RoutineExerciseResponseDTO> exerciseDTOs = workoutRoutine.getExercises().stream()
                    .map(this::convertRoutineExerciseToDTO)
                    .collect(Collectors.toList());
            dto.setExercises(exerciseDTOs);
        }

        return dto;
    }

    private RoutineExerciseResponseDTO convertRoutineExerciseToDTO(RoutineExercise routineExercise) {
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

        return dto;
    }
}