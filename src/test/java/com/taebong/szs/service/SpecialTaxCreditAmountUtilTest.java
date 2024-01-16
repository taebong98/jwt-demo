package com.taebong.szs.service;

import com.taebong.szs.common.util.SpecialTaxCreditAmountUtil;
import com.taebong.szs.domain.user.vo.Deduction;
import com.taebong.szs.domain.user.vo.DeductionCategory;
import com.taebong.szs.domain.user.vo.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SpecialTaxCreditAmountUtilTest {
    @InjectMocks
    SpecialTaxCreditAmountUtil specialTaxCreditAmountUtil;

    @BeforeEach
    void init() {
        List<Deduction> deductionList = hongGilDongScrapDataFixture();

        User hongGilDong = User.builder()
                .id(1L)
                .userId("hong12")
                .taxAmount("3,000,000")
                .deductionList(deductionList)
                .build();
    }

    @Test
    public void donationTest() throws Exception {
        // given
        String amount = "150000";
        BigDecimal donationPaid = new BigDecimal(amount);
        double donationPaidDouble = Double.parseDouble(amount);
        double expect = getDonationDeductionAmount(donationPaidDouble);

        // when
        BigDecimal actual = specialTaxCreditAmountUtil.donation(donationPaid);

        // then
        assertThat(actual.doubleValue()).isEqualTo(expect);
    }

    @Test
    public void insuranceTest() throws Exception {
        // given
        String amount = "100000";
        BigDecimal insurancePaid = new BigDecimal(amount);
        double insurancePaidDouble = Double.parseDouble(amount);

        // when
        BigDecimal actual = specialTaxCreditAmountUtil.insure(insurancePaid);

        // then
        assertThat(actual.doubleValue()).isEqualTo(getInsuranceDeductionAmount(insurancePaidDouble));
    }

    @Test
    public void educationTest() throws Exception {
        // given
        String amount = "200000";
        BigDecimal educationPaid = new BigDecimal(amount);
        double educationPaidDouble = Double.parseDouble(amount);

        // when
        BigDecimal actual = specialTaxCreditAmountUtil.education(educationPaid);

        // then
        assertThat(actual.doubleValue()).isEqualTo(getEducationDeductionAmount(educationPaidDouble));
    }

    @Test
    public void medicalTest() throws Exception {
        // given
        String amount = "4400000";
        String totalSalaryString = "6000000";

        BigDecimal medicalPaid = new BigDecimal(amount);
        BigDecimal totalSalary = new BigDecimal(totalSalaryString);

        double medicalPaidDouble = Double.parseDouble(amount);
        double totalSalaryDouble = Double.parseDouble(totalSalaryString);

        // when
        BigDecimal actual = specialTaxCreditAmountUtil.medical(medicalPaid, totalSalary);

        // then
        assertThat(actual.doubleValue()).isEqualTo(getMedicalDeductionAmount(medicalPaidDouble, totalSalaryDouble));
    }

    @Test
    @DisplayName("의료비 공제금액이 0보다 작으면 0으로 계산한다.")
    public void medicalTest_under_zero() throws Exception {
        // given
        String amount = "10000";
        String totalSalaryString = "3000000000";

        BigDecimal medicalPaid = new BigDecimal(amount);
        BigDecimal totalSalary = new BigDecimal(totalSalaryString);

        double medicalPaidDouble = Double.parseDouble(amount);
        double totalSalaryDouble = Double.parseDouble(totalSalaryString);

        // when
        BigDecimal actual = specialTaxCreditAmountUtil.medical(medicalPaid, totalSalary);

        // then
        assertThat(actual.doubleValue()).isEqualTo(0);
    }

    private double getDonationDeductionAmount(double donationPaid) {
        return donationPaid * 0.15;
    }

    private double getEducationDeductionAmount(double educationPaid) {
        return educationPaid * 0.15;
    }

    private double getInsuranceDeductionAmount(double insurancePaid) {
        return insurancePaid * 0.12;
    }

    private double getMedicalDeductionAmount(double medicalPaid, double totalSalary) {
        double res = (medicalPaid - (totalSalary * 0.03)) * 0.15;
        if (res < 0) {
            return 0;
        }
        return res;
    }

    private List<Deduction> hongGilDongScrapDataFixture() {
        List<Deduction> deductionList = new ArrayList<>();
        Deduction hongDeductionInsurance = Deduction.builder()
                .deductionAmount("100,000")
                .deductionCategory(DeductionCategory.INSURANCE)
                .build();

        Deduction hongDeductionEducation = Deduction.builder()
                .deductionAmount("200,000")
                .deductionCategory(DeductionCategory.EDUCATION)
                .build();

        Deduction hongDeductionDonation = Deduction.builder()
                .deductionAmount("150,000")
                .deductionCategory(DeductionCategory.DONATION)
                .build();

        Deduction hongDeductionMedical = Deduction.builder()
                .deductionAmount("4,400,000")
                .deductionCategory(DeductionCategory.MEDICAL)
                .build();

        Deduction hongDeductionRetire = Deduction.builder()
                .totalPayment("6,000,000")
                .deductionCategory(DeductionCategory.RETIREMENT_PENSION)
                .build();

        deductionList.add(hongDeductionInsurance);
        deductionList.add(hongDeductionEducation);
        deductionList.add(hongDeductionDonation);
        deductionList.add(hongDeductionMedical);
        deductionList.add(hongDeductionRetire);
        return deductionList;
    }
}
