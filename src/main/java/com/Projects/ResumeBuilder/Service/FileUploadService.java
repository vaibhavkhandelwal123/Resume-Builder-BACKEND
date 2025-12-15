package com.Projects.ResumeBuilder.Service;

import com.Projects.ResumeBuilder.Dto.AuthResponse;
import com.Projects.ResumeBuilder.Entity.Resume;
import com.Projects.ResumeBuilder.Repository.ResumeRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final Cloudinary cloudinary;
    private final AuthService authService;
    private final ResumeRepository resumeRepository;

    public Map<String,String> uploadSingleImage(MultipartFile file) throws IOException {
        Map<String,Object> imageUploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type","image"));
        log.info("Inside FileUploadService - uploadSingleImage() {}",imageUploadResult.get("secure_url").toString());
        return Map.of("imageUrl", imageUploadResult.get("secure_url").toString());
    }

    public Map<String, String> uploadResumeImages(String id, Object principal, MultipartFile thumbnail, MultipartFile profileImage) throws IOException {
        AuthResponse authResponse = authService.getProfile(principal);
        Resume existingResume = resumeRepository.findByUserIdAndId(authResponse.getId(),id).orElseThrow(()->new RuntimeException("Resume not found"));

        Map<String,String> val = new HashMap<>();
        Map<String,String> result;
        if(Objects.nonNull(thumbnail)){
            result = uploadSingleImage(thumbnail);
            existingResume.setThumbnailLink(result.get("imageUrl"));
            val.put("thumbnailLink",result.get("imageUrl"));
        }
        if(Objects.nonNull(profileImage)){
            result = uploadSingleImage(profileImage);
            if(Objects.isNull(existingResume.getProfileInfo())){
                existingResume.setProfileInfo(new Resume.ProfileInfo());
            }
            existingResume.getProfileInfo().setProfilePreviewUrl(result.get("imageUrl"));
            val.put("profilePreviewUrl",result.get("imageUrl"));
        }
        resumeRepository.save(existingResume);
        val.put("message","Image uploaded successfully");
        return val;
    }
}
