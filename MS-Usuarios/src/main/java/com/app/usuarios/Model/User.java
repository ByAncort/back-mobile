package com.app.usuarios.Model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp; // Corregido para base de datos
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    // Credenciales de acceso
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    private String password;

    // --- Nuevos campos para el Perfil (coinciden con Android UserProfile) ---

    @Column(name = "display_name")
    private String displayName; // El campo "Name" del formulario

    @Column(name = "phone")
    private String phone;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "height")
    private Integer height;

    @Column(name = "photo_url")
    private String photoUrl; // Corresponde a photoUri

    // -----------------------------------------------------------------------

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

    @Override
    public boolean isAccountNonLocked() { return !locked; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return List.of(); }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }
}