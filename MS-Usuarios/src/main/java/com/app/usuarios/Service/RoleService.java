package com.app.usuarios.Service;

import com.app.usuarios.Dto.RoleDto;
import com.app.usuarios.Dto.ServiceResult;
import com.app.usuarios.Model.Role;
import com.app.usuarios.Repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public ServiceResult<Role> create(RoleDto request) {
        try {
            Optional<Role> find = roleRepository.findByName(request.getName());
            if(find.isPresent()){
                // Corregido: Se debe retornar el error o lanzar la excepción,
                // antes solo se instanciaba sin hacer nada.
                return new ServiceResult<>(List.of("El rol ya está Registrado"));
            }

            // Se eliminó la lógica de búsqueda y asignación de permissions
            Role create = Role.builder()
                    .name(request.getName())
                    .build();

            Role saved = roleRepository.save(create);
            return new ServiceResult<>(saved);

        } catch (Exception e) {
            return new ServiceResult<>(List.of("An error occurred while creating the role: " + e.getMessage()));
        }
    }

    public ServiceResult<String> deleteById(Long id) {
        try {
            Optional<Role> role = roleRepository.findById(id);
            if (role.isEmpty()) {
                return new ServiceResult<>(List.of("Role not found with ID: " + id));
            }

            roleRepository.deleteById(id);
            return new ServiceResult<>("Role deleted successfully.");
        } catch (Exception e) {
            return new ServiceResult<>(List.of("Error deleting role: " + e.getMessage()));
        }
    }

    // Se eliminaron los métodos assignPermissionToRole y removePermissionFromRole

    public ServiceResult<List<RoleDto>> getAllRoles() {
        try {
            List<RoleDto> roles = roleRepository.findAll().stream()
                    .map(role -> RoleDto.builder()
                            .id(role.getId())
                            .name(role.getName())
                            // Se eliminó el mapeo de permissions
                            .build())
                    .collect(Collectors.toList());

            return new ServiceResult<>(roles);
        } catch (Exception e) {
            return new ServiceResult<>(List.of("Error retrieving roles: " + e.getMessage()));
        }
    }
}