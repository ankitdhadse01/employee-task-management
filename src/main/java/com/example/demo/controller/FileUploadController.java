package com.example.demo.controller;

import com.example.demo.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:3001", "http://localhost:3002",
		"http://localhost:3003", "http://localhost:3004" })
public class FileUploadController {

	@Autowired
	private FileStorageService fileStorageService;

	@PostMapping("/upload")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
		try {
			String fileName = fileStorageService.storeFile(file);
			return ResponseEntity.ok(fileName);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Could not upload file: " + e.getMessage());
		}
	}

	@PostMapping("/uploadMultiple")
	public ResponseEntity<?> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
		try {
			List<String> fileNames = Arrays.stream(files).map(file -> {
				try {
					return fileStorageService.storeFile(file);
				} catch (IOException e) {
					throw new RuntimeException("Failed to upload file: " + e.getMessage());
				}
			}).collect(Collectors.toList());
			return ResponseEntity.ok(fileNames);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Could not upload files: " + e.getMessage());
		}
	}
}