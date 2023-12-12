package com.example.demo.security.controllers;

import com.example.demo.domains.Studio;
import com.example.demo.domains.users.Owner;
import com.example.demo.domains.users.Receptionist;
import com.example.demo.domains.users.RegistrationVoucherRepository;
import com.example.demo.domains.users.Student.Student;
import com.example.demo.domains.users.Teacher;
import com.example.demo.security.user.JwtUser;
import com.example.demo.security.user.JwtUserService;
import com.example.demo.security.user.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "false")
@RequestMapping("/user")
public class UserController {

    @Autowired
    JwtUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RegistrationVoucherRepository registrationVoucherRepository;


    // any
    @GetMapping("/get-myself")
    public JwtUser getMyself() {
        return userService.getCurrentUser();
    }

    // any
    @GetMapping("/roles")
    public String getHomePage(Authentication authentication) {
        return authentication.toString();
    }

    // any
    @GetMapping("/login")
    public String loginEndpoint() {
        return "!";
    }

    // statistics. super admin
    @GetMapping("/get-all")
    public List<JwtUser> getAllUsers(Authentication authentication) {
        return userService.findAll(authentication);
    }

    @GetMapping("/user/email/{email}")
    public JwtUser getUserByEmail(@PathVariable String email) {
        return userService.getByEmail(email);
    }

    // any authed, but blur date as email and phone number
    @GetMapping("{id}")
    public JwtUser getUserById(@PathVariable Long id) {
        var user = userService.getById(id);
        return user;
    }

    // create. admin
    @PostMapping("/create/student")
    public JwtUser createStudent(@RequestBody Student student) {
        return userService.create(student);
    }


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    static
    class CreateStudentRequest {
        private JwtUser user;
        private List<Studio> studios;
    }

    // update. any
    @PutMapping("/update-my-account")
    public JwtUser updateAccount(@RequestBody JwtUser jwtUser) {
        return userService.updateCurrentUser(jwtUser);
    }

    // update. ROLE_OWNER
    @PutMapping("/update-as-owner")
    public JwtUser updateUserAsOwner(@RequestBody JwtUser jwtUser, Authentication authentication) {
        return userService.updateUserAsOwner(authentication, jwtUser);
    }

    // update. ROLE_USER
    @PutMapping("/update")
    public JwtUser updateUser(@RequestBody JwtUser jwtUser) {
        return userService.updateUser(jwtUser);
    }

    @PutMapping("/update-password")
    public JwtUser updateUser(@RequestParam String oldPassword, @RequestParam String newPassword, @RequestParam Long userId) {
        var currentUser = userService.getCurrentUser();
        var userToChange = userService.getById(userId);
        if (userToChange.getId().equals(currentUser.getId())) {
            var doTheyMatch = passwordEncoder.matches(oldPassword, currentUser.getPassword());
            if (doTheyMatch) {
                currentUser.setPassword(passwordEncoder.encode(newPassword));
                return userService.save(currentUser);
            } else {
                throw new RuntimeException("Old password is incorrect");
            }

        } else if (currentUser.getRole().size() > userToChange.getRole().size()) {
            userToChange.setPassword(passwordEncoder.encode(newPassword));
            return userService.save(currentUser);
        } else throw new RuntimeException("Something went wrong. Please try again later");
    }

    // remove. super admin
    @DeleteMapping("/remove-user/{id}")
    public void deleteUser(@PathVariable Long id, Authentication authentication) {
        userService.deleteUser(id, authentication);
    }

    @PutMapping("/update/studios/{userId}")
    public void updateUserStudios(@PathVariable Long userId, @RequestBody List<Studio> studios) {
        var currentUser = userService.getCurrentUser();
        if (!currentUser.getRole().contains(Role.ROLE_SUPER_ADMIN)) throw new RuntimeException("Not permitted action");
        var requestedUser = userService.getById(userId);
        var studiosSet = new HashSet<>(studios);
        if (requestedUser instanceof Student student) {
            if (studios.size() > 1) {
                throw new RuntimeException("Student can have only 1 studio");
            } else {
                student.setStudio(studios.get(0));
                userService.save(student);
            }
        } else if (requestedUser instanceof Teacher teacher) {
            teacher.setStudios(studiosSet);
            userService.save(teacher);
        } else if (requestedUser instanceof Receptionist receptionist) {
            receptionist.setStudios(studiosSet);
            userService.save(receptionist);
        } else if (requestedUser instanceof Owner) {
            throw new RuntimeException("Not permitted action");
        }
    }


}
