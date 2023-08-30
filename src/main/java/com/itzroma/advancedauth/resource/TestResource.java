package com.itzroma.advancedauth.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestResource {
    @GetMapping("/all")
    public ResponseEntity<String> helloAll() {
        return ResponseEntity.ok("hello all");
    }

    @GetMapping("/authenticated")
    public ResponseEntity<Object> info(Authentication authentication) {
        return ResponseEntity.ok(authentication.getPrincipal());
    }
}
