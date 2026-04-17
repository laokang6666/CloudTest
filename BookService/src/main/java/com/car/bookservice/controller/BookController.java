package com.car.bookservice.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.car.bookservice.dto.CreateBookRequest;
import com.car.bookservice.sentinel.BookSentinelBlockHandler;
import com.car.bookservice.service.BookCatalogService;
import com.car.common.api.R;
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
    @SentinelResource(
            value = "bookCreate",
            blockHandler = "blockCreate",
            blockHandlerClass = BookSentinelBlockHandler.class)
    public ResponseEntity<R> create(@Valid @RequestBody CreateBookRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(R.ok(bookCatalogService.createBook(request)));
    }

    @GetMapping("/{id}")
    @SentinelResource(
            value = "bookGet",
            blockHandler = "blockGet",
            blockHandlerClass = BookSentinelBlockHandler.class)
    public ResponseEntity<R> get(@PathVariable Long id) {
        return ResponseEntity.ok(R.ok(bookCatalogService.getBook(id)));
    }

    @PostMapping("/{id}/borrow")
    @SentinelResource(
            value = "bookBorrow",
            blockHandler = "blockBorrow",
            blockHandlerClass = BookSentinelBlockHandler.class)
    public ResponseEntity<R> borrow(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> ignored) {
        return ResponseEntity.ok(R.ok(bookCatalogService.borrowOne(id)));
    }

    @PostMapping("/{id}/return")
    @SentinelResource(
            value = "bookReturn",
            blockHandler = "blockReturn",
            blockHandlerClass = BookSentinelBlockHandler.class)
    public ResponseEntity<R> returnBook(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> ignored) {
        return ResponseEntity.ok(R.ok(bookCatalogService.returnOne(id)));
    }
}
