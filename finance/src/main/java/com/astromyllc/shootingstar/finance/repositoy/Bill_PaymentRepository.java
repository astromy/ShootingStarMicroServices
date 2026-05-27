package com.astromyllc.shootingstar.finance.repositoy;

import com.astromyllc.shootingstar.finance.model.Bill_Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Bill_PaymentRepository extends JpaRepository<Bill_Payment,Long> {
}
