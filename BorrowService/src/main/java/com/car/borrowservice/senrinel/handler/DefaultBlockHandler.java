package com.car.borrowservice.senrinel.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.car.borrowservice.dto.BorrowResponse;
import com.car.common.api.R;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class DefaultBlockHandler {

    public static final String CODE_FLOW_BLOCKED = "FLOW_BLOCKED";

    private DefaultBlockHandler() {}

    public static ResponseEntity<R> defaultBlockHandler(Long id, BlockException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new R(CODE_FLOW_BLOCKED, "请求过于频繁", new BorrowResponse()));
    }
}
