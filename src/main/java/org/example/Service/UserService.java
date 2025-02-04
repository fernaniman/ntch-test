package org.example.Service;

import org.example.Dto.ResponseDto;
import org.example.Dto.UserCreateDto;
import org.example.Entity.UserBalanceEntity;
import org.example.Entity.UserEntity;
import org.example.Repository.UserRepository;
import org.example.Security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private Validator validator;

    private Logger log = LoggerFactory.getLogger(UserService.class);

    public ResponseEntity<ResponseDto> registerUser(UserCreateDto userCreateDTO) {
        ResponseDto response = new ResponseDto();

        var violations = validator.validate(userCreateDTO);
        if (!violations.isEmpty()) {
            List<Map<String, Object>> violationHeaderList = new ArrayList<>();
            List<String> validateHeader = new ArrayList<>();
            for (var violation : violations) {
                log.error(violation.getMessage());
                Map<String, Object> data = new HashMap<>();
                validateHeader.add(violation.getMessage());
                data.put(violation.getPropertyPath().toString(), violation.getMessage());
                violationHeaderList.add(data);
            }

            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(validateHeader.get(0));
            response.setData(violationHeaderList);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (userRepository.findByUsername(userCreateDTO.getUsername()) != null) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Username already exists.");
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String encodedPassword = passwordEncoder.encode(userCreateDTO.getPassword());

        UserEntity user = new UserEntity();
        user.setUsername(userCreateDTO.getUsername());
        user.setPassword(encodedPassword);
        user.setEmail(userCreateDTO.getEmail());
        user.setFullname(userCreateDTO.getFullname());
        user.setCreatedAt(new Date());
        userRepository.save(user);

        UserBalanceEntity balance = new UserBalanceEntity();
        balance.setUser(user);
        balance.setBalance(BigDecimal.ZERO);
        userRepository.saveBalance(balance);

        response.setStatus(HttpStatus.CREATED.value());
        response.setMessage("User created successfully.");
        response.setData(user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    public ResponseEntity<ResponseDto> login(UserEntity userLoginDto) {
        ResponseDto response = new ResponseDto();

        var violations = validator.validate(userLoginDto);
        if (!violations.isEmpty()) {
            List<Map<String, Object>> violationHeaderList = new ArrayList<>();
            List<String> validateHeader = new ArrayList<>();
            for (var violation : violations) {
                log.error(violation.getMessage());
                Map<String, Object> data = new HashMap<>();
                validateHeader.add(violation.getMessage());
                data.put(violation.getPropertyPath().toString(), violation.getMessage());
                violationHeaderList.add(data);
            }

            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage(validateHeader.get(0));
            response.setData(violationHeaderList);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        UserEntity user = userRepository.findByEmail(userLoginDto.getEmail());
        if (user == null || !passwordEncoder.matches(userLoginDto.getPassword(), user.getPassword())) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Invalid email or password.");
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String token = jwtTokenProvider.generateToken(user.getUsername(), user.getId());
        LinkedHashMap<String, String> tokenMap = new LinkedHashMap<>();
        tokenMap.put("token", token);

        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Login successful.");
        response.setData(tokenMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<ResponseDto> getProfile(HttpServletRequest request) {
        ResponseDto response = new ResponseDto();

        String username = jwtTokenProvider.getUsernameFromToken(request);
        UserEntity user = userRepository.findByUsername(username);

        if (user == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage("User not found.");
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        response.setStatus(HttpStatus.OK.value());
        response.setMessage("User profile retrieved successfully.");
        response.setData(user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<ResponseDto> updateProfile(HttpServletRequest request, UserEntity userDTO) {
        ResponseDto response = new ResponseDto();

        String username = jwtTokenProvider.getUsernameFromToken(request);
        UserEntity user = userRepository.findByUsername(username);

        if (user == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage("User not found.");
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        user.setFullname(userDTO.getFullname());
        userRepository.update(user);

        response.setStatus(HttpStatus.OK.value());
        response.setMessage("User profile updated successfully.");
        response.setData(user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<ResponseDto> updateProfileImage(HttpServletRequest request, MultipartFile file) throws IOException {
        ResponseDto response = new ResponseDto();

        String username = jwtTokenProvider.getUsernameFromToken(request);
        UserEntity user = userRepository.findByUsername(username);

        if (user == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            response.setMessage("User not found.");
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        String fileName = file.getOriginalFilename();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        if (!(fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png"))) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Invalid file type. Only JPG, JPEG, and PNG files are allowed.");
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (file.getSize() > 2 * 1024 * 1024) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setMessage("File size exceeds the limit of 2 MB.");
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String uploadDir = "uploads";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdir();
        }

        String filePath = uploadDir + "/" + username + "_" + System.currentTimeMillis() + "." + fileExtension;
        Path path = Paths.get(filePath);
        Files.write(path, file.getBytes());

        user.setProfilePic(filePath);
        userRepository.updatePicture(user);

        response.setStatus(HttpStatus.OK.value());
        response.setMessage("Profile image updated successfully.");
        response.setData(user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
