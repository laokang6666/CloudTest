package com.car.borrowservice.controller;

import com.car.borrowservice.dto.BorrowResponse;
import com.car.borrowservice.dto.CreateBorrowRequest;
import com.car.borrowservice.service.BorrowApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/borrows")
public class BorrowController {

    private final BorrowApplicationService borrowApplicationService;

    public BorrowController(BorrowApplicationService borrowApplicationService) {
        this.borrowApplicationService = borrowApplicationService;
    }

    @PostMapping
    public ResponseEntity<BorrowResponse> create(@Valid @RequestBody CreateBorrowRequest request) {
        BorrowResponse body = borrowApplicationService.createBorrow(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BorrowResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(borrowApplicationService.getBorrow(id));
    }
}
