package com.example.demo.domains.users;

import com.example.demo.domains.Company;
import com.example.demo.domains.Studio;
import com.example.demo.security.user.JwtUser;
import com.example.demo.security.user.Position;
import com.example.demo.security.user.Role;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Owner extends JwtUser {

    {
        this.setRole(Set.of(Role.ROLE_USER,
                Role.ROLE_ADMIN,
                Role.ROLE_SUPER_ADMIN,
                Role.ROLE_OWNER));
        this.setPosition(Position.Owner);
    }

    public Owner(JwtUser jwtUser) {
        super(jwtUser);
    }


    @OneToOne
    @JsonIncludeProperties(value = {"name", "id"})
    Company company;

    @Transient
    public Set<Studio> getStudios () {
        return company.getStudios();
    }
}