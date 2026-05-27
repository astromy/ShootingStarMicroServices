package com.astromyllc.shootingstar.hr.utils;

import com.astromyllc.shootingstar.hr.dto.request.PortfolioRequest;
import com.astromyllc.shootingstar.hr.model.Portfolio;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PortfolioUtil {
    public static Portfolio mapPortFolioRequest_ToPortfolio(PortfolioRequest p) {
       return Portfolio.builder()
               .build();
    }
}
