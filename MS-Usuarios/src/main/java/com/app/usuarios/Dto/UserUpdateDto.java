// UserUpdateDto.java
package com.app.usuarios.Dto;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class UserUpdateDto {
    private String username;
    private String email;
    private String displayName;
    private String phone;
    private Double weight;
    private Integer height;
    private String photoUri;
    private Timestamp dateOfBirth;
    private String gender;
    private String fitnessGoal;
    private String experienceLevel;
    private Integer weeklyWorkouts;
    private Integer workoutDuration;
    private String preferredWorkoutTimes;
}