package com.taebong.szs.domain.deduction.vo;

import com.taebong.szs.controller.dto.DeductResponseDto;
import com.taebong.szs.domain.user.vo.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Deduction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String amount;

    private String incomeCategory;

    private String totalPayment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public DeductResponseDto toDeductResponseDto() {
        return DeductResponseDto.builder()
                .id(id)
                .amount(amount)
                .incomeCategory(incomeCategory)
                .totalPayment(totalPayment)
                .userId(user.getUserId())
                .build();
    }
}
