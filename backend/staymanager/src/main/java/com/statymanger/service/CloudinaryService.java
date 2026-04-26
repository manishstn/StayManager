package com.statymanger.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.statymanger.exception.FileStorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * Uploads a file and returns both URL and Public ID.
     */
    public Map<String, String> uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // "resource_type" -> "auto" detects if it's an image or a PDF
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto"));

            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("url", uploadResult.get("secure_url").toString());
            resultMap.put("publicId", uploadResult.get("public_id").toString());

            return resultMap;

        } catch (IOException e) {
            // Throwing a specific explanatory exception
            throw new FileStorageException("Cloudinary upload failed for file: "
                    + file.getOriginalFilename() + ". Error: " + e.getMessage());
        }
    }

    /**
     * Deletes a file from Cloudinary using its Public ID.
     */
    public void deleteFile(String publicId) {
        if (publicId == null || publicId.isBlank()) return;

        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new FileStorageException("Cloudinary deletion failed for ID: "
                    + publicId + ". Error: " + e.getMessage());
        }
    }
}