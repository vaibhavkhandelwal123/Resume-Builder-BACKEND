package com.Projects.ResumeBuilder.Controller;

import com.Projects.ResumeBuilder.Dto.AuthResponse;
import com.Projects.ResumeBuilder.Dto.LoginRequest;
import com.Projects.ResumeBuilder.Dto.RegisterRequest;
import com.Projects.ResumeBuilder.Entity.User;
import com.Projects.ResumeBuilder.Service.AuthService;
import com.Projects.ResumeBuilder.Service.FileUploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static com.Projects.ResumeBuilder.Utilities.AppConstants.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(AUTH_CONTROLLER)
public class AuthController {

    @Autowired
    private AuthService authService;
    private final FileUploadService fileUploadService;
    @PostMapping(REGISTER)
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request){
        log.info("Inside AuthController - register(): {}",request);
        AuthResponse response = authService.register(request);
        log.info("Response from service: {}",response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(VERIFY_EMAIL)
    public ResponseEntity<?> verifyEmail(@RequestParam String token){
        log.info("Inside AuthController - verifyEmail(): {}",token);
        authService.verifyEmail(token);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message","Email verified successfully"));
    }

    @DeleteMapping(DELETE)
    public ResponseEntity<String> deleteUser(@PathVariable String email){
        log.info("Inside AuthController - deleteUser(): {}",email);
        authService.deleteUserByEmail(email);
        return new ResponseEntity<>("User is Deleted",HttpStatus.OK);
    }

    @PostMapping(UPLOAD_IMAGE)
    public ResponseEntity<?> uploadImage(@RequestPart("image")MultipartFile file) throws IOException {
        log.info("Inside AUthController - uploadImage()");
        Map<String,String> response = fileUploadService.uploadSingleImage(file);
        return ResponseEntity.ok(response);
    }

    @PostMapping(LOGIN)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest){
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping(RESEND_VERIFICATION)
    public ResponseEntity<?> resendVerification(@RequestBody Map<String,String> body){
        String email = body.get("email");
        if(Objects.isNull(email)){
            return ResponseEntity.badRequest().body(Map.of("message","Email is required"));
        }
        authService.resendVerification(email);
        return ResponseEntity.ok(Map.of("success",true,"message","verification email sent"));
    }

    @GetMapping(PROFILE)
    public ResponseEntity<?> getProfile(Authentication authentication){
        Object principalObject = authentication.getPrincipal();
        AuthResponse currentProfile = authService.getProfile(principalObject);
        return ResponseEntity.ok(currentProfile);

    }
}
