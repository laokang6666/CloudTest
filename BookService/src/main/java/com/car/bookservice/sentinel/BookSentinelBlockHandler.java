package com.car.bookservice.sentinel;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.car.bookservice.dto.BookResponse;
import com.car.bookservice.dto.CreateBookRequest;
import com.car.common.api.R;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * Sentinel 流控：统一 {@link R}，body 为空的 {@link BookResponse}。
 */
public final class BookSentinelBlockHandler {

    public static final String CODE_FLOW_BLOCKED = "FLOW_BLOCKED";

    private BookSentinelBlockHandler() {}

    private static ResponseEntity<R> blockedWithEmptyBook() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new R(CODE_FLOW_BLOCKED, "请求过于频繁", new BookResponse()));
    }

    public static ResponseEntity<R> blockCreate(CreateBookRequest request, BlockException ex) {
        return blockedWithEmptyBook();
    }

    public static ResponseEntity<R> blockGet(Long id, BlockException ex) {
        return blockedWithEmptyBook();
    }

    public static ResponseEntity<R> blockBorrow(Long id, Map<String, Object> ignored, BlockException ex) {
        return blockedWithEmptyBook();
    }

    public static ResponseEntity<R> blockReturn(Long id, Map<String, Object> ignored, BlockException ex) {
        return blockedWithEmptyBook();
    }
}
