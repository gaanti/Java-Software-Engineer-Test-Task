package com.example.demo.domains.users;

import com.example.demo.domains.Studio;
import com.example.demo.security.user.JwtUser;
import com.example.demo.security.user.Position;
import com.example.demo.security.user.Role;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Receptionist extends JwtUser {

    {
        this.setRole(Set.of(Role.ROLE_USER,
                Role.ROLE_ADMIN,
                Role.ROLE_SUPER_ADMIN));
        this.setPosition(Position.Receptionist);
    }

    @ManyToMany
    @JsonIncludeProperties(value = {"studioName", "abbreviation", "lessonTypes", "lessonTimes"})
//    @JoinColumn(nullable = false)
    Set<Studio> studios;

    public Receptionist(JwtUser jwtUser) {
        super(jwtUser);
    }
}