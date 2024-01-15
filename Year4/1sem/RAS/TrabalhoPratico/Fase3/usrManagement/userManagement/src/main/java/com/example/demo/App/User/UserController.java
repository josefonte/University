package com.example.demo.App.User;

import com.example.demo.App.JsonModels.LoginFormat;
import com.example.demo.App.JsonModels.UserInfoRequest;
import com.example.demo.App.JsonModels.UserInfoResponse;
import com.example.demo.App.JsonModels.UserVerificationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/usr")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping(path="/register")
    public ResponseEntity<Object> registerUsers(@RequestParam String userType, @RequestBody List<User> users){
        List<String> invalidUsers = this.userService.addUsers(userType, users);
        return invalidUsers.isEmpty()
                ? ResponseEntity.ok("Users successfully added")
                : ResponseEntity.badRequest()
                                .body("Some invalid users were not added\n" + invalidUsers);
    }

    @PostMapping(path="/login")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginFormat loginFormat){
        return this.userService.authenticateUser(loginFormat.getPassword(), loginFormat.getNumber())
                ? ResponseEntity.ok("User successfully authenticated")
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Credentials");
    }

    @GetMapping(path="/profile")
    public ResponseEntity<Object> getUserInfo(@RequestParam String number){
        UserInfoResponse userInfoResponse = this.userService.getUserInfo(number);
        return userInfoResponse != null
                ? ResponseEntity.ok(userInfoResponse)
                : ResponseEntity.badRequest().body("Requirements to change user not met");
    }

    @PostMapping(path="/profile")
    public ResponseEntity<String> changeUserInfo(@RequestBody UserInfoRequest user){
        return this.userService.changeUserInfo(user)
                ? ResponseEntity.ok("User settings changed successfully")
                : ResponseEntity.badRequest().body("Requirements to change user not met");
    }

    @PutMapping(path="/verify")
    public ResponseEntity<List<UserVerificationResponse>> verifyUsersNumbers(@RequestParam String info, @RequestBody List<String> usersInfos){
        return ResponseEntity.ok(this.userService.verifyUsers(info, usersInfos));
    }
}
