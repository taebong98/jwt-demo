package com.taebong.szs.study;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class EncryptTest {
    @Test
    public void encryptors_text() throws Exception {
        TextEncryptor text = Encryptors.text("secret", KeyGenerators.string().generateKey());
        String encrypt = text.encrypt("hello");
        String decrypt = text.decrypt(encrypt);

        System.out.println("encrypt = " + encrypt);
        System.out.println("decrypt = " + decrypt);
    }

    @Test
    public void encryptors_strong() throws Exception {
        BytesEncryptor stronger = Encryptors.stronger("secret", KeyGenerators.string().generateKey());

        byte[] encrypt = stronger.encrypt("hello".getBytes(StandardCharsets.UTF_8));
        System.out.println(Arrays.toString(encrypt));
        byte[] decrypt = stronger.decrypt(encrypt);
        System.out.println(Arrays.toString(decrypt));
        String string = byteArrayToString(decrypt);
        System.out.println("string = " + string);
    }

    @Test
    public void encryptors_queryabletext() throws Exception {
        TextEncryptor encryptor = Encryptors.queryableText("secret", KeyGenerators.string().generateKey());

        String encrypt = encryptor.encrypt("hello");
        String decrypt = encryptor.decrypt(encrypt);
        System.out.println("encrypt = " + encrypt);
        System.out.println("decrypt = " + decrypt);
    }

    public String byteArrayToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte abyte :bytes){
            sb.append(abyte);
            sb.append(" ");
        }
        return sb.toString();
    }

    /*
    - 결정세액: 산출세액 - 근로소득세액공제금액 - 특별세액공제금액 - 표준세액공제금액 - 퇴직연금세액공제금액
  - 근로소득세액공제금액 = 산출세액 * 0.55
  - 특별세액공제금액 = 보험료공제금액, 의료비공제금액, 교육비공제금액, 기부금공제금액

    - 보험료공제금액 = 보험료납입금액 * 12% / 100000
    - 의료비공제금액 = (의료비납입금액 - 총급여 * 3%) * 15% / 700000
    - 교육비공제금액 = 교육비납입금액 * 15% / 200000
    - 기부금공제금액 = 기부금납입금액 * 15% / 150000
     */
    @Test
    public void 결정세액_계산() throws Exception {
        int taxAmount = 600_000; // 산출세액
        double work = taxAmount * 0.55;
        double special = special();
        double basic = basic(special);
        double retire = retire(1333333.333);

        double res = taxAmount - work - special- basic - retire;
        System.out.println("res = " + res);
    }

    private double retire(double n) {
        return n * 0.55;
    }

    private double basic(double special) {
        if (special >= 130000) return 0;
        return 130000;
    }

    private double special() {
        double res = getInsurance(100000) + getDonation(150000) + getEducation(200000) + getMedical(700000, 30_000_000);
        return res;
    }

    // 보험료 공제금액
    private double getInsurance(int n) {
        return n * 0.55;
    }

    // 의료비 공제금액
    private double getMedical(int n, int total) {
        double v = (n - total * 0.03) * 0.15;
        if (v < 0) return 0;
        return v;
    }

    // 교육비 공제금액
    private double getEducation(int n) {
        return n * 0.15;
    }

    // 기부금 공제금액
    private double getDonation(int n) {
        return n * 0.15;
    }
}
