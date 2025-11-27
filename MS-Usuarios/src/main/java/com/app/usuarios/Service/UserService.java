package com.app.usuarios.Service;

import com.app.usuarios.Dto.*;
import com.app.usuarios.Model.Role;
import com.app.usuarios.Model.User;
import com.app.usuarios.Repository.RoleRepository;
import com.app.usuarios.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // CREATE - Crear usuario
    @Transactional
    public User create(UserCreateDto userDto) {
        // Validar que no exista usuario con mismo username o email
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("Username already exists: " + userDto.getUsername());
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email already exists: " + userDto.getEmail());
        }

        // Obtener rol por defecto
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role ROLE_USER not found"));

        // Construir usuario
        User user = User.builder()
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .displayName(userDto.getDisplayName())
                .phone(userDto.getPhone())
                .weight(userDto.getWeight())
                .height(userDto.getHeight())
                .photoUri(userDto.getPhotoUri())
                .dateOfBirth(userDto.getDateOfBirth())
                .gender(userDto.getGender())
                .fitnessGoal(userDto.getFitnessGoal())
                .experienceLevel(userDto.getExperienceLevel())
                .weeklyWorkouts(userDto.getWeeklyWorkouts())
                .workoutDuration(userDto.getWorkoutDuration())
                .preferredWorkoutTimes(userDto.getPreferredWorkoutTimes())
                .roles(Collections.singleton(userRole))
                .createdAt(Timestamp.from(Instant.now()))
                .enabled(true)
                .locked(false)
                .failedLoginAttempts(0)
                .build();

        return userRepository.save(user);
    }

    // READ - Obtener todos los usuarios
    public List<UserResponseDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // READ - Obtener usuario por ID
    public UserResponseDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return toDto(user);
    }

    // READ - Obtener usuario por username
    public UserResponseDto findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return toDto(user);
    }

    // READ - Obtener usuario por email
    public UserResponseDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return toDto(user);
    }

    // UPDATE - Actualizar usuario
    @Transactional
    public UserResponseDto update(Long id, UserUpdateDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Actualizar campos básicos
        Optional.ofNullable(userDto.getUsername()).ifPresent(user::setUsername);
        Optional.ofNullable(userDto.getEmail()).ifPresent(user::setEmail);

        // Actualizar campos del perfil
        Optional.ofNullable(userDto.getDisplayName()).ifPresent(user::setDisplayName);
        Optional.ofNullable(userDto.getPhone()).ifPresent(user::setPhone);
        Optional.ofNullable(userDto.getWeight()).ifPresent(user::setWeight);
        Optional.ofNullable(userDto.getHeight()).ifPresent(user::setHeight);
        Optional.ofNullable(userDto.getPhotoUri()).ifPresent(user::setPhotoUri);
        Optional.ofNullable(userDto.getDateOfBirth()).ifPresent(user::setDateOfBirth);
        Optional.ofNullable(userDto.getGender()).ifPresent(user::setGender);
        Optional.ofNullable(userDto.getFitnessGoal()).ifPresent(user::setFitnessGoal);
        Optional.ofNullable(userDto.getExperienceLevel()).ifPresent(user::setExperienceLevel);
        Optional.ofNullable(userDto.getWeeklyWorkouts()).ifPresent(user::setWeeklyWorkouts);
        Optional.ofNullable(userDto.getWorkoutDuration()).ifPresent(user::setWorkoutDuration);
        Optional.ofNullable(userDto.getPreferredWorkoutTimes()).ifPresent(user::setPreferredWorkoutTimes);

        // Actualizar timestamp
        user.setUpdatedAt(Timestamp.from(Instant.now()));

        User updatedUser = userRepository.save(user);
        return toDto(updatedUser);
    }

    // UPDATE - Actualizar campos específicos del perfil
    @Transactional
    public UserResponseDto updateProfile(Long id, UserProfileUpdateDto profileDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Actualizar solo campos del perfil
        Optional.ofNullable(profileDto.getDisplayName()).ifPresent(user::setDisplayName);
        Optional.ofNullable(profileDto.getPhone()).ifPresent(user::setPhone);
        Optional.ofNullable(profileDto.getWeight()).ifPresent(user::setWeight);
        Optional.ofNullable(profileDto.getHeight()).ifPresent(user::setHeight);
        Optional.ofNullable(profileDto.getPhotoUri()).ifPresent(user::setPhotoUri);
        Optional.ofNullable(profileDto.getDateOfBirth()).ifPresent(user::setDateOfBirth);
        Optional.ofNullable(profileDto.getGender()).ifPresent(user::setGender);
        Optional.ofNullable(profileDto.getFitnessGoal()).ifPresent(user::setFitnessGoal);
        Optional.ofNullable(profileDto.getExperienceLevel()).ifPresent(user::setExperienceLevel);
        Optional.ofNullable(profileDto.getWeeklyWorkouts()).ifPresent(user::setWeeklyWorkouts);
        Optional.ofNullable(profileDto.getWorkoutDuration()).ifPresent(user::setWorkoutDuration);
        Optional.ofNullable(profileDto.getPreferredWorkoutTimes()).ifPresent(user::setPreferredWorkoutTimes);

        user.setUpdatedAt(Timestamp.from(Instant.now()));

        User updatedUser = userRepository.save(user);
        return toDto(updatedUser);
    }

    // DELETE - Eliminar usuario
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    // Método auxiliar para convertir Entity a DTO
    private UserResponseDto toDto(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .phone(user.getPhone())
                .weight(user.getWeight())
                .height(user.getHeight())
                .photoUri(user.getPhotoUri())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .fitnessGoal(user.getFitnessGoal())
                .experienceLevel(user.getExperienceLevel())
                .weeklyWorkouts(user.getWeeklyWorkouts())
                .workoutDuration(user.getWorkoutDuration())
                .preferredWorkoutTimes(user.getPreferredWorkoutTimes())
                .roles(roleNames)
                .enabled(user.isEnabled())
                .locked(user.isLocked())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}