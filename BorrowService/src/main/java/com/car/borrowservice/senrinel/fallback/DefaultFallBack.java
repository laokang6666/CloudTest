package com.car.borrowservice.senrinel.fallback;

import com.car.borrowservice.dto.BorrowResponse;
import com.car.common.api.R;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class DefaultFallBack {

    private DefaultFallBack() {}

    public static ResponseEntity<R> defaultFallBack(Long id, Throwable t) {
        String msg = t != null && t.getMessage() != null ? t.getMessage() : "服务降级";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new R("DEGRADED", msg, new BorrowResponse()));
    }
}
