package com.example.demo.security.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "uuid")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIgnoreProperties(value = {"authorities", "uuid", "token"})
public class JwtUser implements UserDetails, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Builder.Default
    @JsonIgnore
    private String uuid = UUID.randomUUID().toString();

    @Column(name = "username", nullable = false, unique = true)
    @JsonProperty("username")
    private String username;

    private String firstName;

    private String lastName;

    private String address;
    private String state;
    private String zipCode;
    private String language;
    private String phoneNumber;

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @Column(updatable = false)
    private Set<Role> role = new TreeSet<>(Set.of(Role.ROLE_USER));

    @Enumerated(EnumType.STRING)
    @Column(name = "position", columnDefinition = "enum ('Owner', 'Receptionist' 'Teacher', 'Student');", nullable = false, updatable = false)
    private Position position;

    @DateTimeFormat
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @Column
    @Builder.Default
    @JsonIgnore
    private boolean enabled = true;


    @CreationTimestamp
    @Column(updatable = false)
    Timestamp registered;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (var r : this.role) {
            var sga = new SimpleGrantedAuthority(r.name());
            authorities.add(sga);
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }


    public JwtUser(JwtUser jwtUser) {
        this.username = jwtUser.getUsername();
        this.firstName = jwtUser.getFirstName();
        this.lastName = jwtUser.getLastName();
        this.email = jwtUser.getEmail();
        this.password = jwtUser.getPassword();
        this.role = jwtUser.getRole();
        this.position = jwtUser.getPosition();
        this.birthday = jwtUser.getBirthday();
        this.enabled = jwtUser.enabled;
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    public JwtUser(Long id) {
        this.id = id;
    }

    public void setField(String fieldName, Object value) {
        try {
            Class<?> currentClass = this.getClass();
            Field field = null;

            // Loop through the class hierarchy to find the field
            while (currentClass != null && field == null) {
                try {
                    field = currentClass.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                    // Field not found in the current class, try the parent class
                    currentClass = currentClass.getSuperclass();
                }
            }

            if (field != null) {
                field.setAccessible(true);
                field.set(this, value);
            } else {
                // Field not found in the entire hierarchy
                throw new NoSuchFieldException(fieldName);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JwtUser jwtUser)) return false;

        if (isEnabled() != jwtUser.isEnabled()) return false;
        if (getId() != null ? !getId().equals(jwtUser.getId()) : jwtUser.getId() != null) return false;
        if (getUsername() != null ? !getUsername().equals(jwtUser.getUsername()) : jwtUser.getUsername() != null)
            return false;
        if (getFirstName() != null ? !getFirstName().equals(jwtUser.getFirstName()) : jwtUser.getFirstName() != null)
            return false;
        if (getLastName() != null ? !getLastName().equals(jwtUser.getLastName()) : jwtUser.getLastName() != null)
            return false;
        if (getEmail() != null ? !getEmail().equals(jwtUser.getEmail()) : jwtUser.getEmail() != null) return false;
        if (getPassword() != null ? !getPassword().equals(jwtUser.getPassword()) : jwtUser.getPassword() != null)
            return false;
        if (getRole() != null ? !getRole().equals(jwtUser.getRole()) : jwtUser.getRole() != null) return false;
        if (getPosition() != jwtUser.getPosition()) return false;
        if (getBirthday() != null ? !getBirthday().equals(jwtUser.getBirthday()) : jwtUser.getBirthday() != null)
            return false;
        return getRegistered() != null ? getRegistered().equals(jwtUser.getRegistered()) : jwtUser.getRegistered() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getUsername() != null ? getUsername().hashCode() : 0);
        result = 31 * result + (getFirstName() != null ? getFirstName().hashCode() : 0);
        result = 31 * result + (getLastName() != null ? getLastName().hashCode() : 0);
        result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
        result = 31 * result + (getPassword() != null ? getPassword().hashCode() : 0);
        result = 31 * result + (getRole() != null ? getRole().hashCode() : 0);
        result = 31 * result + (getPosition() != null ? getPosition().hashCode() : 0);
        result = 31 * result + (getBirthday() != null ? getBirthday().hashCode() : 0);
        result = 31 * result + (isEnabled() ? 1 : 0);
        result = 31 * result + (getRegistered() != null ? getRegistered().hashCode() : 0);
        return result;
    }
}
