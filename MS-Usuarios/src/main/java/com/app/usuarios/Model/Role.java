package com.app.usuarios.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

}
