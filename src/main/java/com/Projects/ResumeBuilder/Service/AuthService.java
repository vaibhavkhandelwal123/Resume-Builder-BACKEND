package com.Projects.ResumeBuilder.Service;

import com.Projects.ResumeBuilder.Dto.AuthResponse;
import com.Projects.ResumeBuilder.Dto.RegisterRequest;
import com.Projects.ResumeBuilder.Entity.User;
import com.Projects.ResumeBuilder.Exception.ResumeBuilderException;
import com.Projects.ResumeBuilder.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${app.base.url}")
    private String appBaseUrl;

    public AuthResponse register(RegisterRequest registerRequest){
        log.info("Inside AuthService: register(){}",registerRequest);
        if(userRepository.existsByEmail(registerRequest.getEmail())){
            throw new ResumeBuilderException("User already exists with this email");
        }
        User newUser = toDocument(registerRequest);
        userRepository.save(newUser);
        sendVerificationEmail(newUser);
        return toResponse(newUser);
    }

    private void sendVerificationEmail(User newUser) {
        log.info("Inside AuthService - sendVerificationEmail(): {}",newUser);
        try {
            String link = appBaseUrl+"/api/auth/verify-email?token="+newUser.getVerificationToken();
            String html = "<div style=\"max-width: 600px; margin: auto; background: #ffffff; padding: 25px; border-radius: 8px;\">\n" +
                    "\n" +
                    "   <h2 style=\"color: #333;\">Hello, "+newUser.getName()+"</h2>\n" +

                    "    <p style=\"font-size: 15px; color: #555;\">\n" +
                    "      Please verify your email address by clicking the link below:\n" +
                    "    </p>\n" +
                    "\n" +
                    "    <p style=\"margin: 25px 0;\">\n" +
                    "      <a href=\""+link+"\" \n" +
                    "         style=\"background: #4A90E2; color: white; padding: 12px 20px; text-decoration: none; border-radius: 6px; font-weight: bold;\">\n" +
                    "         Verify Email\n" +
                    "      </a>\n" +
                    "    </p>\n" +
                    "\n" +
                    "    <p style=\"font-size: 14px; color: #777;\">\n" +
                    "      Or copy and paste this link into your browser:\n" +
                    "    </p>\n" +
                    "\n" +
                    "    <p style=\"word-break: break-all; font-size: 14px;\">\n" +
                    "      <a href=\"${verificationLink}\" style=\"color: #4A90E2;\">"+link+"</a>\n" +
                    "    </p>\n" +
                    "\n" +
                    "    <p style=\"font-size: 13px; color: #aaa; margin-top: 30px;\">\n" +
                    "      If you did not request this, please ignore this email.\n" +
                    "    </p>\n" +
                    "\n" +
                    "  </div>";
            emailService.sendHtmlEmail(newUser.getEmail(),"Verify your email",html);
        }catch (Exception e){
            log.error("Exception occurred at sendVerificationEmail(): {}",e.getMessage());
            throw new RuntimeException("Failed to send verification email: "+e.getMessage());
        }
    }

    private AuthResponse toResponse(User newUser){
        return AuthResponse.builder()
                .id(newUser.getId())
                .name(newUser.getName())
                .email(newUser.getEmail())
                .profileImageUrl(newUser.getProfileImageUrl())
                .emailVerified(newUser.isEmailVerified())
                .subscriptionPlan(newUser.getSubscriptionPlan())
                .createdAt(newUser.getCreatedAt())
                .updatedAt(newUser.getUpdatedAt())
                .build();
    }

    private User toDocument(RegisterRequest newUser){
        return User.builder()
                .name(newUser.getName())
                .email(newUser.getEmail())
                .password(newUser.getPassword())
                .profileImageUrl(newUser.getProfileImageUrl())
                .subscriptionPlan("Basic")
                .emailVerified(false)
                .verificationToken(UUID.randomUUID().toString())
                .verificationExpires(LocalDateTime.now().plusHours(24))
                .build();
    }

    public void verifyEmail(String token){
        log.info("Inside AUthService: verifyEmail(): {}",token);
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new ResumeBuilderException("Invalid or expired verification token"));
        if(user.getVerificationExpires() != null && user.getVerificationExpires().isBefore(LocalDateTime.now())){
            throw new ResumeBuilderException("Verification token has expired. Please request new one");
        }
        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationExpires(null);
        userRepository.save(user);
    }

    public void deleteUserByEmail(String email) {
        boolean user = userRepository.existsByEmail(email);
        if(!user){
            throw new ResumeBuilderException("User doesn't exist");
        }
        userRepository.deleteByEmail(email);
    }
}
