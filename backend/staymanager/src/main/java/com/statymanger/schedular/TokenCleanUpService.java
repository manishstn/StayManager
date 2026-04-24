package com.statymanger.schedular;

import com.statymanger.repository.RevokedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenCleanUpService {
    private final RevokedTokenRepository revokedTokenRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void dailyCleanup() {
        revokedTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}