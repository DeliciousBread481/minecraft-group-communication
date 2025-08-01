package com.github.konstantyn111.crashapi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {
    private final String uploadDir = "uploads/solution";
    private final String cdnBaseUrl = "http://cdn.hello/";

    public List<String> storeFiles(List<MultipartFile> files) {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            String fileName = generateUniqueFileName(file);
            Path filePath = Paths.get(uploadDir, fileName);

            try{
                Files.createDirectories(filePath.getParent());
                Files.copy(file.getInputStream(), filePath,
                        StandardCopyOption.REPLACE_EXISTING);
                urls.add(cdnBaseUrl + fileName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return urls;
    }

    private String generateUniqueFileName(MultipartFile file) {
        return UUID.randomUUID() + "." + getFileExtension(file);
    }

    private String getFileExtension(MultipartFile file) {
        String name = file.getOriginalFilename();
        if (name != null && name.contains(".")) {
            return name.substring(name.lastIndexOf(".") + 1);
        }
        return "bin";
    }
}