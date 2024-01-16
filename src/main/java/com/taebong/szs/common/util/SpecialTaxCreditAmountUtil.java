package com.taebong.szs.common.util;

import com.taebong.szs.domain.user.vo.Deduction;
import com.taebong.szs.domain.user.vo.DeductionCategory;
import com.taebong.szs.domain.user.vo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
public class SpecialTaxCreditAmountUtil {

    //  보험료 납입금액 x 0.12
    public BigDecimal insure(BigDecimal insurePaidAmount) {
        log.info("보함료 납입금액: {}", insurePaidAmount);
        BigDecimal insuranceDeductAmount = insurePaidAmount.multiply(BigDecimal.valueOf(0.12));
        log.info("보험료 공제금액: {}", insuranceDeductAmount);

        return insuranceDeductAmount;
    }

    // 교육비 납입금액 x 0.15
    public BigDecimal education(BigDecimal eduPaidAmount) {
        log.info("교육비 납입금액: {}", eduPaidAmount);
        BigDecimal educationDeductAmount = eduPaidAmount.multiply(BigDecimal.valueOf(0.15));
        log.info("교육비 공제금액: {}", educationDeductAmount);

        return educationDeductAmount;
    }

    // 기부금납입금액 x 0.15
    public BigDecimal donation(BigDecimal donationPaidAmount) {
        log.info("기부금 납입금액: {}", donationPaidAmount);
        BigDecimal donationDeductAmount = donationPaidAmount.multiply(BigDecimal.valueOf(0.15));
        log.info("기부금 공제금액: {}", donationDeductAmount);
        return donationDeductAmount;
    }

    // 의료비공제금액 = (의료비납입금액 - 총급여 * 3%) * 15%
    public BigDecimal medical(BigDecimal medicalPaidAmount, BigDecimal totalSalary) {
        BigDecimal x = totalSalary.multiply(BigDecimal.valueOf(0.03)); // 총급여 x 0.03
        BigDecimal y = medicalPaidAmount.subtract(x); // 의료비 납입 금액 - 총급여 x 0.03

        BigDecimal medicalDeductAmount = y.multiply(BigDecimal.valueOf(0.15));
        if (medicalDeductAmount.compareTo(BigDecimal.ZERO) < 0) {
            log.info("의료비공제금액이 0 보다 작으면 0으로 처리한다.");
            return BigDecimal.ZERO;
        }

        log.info("의료비공제금액: {}", medicalDeductAmount);
        return medicalDeductAmount;
    }
}
