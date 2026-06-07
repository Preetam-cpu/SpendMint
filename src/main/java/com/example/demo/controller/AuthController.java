package com.example.demo.controller;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {
    private final UserRepository repo;
    public AuthController(UserRepository repo){
        this.repo=repo;

    }

    //Register
    @PostMapping("/register")
    public String register(@RequestBody User user){
        if(repo.findByEmail(user.getEmail())!=null){
            return "Email already exists!";

        }

        repo.save(user);
        return "User Registered Successfully!";
    }

    //LOGIN
    @PostMapping("/login")
    public String login(@RequestBody User user){
        User existingUser=repo.findByEmail(user.getEmail());
        if(existingUser==null){
            return "User not found!";
        }
        if(!existingUser.getPassword().equals(user.getPassword())){
            return "Wrong Password!";
        }

        return "Login Successful!";
    }
}
