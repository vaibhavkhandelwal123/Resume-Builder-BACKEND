package com.Projects.ResumeBuilder.Service;

import com.Projects.ResumeBuilder.Dto.AuthResponse;
import com.Projects.ResumeBuilder.Dto.CreateResumeRequest;
import com.Projects.ResumeBuilder.Entity.Resume;
import com.Projects.ResumeBuilder.Repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final AuthService authService;

    public Resume createResume(CreateResumeRequest request, Object principalObject) {
        Resume newResume = new Resume();
        AuthResponse authResponse = authService.getProfile(principalObject);
        newResume.setUserId(authResponse.getId());
        newResume.setTitle(request.getTitle());
        setDefaultResumeData(newResume);
        return resumeRepository.save(newResume);
    }

    private void setDefaultResumeData(Resume newResume) {
        newResume.setProfileInfo(new Resume.ProfileInfo());
        newResume.setContactInfo(new Resume.ContactInfo());
        newResume.setWorkExperiences(new ArrayList<>());
        newResume.setEducations(new ArrayList<>());
        newResume.setSkills(new ArrayList<>());
        newResume.setProjects(new ArrayList<>());
        newResume.setCertifications(new ArrayList<>());
        newResume.setLanguages(new ArrayList<>());
        newResume.setInterest(new ArrayList<>());
    }

    public List<Resume> getAllResumes(Object principal) {
        AuthResponse authResponse = authService.getProfile(principal);
        return resumeRepository.findByUserIdOrderByUpdatedAtDesc(authResponse.getId());
    }

    public Resume getResumeById(String id, Object principal) {
        AuthResponse authResponse = authService.getProfile(principal);
        return resumeRepository.findByUserIdAndId(authResponse.getId(),id).orElseThrow(() -> new RuntimeException("Resume not found"));
    }

    public Resume updateResume(String id, Resume updatedResume, Object principal) {
        AuthResponse authResponse = authService.getProfile(principal);
        Resume oldResume = resumeRepository.findByUserIdAndId(authResponse.getId(),id).orElseThrow(()->new RuntimeException("Resume not found"));
        oldResume.setTitle(updatedResume.getTitle());
        oldResume.setProfileInfo(updatedResume.getProfileInfo());
        oldResume.setInterest(updatedResume.getInterest());
        oldResume.setEducations(updatedResume.getEducations());
        oldResume.setCertifications(updatedResume.getCertifications());
        oldResume.setWorkExperiences(updatedResume.getWorkExperiences());
        oldResume.setContactInfo(updatedResume.getContactInfo());
        oldResume.setProjects(updatedResume.getProjects());
        oldResume.setSkills(updatedResume.getSkills());
        oldResume.setLanguages(updatedResume.getLanguages());
        resumeRepository.save(oldResume);
        return oldResume;
    }

    public void deleteResume(String id, Object principal) {
        AuthResponse authResponse = authService.getProfile(principal);
        Resume resume = resumeRepository.findByUserIdAndId(authResponse.getId(),id).orElseThrow(()->new RuntimeException("Resume not found"));
        resumeRepository.delete(resume);
    }
}
