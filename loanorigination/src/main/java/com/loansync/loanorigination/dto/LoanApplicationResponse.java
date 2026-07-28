package com.loansync.loanorigination.dto;

import com.loansync.loanorigination.entity.LoanApplication;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationResponse {
    private UUID applicationId;

    private UUID applicantId;

    private BigDecimal amount;

    private int tenureMonths;

    private String purpose;

    private LoanApplication.Status status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
