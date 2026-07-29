package com.loansync.loanorigination.services.impl;

import com.loansync.loanorigination.dto.LoanApplicationRequest;
import com.loansync.loanorigination.dto.LoanApplicationResponse;
import com.loansync.loanorigination.entity.LoanApplication;
import com.loansync.loanorigination.repository.LoanApplicationRepository;
import com.loansync.loanorigination.services.LoanApplicationService;

import java.util.List;
import java.util.UUID;

public class LoanApplicationServiceImpl implements LoanApplicationService {

    private final LoanApplicationRepository loanApplicationRepository;

    public LoanApplicationServiceImpl(LoanApplicationRepository loanApplicationRepository) {
        this.loanApplicationRepository = loanApplicationRepository;
    }

    @Override
    public LoanApplicationResponse createLoanApplication(LoanApplicationRequest applicationRequest, UUID applicantId) {

        LoanApplication loanApplication = LoanApplication.builder().applicantId(applicantId)
                .amount(applicationRequest.getAmount())
                .tenureMonths(applicationRequest.getTenureMonths())
                .purpose(applicationRequest.getPurpose()).status(LoanApplication.Status.SUBMITTED).build();

        loanApplicationRepository.save(loanApplication);

        return LoanApplicationResponse.fromEntity(loanApplication);
    }

    @Override
    public LoanApplicationResponse getLoanApplication(UUID applicantId) {
        return null;
    }

    @Override
    public List<LoanApplicationResponse> getAllApplication() {
        return List.of();
    }

    @Override
    public LoanApplicationResponse updateLoanApplication(LoanApplicationRequest applicationRequest, UUID applicationId) {
        return null;
    }

    @Override
    public void deleteLoanApplication(UUID applicationId) {

    }
}
