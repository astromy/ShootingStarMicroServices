package com.astromyllc.shootingstar.adminpta.util;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
@Slf4j
public class PaystackSignatureVerifier {

    private static final String HMAC_ALGO = "HmacSHA512";

    @Value("${paystack.secrete}")
    private String paystackSecretKey;

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public boolean isValid(String rawBody, String signatureHeader) {
        if (rawBody == null || signatureHeader == null || signatureHeader.isBlank()) {
            return false;
        }
        try {
            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(paystackSecretKey.getBytes(StandardCharsets.UTF_8), HMAC_ALGO));
            String computed = bytesToHex(mac.doFinal(rawBody.getBytes(StandardCharsets.UTF_8)));

            // Constant-time comparison — avoids leaking timing info about how much
            // of the signature matched, which is what makes this safe to use for auth.
            return MessageDigest.isEqual(
                    computed.getBytes(StandardCharsets.UTF_8),
                    signatureHeader.trim().getBytes(StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            log.error("Failed to verify Paystack webhook signature", e);
            return false;
        }
    }
}