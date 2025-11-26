package com.app.msexecise.controller;

import com.app.msexecise.controller.dto.ErrorResponseDTO;
import com.app.msexecise.controller.dto.ExerciseRequestDTO;
import com.app.msexecise.controller.dto.ExerciseResponseDTO;
import com.app.msexecise.domain.model.Exercise;
import com.app.msexecise.domain.service.ExerciseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/v1/exercises")
@RequiredArgsConstructor
@Validated
@Tag(name = "Exercises", description = "Exercise management APIs")
public class ExerciseController {

    private final ExerciseService exerciseService;
    private final ModelMapper modelMapper;

    @PostMapping
    @Operation(summary = "Create a new exercise", description = "Creates a new exercise with the provided details")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Exercise created successfully",
                    content = @Content(schema = @Schema(implementation = ExerciseResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Exercise with this name already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ExerciseResponseDTO> createExercise(@Valid @RequestBody ExerciseRequestDTO exerciseRequest) {
        try {
            Exercise exercise = convertToEntity(exerciseRequest);
            Exercise createdExercise = exerciseService.createExercise(exercise);
            ExerciseResponseDTO response = convertToDTO(createdExercise);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/exercises"
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    @Operation(summary = "Get all exercises", description = "Retrieves a list of all exercises")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved exercises")
    public ResponseEntity<List<ExerciseResponseDTO>> getAllExercises() {
        List<Exercise> exercises = exerciseService.getAllExercises();
        List<ExerciseResponseDTO> response = exercises.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get exercise by ID", description = "Retrieves a specific exercise by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exercise found",
                    content = @Content(schema = @Schema(implementation = ExerciseResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Exercise not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ExerciseResponseDTO> getExerciseById(
            @Parameter(description = "ID of the exercise to be retrieved")
            @PathVariable @NotNull(message = "ID cannot be null")
            @Positive(message = "ID must be positive") Long id) {
        try {
            return exerciseService.getExerciseById(id)
                    .map(this::convertToDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/exercises/" + id
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/muscle/{muscle}")
    @Operation(summary = "Get exercises by muscle group", description = "Retrieves exercises targeting a specific muscle group")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved exercises"),
            @ApiResponse(responseCode = "400", description = "Invalid muscle group",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<ExerciseResponseDTO>> getExercisesByMuscleGroup(
            @Parameter(description = "Muscle group to filter by")
            @PathVariable @NotBlank(message = "Muscle group cannot be blank") String muscle) {
        try {
            List<Exercise> exercises = exerciseService.getExercisesByMuscleGroup(muscle);
            List<ExerciseResponseDTO> response = exercises.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/exercises/muscle/" + muscle
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get exercises by type", description = "Retrieves exercises of a specific type")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved exercises"),
            @ApiResponse(responseCode = "400", description = "Invalid exercise type",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<ExerciseResponseDTO>> getExercisesByType(
            @Parameter(description = "Exercise type to filter by")
            @PathVariable @NotBlank(message = "Type cannot be blank") String type) {
        try {
            List<Exercise> exercises = exerciseService.getExercisesByType(type);
            List<ExerciseResponseDTO> response = exercises.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/exercises/type/" + type
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/difficulty/{difficulty}")
    @Operation(summary = "Get exercises by difficulty", description = "Retrieves exercises of a specific difficulty level")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved exercises"),
            @ApiResponse(responseCode = "400", description = "Invalid difficulty level",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<ExerciseResponseDTO>> getExercisesByDifficulty(
            @Parameter(description = "Difficulty level to filter by")
            @PathVariable @NotBlank(message = "Difficulty cannot be blank") String difficulty) {
        try {
            List<Exercise> exercises = exerciseService.getExercisesByDifficulty(difficulty);
            List<ExerciseResponseDTO> response = exercises.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/exercises/difficulty/" + difficulty
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search exercises by name", description = "Searches exercises by name (case insensitive)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved exercises"),
            @ApiResponse(responseCode = "400", description = "Invalid search term",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<ExerciseResponseDTO>> searchExercisesByName(
            @Parameter(description = "Name to search for")
            @RequestParam @NotBlank(message = "Search term cannot be blank") String name) {
        try {
            List<Exercise> exercises = exerciseService.searchExercisesByName(name);
            List<ExerciseResponseDTO> response = exercises.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/exercises/search?name=" + name
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an exercise", description = "Updates an existing exercise with the provided details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exercise updated successfully",
                    content = @Content(schema = @Schema(implementation = ExerciseResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Exercise not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Exercise with this name already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ExerciseResponseDTO> updateExercise(
            @Parameter(description = "ID of the exercise to be updated")
            @PathVariable @NotNull(message = "ID cannot be null")
            @Positive(message = "ID must be positive") Long id,
            @Valid @RequestBody ExerciseRequestDTO exerciseDetails) {
        try {
            Exercise exercise = convertToEntity(exerciseDetails);
            Exercise updatedExercise = exerciseService.updateExercise(id, exercise);
            ExerciseResponseDTO response = convertToDTO(updatedExercise);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/exercises/" + id
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    e.getMessage(),
                    "/api/v1/exercises/" + id
            );
            return new ResponseEntity(error, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an exercise", description = "Deletes a specific exercise by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Exercise deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Exercise not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "409", description = "Exercise cannot be deleted because it's used in routines",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> deleteExercise(
            @Parameter(description = "ID of the exercise to be deleted")
            @PathVariable @NotNull(message = "ID cannot be null")
            @Positive(message = "ID must be positive") Long id) {
        try {
            exerciseService.deleteExercise(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/exercises/" + id
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.NOT_FOUND.value(),
                    "Not Found",
                    e.getMessage(),
                    "/api/v1/exercises/" + id
            );
            return new ResponseEntity(error, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}/exists")
    @Operation(summary = "Check if exercise exists", description = "Checks if an exercise exists by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Existence check completed"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Boolean> exerciseExists(
            @Parameter(description = "ID of the exercise to check")
            @PathVariable @NotNull(message = "ID cannot be null")
            @Positive(message = "ID must be positive") Long id) {
        try {
            boolean exists = exerciseService.existsById(id);
            return ResponseEntity.ok(exists);
        } catch (IllegalArgumentException e) {
            ErrorResponseDTO error = new ErrorResponseDTO(
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad Request",
                    e.getMessage(),
                    "/api/v1/exercises/" + id + "/exists"
            );
            return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
        }
    }

    // Métodos de conversión
    private Exercise convertToEntity(ExerciseRequestDTO dto) {
        return modelMapper.map(dto, Exercise.class);
    }

    private ExerciseResponseDTO convertToDTO(Exercise exercise) {
        return modelMapper.map(exercise, ExerciseResponseDTO.class);
    }
}