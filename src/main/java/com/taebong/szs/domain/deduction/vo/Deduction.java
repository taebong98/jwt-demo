package com.taebong.szs.domain.deduction.vo;

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

    private String userId;
}
