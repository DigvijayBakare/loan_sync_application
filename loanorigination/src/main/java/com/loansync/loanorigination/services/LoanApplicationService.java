package com.loansync.loanorigination.services;

import com.loansync.loanorigination.dto.LoanApplicationRequest;
import com.loansync.loanorigination.dto.LoanApplicationResponse;

import java.util.List;
import java.util.UUID;

public interface LoanApplicationService {
    public LoanApplicationResponse createLoanApplication(LoanApplicationRequest applicationRequest, UUID applicantId);

    public LoanApplicationResponse getLoanApplication(UUID applicantId);

    public List<LoanApplicationResponse> getAllApplication();

    public LoanApplicationResponse updateLoanApplication(LoanApplicationRequest applicationRequest, UUID applicationId);

    public void deleteLoanApplication(UUID applicationId);
}
