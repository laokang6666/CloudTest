package com.car.borrowservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateBorrowRequest {

    @NotNull
    @Positive
    private Long userId;

    @NotNull
    @Positive
    private Long bookId;
}
