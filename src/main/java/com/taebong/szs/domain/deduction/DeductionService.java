package com.taebong.szs.domain.deduction;

import com.taebong.szs.controller.dto.scrap.DeductionResponseDto;
import com.taebong.szs.domain.deduction.repository.DeductionRepository;
import com.taebong.szs.domain.deduction.vo.Deduction;
import com.taebong.szs.domain.user.vo.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeductionService {
    private final DeductionRepository deductionRepository;

    public void getDeduction(List<DeductionResponseDto> deductionResponseDto, User user) {
        log.info("getDeduction. ");

        for (DeductionResponseDto responseDto : deductionResponseDto) {
            Deduction deduction = Deduction.builder()
                    .amount(responseDto.getAmount())
                    .incomeCategory(responseDto.getIncomeCategory())
                    .totalPayment(responseDto.getTotalPayment())
                    .userId(user.getUserId())
                    .build();

            deductionRepository.save(deduction);
        }
    }
}
