package com.concert.app.infrastructure.encryption;

import com.common.exception.ApiException;
import com.common.exception.ExceptionCode;
import com.concert.app.domain.encryption.EncryptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Slf4j
@Service
public class EncryptionImpl implements EncryptionUtil {
    private static final String ALGORITHM = "AES";
    private final SecretKey secretKey;

    // 고정 키 초기화
    public EncryptionImpl(@Value("${encryption.base64-key}") String base64Key) {
        this.secretKey = createKeyFromBase64(base64Key);
    }


    // 암호화 메서드
    @Override
    public String encrypt(String data) {
        if (data == null || data.isEmpty()) {
            throw new ApiException(ExceptionCode.ENCRYPTION_DATA_INVALID, LogLevel.ERROR);
        }
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            throw new ApiException(ExceptionCode.ENCRYPTION_ERROR, LogLevel.ERROR);
        }
    }

    // 복호화 메서드
    @Override
    public String decrypt(String encryptedData) {
        if (encryptedData == null || encryptedData.isEmpty()) {
            throw new ApiException(ExceptionCode.DECRYPTION_DATA_INVALID, LogLevel.ERROR);
        }
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedData = Base64.getDecoder().decode(encryptedData);
            return new String(cipher.doFinal(decodedData));
        } catch (Exception e) {
            throw new ApiException(ExceptionCode.DECRYPTION_ERROR, LogLevel.ERROR);
        }
    }

    // Base64 키로 SecretKey 생성
    private SecretKey createKeyFromBase64(String base64Key) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(base64Key);
            return new SecretKeySpec(decodedKey, ALGORITHM);
        } catch (Exception e) {
            log.error("고정 키 초기화 실패: {}", e.getMessage());
            throw new ApiException(ExceptionCode.GENERATION_ERROR, LogLevel.ERROR);
        }
    }
}
