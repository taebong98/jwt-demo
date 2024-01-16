package com.taebong.szs.study;

import org.junit.jupiter.api.Test;

public class CalSpecialDeductionPrice {

    /*
    보험료공제금액 = 보험료납입금액 * 12%, (10)
    의료비공제금액 = (의료비납입금액 - 총급여 * 3%) * 15%, (20) 총급여: 60,000,000
        단, 의료비공제금액 < 0 일 경우, 의료비공제금액 = 0 처리 한다.
    기부금공제금액 = 기부금납입금액 * 15%, (15)
    교육비공제금액 = 교육비납입금액 * 15%, (4400000)
     */
    @Test
    public void test() throws Exception {
        double a = 100000 * 0.12; // 보험
        double b = (4400000 - 60000000 * 0.03) * 0.15; // 의료
        double c = 150000 * 0.15; // 기부
        double d = 200000 * 0.15; // 교육

        System.out.println("보험 = " + a);
        System.out.println("교육 = " + d);
        System.out.println("기부 = " + c);
        System.out.println("의료 = " + b);
        double special = a + b + c + d;
        System.out.println("special = " + special);
    }

}
