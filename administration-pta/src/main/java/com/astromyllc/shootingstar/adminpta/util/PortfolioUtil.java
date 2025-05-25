package com.astromyllc.shootingstar.adminpta.util;

import com.astromyllc.shootingstar.adminpta.model.Portfolio;
import com.astromyllc.shootingstar.adminpta.repository.PortfolioRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PortfolioUtil {

    private final PortfolioRepository portfolioRepository;
    public static List<Portfolio> portfolioGlobalList;

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @PostConstruct
    private void fetAllPortfolio() {
        portfolioGlobalList = portfolioRepository.findAll();
        log.info("Global Portfolio List populated with {} records", portfolioGlobalList.size());
    }
}
