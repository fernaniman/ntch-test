package org.example.Controller;

import org.example.Dto.ResponseDto;
import org.example.Dto.UserCreateDto;
import org.example.Entity.UserEntity;
import org.example.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {

    private Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/registration")
    public ResponseEntity<ResponseDto> register(@RequestBody UserCreateDto userCreateDTO) {
        return userService.registerUser(userCreateDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto> login(@RequestBody UserEntity userLoginDto) {
        return userService.login(userLoginDto);
    }

    @GetMapping("/profile")
    public ResponseEntity<ResponseDto> getProfile(HttpServletRequest request) {
        return userService.getProfile(request);
    }

    @PutMapping("/profile/update")
    public ResponseEntity<ResponseDto> updateProfile(HttpServletRequest request, @RequestBody UserEntity userDTO) {
        return userService.updateProfile(request, userDTO);
    }

    @PutMapping("/profile/image")
    public ResponseEntity<ResponseDto> updateProfileImage(HttpServletRequest request, @RequestParam("image") MultipartFile imageUrl) {
        try {
            return userService.updateProfileImage(request, imageUrl);
        } catch (IOException e) {
            log.info(e.getMessage());
        }
        return null;
    }
}

