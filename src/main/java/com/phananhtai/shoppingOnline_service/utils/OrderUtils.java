package com.phananhtai.shoppingOnline_service.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class OrderUtils {
    // Hàm tạo chuỗi mã đơn hàng
    public static String generateOrderCode() {
        // Lấy thời gian hiện tại
        Date now = new Date();
        // Định dạng thời gian thành chuỗi
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = dateFormat.format(now);

        // Tạo một chuỗi số ngẫu nhiên
        Random random = new Random();
        int randomNumber = random.nextInt(9000) + 1000; // Số ngẫu nhiên từ 1000 đến 9999

        // Kết hợp thời gian và số ngẫu nhiên để tạo mã đơn hàng
        String orderCode = "OD" + timestamp + randomNumber;

        return orderCode;
    }
    public static String generateImage() {
        // Lấy thời gian hiện tại
        Date now = new Date();
        // Định dạng thời gian thành chuỗi
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = dateFormat.format(now);

        // Tạo một chuỗi số ngẫu nhiên
        Random random = new Random();
        int randomNumber = random.nextInt(9000) + 1000; // Số ngẫu nhiên từ 1000 đến 9999

        // Kết hợp thời gian và số ngẫu nhiên để tạo mã đơn hàng
        String orderCode = "IM" + timestamp + randomNumber;

        return orderCode;
    }
    public static String generateUniqueId() {
        // Lấy ngày tháng năm và thời gian hiện tại
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Định dạng để chuyển LocalDateTime thành chuỗi theo yêu cầu
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmssSSS");

        // Chuyển đổi thành chuỗi ID
        String idString = currentDateTime.format(formatter);

        // Lấy 6 ký tự cuối cùng của chuỗi ID
        String sixCharId = idString.substring(idString.length() - 6);

        return sixCharId;
    }


    public static String generateToken(String input) {
        // Lấy thời gian hiện tại
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        String formattedNow = now.format(formatter);

        // Tạo UUID ngẫu nhiên
        String randomUUID = UUID.randomUUID().toString();

        // Kết hợp chuỗi đầu vào, thời gian hiện tại và UUID
        String combinedString = input + "-" + formattedNow + "-" + randomUUID;

        // Mã hóa chuỗi kết hợp bằng SHA-256
        String token = sha256(combinedString);

        return token;
    }

    private static String sha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
