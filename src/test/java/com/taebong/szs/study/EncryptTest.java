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
}
