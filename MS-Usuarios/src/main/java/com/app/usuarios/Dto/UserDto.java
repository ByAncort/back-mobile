package com.app.usuarios.Dto;

import lombok.Data;
import java.util.Set;

@Data
public class UserDto {
    private String username; // Puede ser opcional si usas displayName
    private String displayName;
    private String email;
    private String phone;
    private Double weight;
    private Integer height;
    private String photoUrl;
    private Set<String> roles; // Nombres de roles
}