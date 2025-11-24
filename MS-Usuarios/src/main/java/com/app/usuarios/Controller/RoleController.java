package com.app.usuarios.Controller;

import com.app.usuarios.Dto.RoleDto;
import com.app.usuarios.Dto.ServiceResult;
import com.app.usuarios.Model.Role;
import com.app.usuarios.Service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "API para la gestión de roles de usuario")
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "Obtener todos los roles", description = "Devuelve una lista de todos los roles disponibles en el sistema")
    @ApiResponse(responseCode = "200", description = "Operación exitosa", content = @Content(schema = @Schema(implementation = ServiceResult.class)))
    @GetMapping
    public ResponseEntity<ServiceResult<List<RoleDto>>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @Operation(summary = "Crear un nuevo rol", description = "Crea un rol con el nombre proporcionado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "El rol ya existe o datos inválidos")
    })
    @PostMapping
    public ResponseEntity<ServiceResult<Role>> createRole(@RequestBody RoleDto roleDto) {
        return ResponseEntity.ok(roleService.create(roleDto));
    }

    @Operation(summary = "Eliminar un rol", description = "Elimina un rol existente basado en su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol eliminado"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ServiceResult<String>> deleteRole(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.deleteById(id));
    }
}