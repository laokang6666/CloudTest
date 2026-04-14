package com.car.borrowservice.enums;

/**
 * Borrow 对外错误码（与设计文档一致）。
 */
public enum BorrowErrorCode {

    USER_NOT_FOUND("USER_NOT_FOUND", "用户不存在"),
    BOOK_NOT_FOUND("BOOK_NOT_FOUND", "图书不存在"),
    STOCK_INSUFFICIENT("STOCK_INSUFFICIENT", "库存不足"),
    BORROW_NOT_FOUND("BORROW_NOT_FOUND", "借阅单不存在"),
    REMOTE_USER_ERROR("REMOTE_USER_ERROR", "用户服务调用失败"),
    REMOTE_BOOK_ERROR("REMOTE_BOOK_ERROR", "图书服务调用失败");

    private final String code;
    private final String message;

    BorrowErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static BorrowErrorCode getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (BorrowErrorCode item : values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }

    public static String getMessageByCode(String code) {
        BorrowErrorCode item = getByCode(code);
        return item != null ? item.getMessage() : null;
    }
}
