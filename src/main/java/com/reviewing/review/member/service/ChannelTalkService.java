package com.reviewing.review.member.service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ChannelTalkService {

    @Value("${channelTalk.secret-key}")
    private String secretKey;

    public String encode(String memberId) {
        try {
            byte[] keyBytes = hexStringToByteArray(secretKey);

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(keyBytes, "HmacSHA256"));

            byte[] hash = mac.doFinal(memberId.getBytes());

            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("HMAC encoding failed", e);
        }
    }

    private byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        if (len % 2 != 0) throw new IllegalArgumentException("Invalid hex string");

        byte[] result = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            result[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return result;
    }

}
