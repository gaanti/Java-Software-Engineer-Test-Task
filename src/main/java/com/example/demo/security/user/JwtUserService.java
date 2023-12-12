package com.example.demo.security.user;

import com.example.demo.domains.Studio;
import com.example.demo.domains.users.Owner;
import com.example.demo.domains.users.Receptionist;
import com.example.demo.domains.users.Student.Student;
import com.example.demo.domains.users.Teacher;
import com.example.demo.repositories.*;
import com.example.demo.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class JwtUserService {
    private final JwtUserRepository jwtUserRepository;

    @Autowired
    StudentService studentService;

    @Autowired
    StudioRepository studioRepository;

    @Autowired
    ReceptionistRepository receptionistRepository;

    @Autowired
    OwnerRepository ownerRepository;

    private final PasswordEncoder passwordEncoder;


    public <T extends JwtUser> T save(T user) {
        try {
            return jwtUserRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends JwtUser> T create(T user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return jwtUserRepository.save(user);
    }

    public JwtUser getById(Long id) {
        return jwtUserRepository.findById(id).orElseThrow();
    }

    public JwtUser getByEmail(String email) {
        return jwtUserRepository.findJwtUserByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public JwtUser getByUsername(String username) {
        return jwtUserRepository.findJwtUserByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public JwtUser getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var user = getByUsername(authentication.getPrincipal().toString());
        return user;
    }

    public List<JwtUser> findAll(Authentication authentication) {
        JwtUser authedUser = getByUsername(authentication.getPrincipal().toString());
        if (!authedUser.getPosition().equals(Position.Owner)) {
            throw new RuntimeException("Not authed user");
        }
        return jwtUserRepository.findJwtUserByPositionIsNot(Position.Owner);
    }

    public JwtUser updateCurrentUser(JwtUser jwtUser) {
        if (!getCurrentUser().getId().equals(jwtUser.getId())) {
            throw new RuntimeException("You cannot update another user");
        }
        var userToChange = jwtUserRepository.findById(jwtUser.getId()).orElseThrow();
        userToChange.setFirstName(jwtUser.getFirstName());
        userToChange.setLastName(jwtUser.getLastName());
        userToChange.setPhoneNumber(jwtUser.getPhoneNumber());
        userToChange.setAddress(jwtUser.getAddress());
        userToChange.setState(jwtUser.getState());
        userToChange.setZipCode(jwtUser.getZipCode());
        return save(userToChange);
    }

    public JwtUser updateUser(JwtUser jwtUser) {
        JwtUser authedUser = getCurrentUser();
        var userToChange = jwtUserRepository.findById(jwtUser.getId()).orElseThrow();
        if (authedUser.getRole().size() < jwtUser.getRole().size() && (!authedUser.getId().equals(jwtUser))) {
            throw new RuntimeException("You cannot update another user");
        } else {
            userToChange.setBirthday(jwtUser.getBirthday());
            userToChange.setLastName(jwtUser.getLastName());
            userToChange.setFirstName(jwtUser.getFirstName());
            userToChange.setLastName(jwtUser.getLastName());
            return save(userToChange);
        }
    }

    public JwtUser updateUserAsOwner(Authentication authentication, JwtUser jwtUser) {
        var currentUser = getCurrentUser();
        if (!currentUser.getPosition().equals(Position.Owner)) {
            throw new RuntimeException("No right for it");
        }
        var user = jwtUserRepository.findById(jwtUser.getId()).orElseThrow();
        user.setFirstName(jwtUser.getFirstName());
        user.setLastName(jwtUser.getLastName());
        user.setUsername(jwtUser.getUsername());
        user.setEmail(jwtUser.getEmail());
        user.setPhoneNumber(jwtUser.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(jwtUser.getPassword()));
        return jwtUserRepository.save(user);
    }

    public void deleteUser(Long id, Authentication authentication) {
        JwtUser authedUser = getByUsername(authentication.getPrincipal().toString());
        if (!authedUser.getPosition().equals(Position.Owner)) {
            throw new RuntimeException("No right for it");
        }
        jwtUserRepository.deleteById(id);
    }


    public JwtUser createUser(JwtUser jwtUser, List<Studio> studios) {
        var studiosSet = new HashSet<>(studios);
        jwtUser.setPassword(passwordEncoder.encode(jwtUser.getPassword()));
        jwtUser.setId(null);
        if (jwtUser.getPosition().equals(Position.Student)) {
            var student = new Student(jwtUser);
            student.setStudio(studios.get(0));
            return save(student);
        } else if (jwtUser.getPosition().equals(Position.Teacher)) {
            var teacher = new Teacher(jwtUser);
            teacher.setStudios(studiosSet);
            return save(teacher);
        } else if (jwtUser.getPosition().equals(Position.Receptionist)) {
            var receptionist = new Receptionist(jwtUser);
            receptionist.setStudios(studiosSet);
            return save(receptionist);
        } else if (jwtUser.getPosition().equals(Position.Owner)) {
            var owner = new Owner(jwtUser);
            var currentUser = getCurrentUser();
            if (currentUser instanceof Owner currentOwner) {
                owner.setCompany(currentOwner.getCompany());
            }
            return save(owner);
        } else {
            // Handle other cases if needed
            throw new IllegalArgumentException("Unsupported user type");
        }
    }

}
