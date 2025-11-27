package com.app.usuarios.Controller;

import com.app.usuarios.Dto.*;
import com.app.usuarios.Model.User;
import com.app.usuarios.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // CREATE
    @PostMapping
    public ResponseEntity<User> create(@RequestBody UserCreateDto userDto) {
        return ResponseEntity.ok(userService.create(userDto));
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    // READ BY USERNAME
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDto> findByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.findByUsername(username));
    }

    // READ BY EMAIL
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDto> findByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }

    // UPDATE FULL
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> update(@PathVariable Long id, @RequestBody UserUpdateDto userDto) {
        return ResponseEntity.ok(userService.update(id, userDto));
    }

    // UPDATE PROFILE ONLY
    @PatchMapping("/{id}/profile")
    public ResponseEntity<UserResponseDto> updateProfile(@PathVariable Long id, @RequestBody UserProfileUpdateDto profileDto) {
        return ResponseEntity.ok(userService.updateProfile(id, profileDto));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}