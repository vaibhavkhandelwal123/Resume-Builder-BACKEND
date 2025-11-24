package com.Projects.ResumeBuilder.Controller;

import com.Projects.ResumeBuilder.Dto.AuthResponse;
import com.Projects.ResumeBuilder.Dto.RegisterRequest;
import com.Projects.ResumeBuilder.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.Projects.ResumeBuilder.Utilities.AppConstants.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(AUTH_CONTROLLER)
public class AuthController {

    @Autowired
    private AuthService authService;

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

    @DeleteMapping("/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable String email){
        log.info("Inside AuthController - deleteUser(): {}",email);
        authService.deleteUserByEmail(email);
        return new ResponseEntity<>("User is Deleted",HttpStatus.OK);
    }
}
