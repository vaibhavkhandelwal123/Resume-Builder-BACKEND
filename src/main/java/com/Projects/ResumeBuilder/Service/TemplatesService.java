package com.Projects.ResumeBuilder.Service;

import com.Projects.ResumeBuilder.Dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.Projects.ResumeBuilder.Utilities.AppConstants.PREMIUM;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplatesService {

    private final AuthService authService;
    public Map<String,Object> getTemplates(Object principal){
        AuthResponse authResponse = authService.getProfile(principal);

        List<String> availableTemplates;

        Boolean isPremium = PREMIUM.equalsIgnoreCase(authResponse.getSubscriptionPlan());

        if(isPremium){
            availableTemplates = List.of("01","02","03");
        }else{
            availableTemplates = List.of("01");
        }

        Map<String,Object> restrictions = new HashMap<>();
        restrictions.put("availableTemplates" , availableTemplates);
        restrictions.put("allTemplates" , List.of("01","02","03"));
        restrictions.put("subscriptionPlan",authResponse.getSubscriptionPlan());
        restrictions.put("isPremium" , isPremium);

        return restrictions;

    }
}
