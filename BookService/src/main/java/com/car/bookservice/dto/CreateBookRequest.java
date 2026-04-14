package com.car.bookservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateBookRequest {

    @NotBlank
    @Size(max = 256)
    private String title;

    @Positive
    private Integer totalStock;
}
