package com.group6.api.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.group6.api.services.FileService;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {
	
	@Autowired
	private FileService fileService;
	
	
	@PostMapping("/uploadData/{parseFlag}")
	public ResponseEntity<String> uploadData(@RequestParam("file") MultipartFile file, @PathVariable String parseFlag) throws IOException, Exception {
		String response = null;			
		if (file == null) {
			response = "Please upload a valid file that contains data.";
			return new ResponseEntity<String>(response, HttpStatus.BAD_REQUEST);
		} else {
			response = fileService.processFile(file, parseFlag);
		}
		
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}
	

}
