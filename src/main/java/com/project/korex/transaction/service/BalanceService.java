package com.project.korex.transaction.service;

import com.project.korex.transaction.dto.response.BalanceResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BalanceService {
    public List<BalanceResponseDto> getMyBalances() {
        return null;
    }
}
