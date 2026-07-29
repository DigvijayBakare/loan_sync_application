package com.loansync.loanorigination.repository;

import com.loansync.loanorigination.dto.LoanApplicationResponse;
import com.loansync.loanorigination.entity.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, UUID> {
    LoanApplicationResponse findByApplicationId(UUID applicationId);

    List<LoanApplicationResponse> findByStatus(String status);
}
