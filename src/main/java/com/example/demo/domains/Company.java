package com.example.demo.domains;

import com.example.demo.BaseEntity;
import com.example.demo.domains.users.Owner;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company extends BaseEntity {
    @OneToOne(cascade = CascadeType.ALL)
    @JsonIncludeProperties(value = {"firstName", "lastName", "id"})
    Owner owner;

    @OneToMany(mappedBy = "company")
    @JsonIncludeProperties(value = {"studioName", "id"})
    Set<Studio> studios;

    String name;
    String logoKey;
    String preferredColor;
}
