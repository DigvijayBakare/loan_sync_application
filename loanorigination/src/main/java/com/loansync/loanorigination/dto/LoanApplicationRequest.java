package com.loansync.loanorigination.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanApplicationRequest {
    @NotNull
    @DecimalMin(value = "1000", message = "Loan amount must be at least 1000")
    private BigDecimal amount;

    @Min(value = 3, message = "Tenure must be at least 3 months")
    @Max(value = 360, message = "Tenure cannot exceed 360 months")
    private int tenureMonths;

    @NotBlank
    private String purpose;
}
