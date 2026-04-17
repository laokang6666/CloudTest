package com.car.borrowservice.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.car.borrowservice.dto.BorrowResponse;
import com.car.borrowservice.dto.CreateBorrowRequest;
import com.car.borrowservice.senrinel.fallback.DefaultFallBack;
import com.car.borrowservice.senrinel.handler.DefaultBlockHandler;
import com.car.borrowservice.service.BorrowApplicationService;
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
@RequestMapping("/api/borrows")
public class BorrowController {

    public static final String CODE_FLOW_BLOCKED = "FLOW_BLOCKED";

    private final BorrowApplicationService borrowApplicationService;

    public BorrowController(BorrowApplicationService borrowApplicationService) {
        this.borrowApplicationService = borrowApplicationService;
    }

    @PostMapping
    @SentinelResource(value = "createBorrowResource",
            blockHandler = "createBlockHandler",
            fallback = "createFallBack")
    public ResponseEntity<R> create(@Valid @RequestBody CreateBorrowRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(R.ok(borrowApplicationService.createBorrow(request)));
    }

    @GetMapping("/{id}")
    @SentinelResource(value = "getBorrowResource",
            blockHandler = "defaultBlockHandler",
            blockHandlerClass = DefaultBlockHandler.class,
            defaultFallback = "defaultFallBack",
            fallbackClass = DefaultFallBack.class)
    public ResponseEntity<R> get(@PathVariable Long id) {
        return ResponseEntity.ok(R.ok(borrowApplicationService.getBorrow(id)));
    }

    @PostMapping("/{id}/return")
    @SentinelResource(value = "getBorrowResource",
            blockHandler = "defaultBlockHandler",
            blockHandlerClass = DefaultBlockHandler.class,
            defaultFallback = "defaultFallBack",
            fallbackClass = DefaultFallBack.class)
    public ResponseEntity<R> returnBorrow(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Object> ignored) {
        return ResponseEntity.ok(R.ok(borrowApplicationService.returnBorrow(id)));
    }

    public static ResponseEntity<R> createBlockHandler(CreateBorrowRequest request, BlockException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new R(CODE_FLOW_BLOCKED, "请求过于频繁", new BorrowResponse()));
    }

    public static ResponseEntity<R> createFallBack(CreateBorrowRequest request, Throwable t) {
        String msg = t != null && t.getMessage() != null ? t.getMessage() : "服务降级";
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new R("DEGRADED", msg, new BorrowResponse()));
    }
}
