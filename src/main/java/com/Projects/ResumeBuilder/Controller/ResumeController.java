package com.Projects.ResumeBuilder.Controller;

import com.Projects.ResumeBuilder.Dto.CreateResumeRequest;
import com.Projects.ResumeBuilder.Entity.Resume;
import com.Projects.ResumeBuilder.Service.FileUploadService;
import com.Projects.ResumeBuilder.Service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.Projects.ResumeBuilder.Utilities.AppConstants.*;

@RestController
@RequestMapping(RESUME)
@RequiredArgsConstructor
@Slf4j
public class ResumeController {

    private final ResumeService resumeService;
    private final FileUploadService fileUploadService;
    @PostMapping
    public ResponseEntity<?> createResume(@Valid @RequestBody CreateResumeRequest request, Authentication authentication){
        Resume newResume = resumeService.createResume(request,authentication.getPrincipal());
        return ResponseEntity.status(HttpStatus.CREATED).body(newResume);
    }

    @GetMapping
    public ResponseEntity<?> getUserResumes(Authentication authentication){
        List<Resume> resumes = resumeService.getAllResumes(authentication.getPrincipal());
        return ResponseEntity.ok(resumes);
    }

    @GetMapping(ID)
    public ResponseEntity<?> getResumeById(@PathVariable String id,Authentication authentication){
        Resume resume= resumeService.getResumeById(id,authentication.getPrincipal());
        return ResponseEntity.ok(resume);
    }

    @PutMapping(ID)
    public ResponseEntity<?> updateResume(@PathVariable String id, @RequestBody Resume updatedResume,Authentication authentication){
        Resume resume = resumeService.updateResume(id,updatedResume,authentication.getPrincipal());
        return ResponseEntity.ok(resume);
    }

    @PutMapping(UPLOAD_IMAGES)
    public ResponseEntity<?> updateResumeImages(@PathVariable String id,
                                                @RequestPart(value = "thumbnail" , required = false)MultipartFile thumbnail,
                                                @RequestPart(value = "profileImage" , required = false)MultipartFile profileImage,
                                                Authentication authentication) throws IOException {
        Map<String,String> response = fileUploadService.uploadResumeImages(id,authentication.getPrincipal(),thumbnail,profileImage);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(ID)
    public ResponseEntity<?> deleteResume(@PathVariable String id,Authentication authentication){
        resumeService.deleteResume(id,authentication.getPrincipal());
        return new ResponseEntity<>("Resume is deleted" , HttpStatus.OK);
    }
}
