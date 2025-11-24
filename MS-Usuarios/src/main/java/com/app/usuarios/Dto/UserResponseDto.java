package com.app.usuarios.Dto;

import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
public class UserResponseDto {
    private Long id;
    private String username;
    private String displayName;
    private String email;
    private String phone;
    private Double weight;
    private Integer height;
    private String photoUrl;
    private Set<String> roles;
}