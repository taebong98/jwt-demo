package com.taebong.szs.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;

@Component
public class CryptUtils {
    @Value("${crypto.secret}")
    private String secret;

    @Value("${crypto.salt}")
    private String salt;

    public String encrypt(String plain) {
        TextEncryptor encryptor = Encryptors.text(secret, getStringToHex(salt));
        return encryptor.encrypt(plain);
    }

    public String decrypt(String encrypted) {
        TextEncryptor encryptor = Encryptors.text(secret, getStringToHex(salt));
        return encryptor.decrypt(encrypted);
    }

    public boolean matches(String plane, String encrypted) {
        return decrypt(encrypted).equals(plane);
    }

    private String getStringToHex(String str) {
        byte[] testBytes = str.getBytes(StandardCharsets.UTF_8);
        return DatatypeConverter.printHexBinary(testBytes);
    }
}
