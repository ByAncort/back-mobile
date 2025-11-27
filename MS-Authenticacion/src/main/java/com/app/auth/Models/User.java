package com.app.auth.Models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @CreatedDate
    @Column(updatable = false)
    private Timestamp createdAt;

    @LastModifiedDate
    private Timestamp updatedAt;

    private boolean enabled = true;
    private boolean locked = false;
    private int failedLoginAttempts = 0;

    // ========== NUEVOS CAMPOS PARA EL PERFIL ==========

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "height")
    private Integer height;

    @Column(name = "photo_uri")
    private String photoUri;

    @Column(name = "date_of_birth")
    private Timestamp dateOfBirth;

    @Column(name = "gender")
    private String gender; // "male", "female", "other"

    @Column(name = "fitness_goal")
    private String fitnessGoal; // "weight_loss", "muscle_gain", "maintenance", "endurance"

    @Column(name = "experience_level")
    private String experienceLevel; // "beginner", "intermediate", "advanced"

    @Column(name = "weekly_workouts")
    private Integer weeklyWorkouts;

    @Column(name = "workout_duration")
    private Integer workoutDuration; // en minutos

    @Column(name = "preferred_workout_times")
    private String preferredWorkoutTimes; // "morning", "afternoon", "evening"

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}