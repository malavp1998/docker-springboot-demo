package com.leaning.docket_k8s.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {


    @GetMapping("/hello")
    public String Hello(){
        return "Hello from Docker spring boot app";
    }

}
