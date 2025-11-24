package com.app.usuarios.Controller;

import com.app.usuarios.Dto.RegisterRequest;
import com.app.usuarios.Dto.ServiceResult;
import com.app.usuarios.Dto.UserDto;
import com.app.usuarios.Dto.UserResponseDto;
import com.app.usuarios.Model.User;
import com.app.usuarios.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "API para gestión de usuarios y actualización de perfiles")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Listar usuarios", description = "Obtiene la lista completa de usuarios registrados con sus roles")
    @GetMapping
    public ResponseEntity<ServiceResult<List<UserResponseDto>>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "409", description = "El usuario o email ya existen")
    })
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody RegisterRequest request) {
        // Nota: Idealmente deberías devolver un DTO o ServiceResult, pero mantengo tu firma original del servicio
        return ResponseEntity.ok(userService.createUser(request));
    }

    @Operation(
            summary = "Actualizar perfil de usuario",
            description = "Permite actualizar datos del perfil como: Nombre, Peso, Altura, Teléfono y Foto. Usado por la App Móvil."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno al actualizar")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ServiceResult<UserResponseDto>> updateUser(
            @Parameter(description = "ID del usuario a actualizar") @PathVariable Long id,
            @RequestBody UserDto userDto
    ) {
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema por su ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResult<String>> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }
}