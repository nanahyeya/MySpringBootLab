package com.rookies3.myspringbootlab.controller;

import com.rookies3.myspringbootlab.controller.dto.PublisherDTO;
import com.rookies3.myspringbootlab.controller.dto.PublisherDTO.*;
import com.rookies3.myspringbootlab.service.PublisherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/publishers")
@RequiredArgsConstructor
public class PublisherController {

    private final PublisherService publisherService;

    @GetMapping
    public ResponseEntity<List<PublisherDTO.Response>> getAllPublishers() {
        List<PublisherDTO.Response> publishers = publisherService.getAllPublishers();
        return ResponseEntity.ok(publishers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublisherDTO.DetailResponse> getPublisherById(@PathVariable Long id) {
        PublisherDTO.DetailResponse publisher = publisherService.getPublisherById(id);
        return ResponseEntity.ok(publisher);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<PublisherDTO.Response> getPublisherByName(@PathVariable String name) {
        PublisherDTO.Response publisher = publisherService.getPublisherByName(name);
        return ResponseEntity.ok(publisher);
    }

    @PostMapping
    public ResponseEntity<PublisherDTO.Response> createPublisher(
            @Valid @RequestBody PublisherDTO.Request request) {
        PublisherDTO.Response publisher = publisherService.createPublisher(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(publisher);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PublisherDTO.Response> updatePublisher(
            @PathVariable Long id, @Valid @RequestBody PublisherDTO.Request request) {
        PublisherDTO.Response publisher = publisherService.updatePublisher(id, request);
        return ResponseEntity.ok(publisher);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublisher(@PathVariable Long id) {
        publisherService.deletePublisher(id);
        return ResponseEntity.noContent().build();
    }
}