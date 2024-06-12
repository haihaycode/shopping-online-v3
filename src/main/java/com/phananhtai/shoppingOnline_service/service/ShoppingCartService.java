package com.phananhtai.shoppingOnline_service.service;



import com.phananhtai.shoppingOnline_service.dto.ShoppingCartDTO;
import com.phananhtai.shoppingOnline_service.entity.Product;

import java.util.Collection;

public interface ShoppingCartService {
    /**
     * Thêm mặt hàng vào giỏ hoặc tăng số lượng lên 1 nếu đã tồn tại
     * @param id là mã mặt hàng cần thêm
     * @return mặt hàng đã được thêm vào hoặc cập nhật số lượng
     */
    ShoppingCartDTO add(Integer id);
    /**
     * Xóa mặt hàng khỏi giỏ
     * @param id mã mặt hàng cần xóa
     */
    void remove(Integer id);
    /**
     * Thay đổi số lượng lên của mặt hàng trong giỏ
     * @param id mã mặt hàng
     * @param qty số lượng mới
     * @return mặt hàng đã được thay đổi số lượng
     */
    ShoppingCartDTO update(Integer id, int qty);
    /**
     * Xóa sạch các mặt hàng trong giỏ
     */
    void clear();
    /**
     * Lấy tất cả các mặt hàng trong giỏ
     */
    Collection<ShoppingCartDTO> getProducts();
    /**
     * Lấy tổng số lượng các mặt hàng trong giỏ
     */
    int getCount();
    /**
     * Lấy tổng số tiền tất cả các mặt hàng trong giỏ
     LAB4: SPRING BEANS
     SOF3021 – LẬP TRÌNH JAVA 5 TRANG 7
     */
    double getAmount();
}
