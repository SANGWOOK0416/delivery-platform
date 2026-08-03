package com.portfolio.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OrderRequest(
        @NotNull Long customerId,
        @NotBlank @Size(max = 255) String address
) {
}
