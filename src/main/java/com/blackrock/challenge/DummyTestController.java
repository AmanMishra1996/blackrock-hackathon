package com.blackrock.challenge;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DummyTestController {

    @GetMapping("/test")
    public String testEndpoint() {
        return "This is a dummy test endpoint!";
    }
}

