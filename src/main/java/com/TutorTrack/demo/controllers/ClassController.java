package com.TutorTrack.demo.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/class")
public class ClassController {

    @GetMapping("/get")
    public ResponseEntity<?> fetchAllClasses(){
        return  new ResponseEntity<>("this is secured end point", HttpStatus.OK);

    }
}
