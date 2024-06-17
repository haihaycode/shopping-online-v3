package com.phananhtai.shoppingOnline_service.utils;

import java.util.Random;

public class OTPGenerator {

    // Hàm để tạo mã OTP gồm 6 chữ số
    public static String generateOTP() {
        // Tạo đối tượng Random để sinh số ngẫu nhiên
        Random random = new Random();
        // Tạo biến để chứa mã OTP
        StringBuilder otp = new StringBuilder();

        // Vòng lặp để tạo 6 chữ số ngẫu nhiên
        for (int i = 0; i < 6; i++) {
            // Thêm một chữ số ngẫu nhiên vào biến otp
            otp.append(random.nextInt(10));
        }

        // Trả về mã OTP đã tạo
        return otp.toString();
    }

    public static void main(String[] args) {
        // Gọi hàm để tạo mã OTP và in ra console
        String otp = generateOTP();
        System.out.println("Generated OTP: " + otp);
    }
}
