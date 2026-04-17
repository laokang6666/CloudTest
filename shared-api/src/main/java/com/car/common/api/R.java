package com.car.common.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一 HTTP JSON 响应：非泛型外壳，业务数据放在 {@link #body}。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class R {

    /** 成功时业务码 */
    public static final String CODE_OK = "0";

    private String code;
    private String message;
    private Object body;

    public static R ok(Object body) {
        return new R(CODE_OK, "OK", body);
    }

    public static R ok(String message, Object body) {
        return new R(CODE_OK, message, body);
    }

    public static R fail(String code, String message) {
        return new R(code, message, null);
    }

    public boolean isSuccess() {
        return CODE_OK.equals(code);
    }
}
