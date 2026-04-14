package com.car.bookservice.controller;

import com.car.bookservice.dto.BookResponse;
import com.car.bookservice.dto.CreateBookRequest;
import com.car.bookservice.service.BookCatalogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookCatalogService bookCatalogService;

    public BookController(BookCatalogService bookCatalogService) {
        this.bookCatalogService = bookCatalogService;
    }

    @PostMapping
    public ResponseEntity<BookResponse> create(@Valid @RequestBody CreateBookRequest request) {
        BookResponse body = bookCatalogService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(bookCatalogService.getBook(id));
    }

    @PostMapping("/{id}/borrow")
    public ResponseEntity<BookResponse> borrow(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> ignored) {
        return ResponseEntity.ok(bookCatalogService.borrowOne(id));
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<BookResponse> returnBook(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> ignored) {
        return ResponseEntity.ok(bookCatalogService.returnOne(id));
    }
}
