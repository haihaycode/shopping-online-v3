package com.phananhtai.shoppingOnline_service.dto;

import lombok.Data;

@Data

public class ProductStatisticsDTO {
    private Integer productId;
    private String productName;
    private Double productpriceStock;
    private Long totalQuantity;
    private Double totalPrice;

    private String Image;

    public ProductStatisticsDTO(Integer productId, String productName,Double productpriceStock, Long totalQuantity, Double totalPrice) {
        this.productId = productId;
        this.productName = productName;
        this.totalQuantity = totalQuantity;
        this.totalPrice = totalPrice;
        this.productpriceStock= productpriceStock;
    }

    public ProductStatisticsDTO(Integer productId, String productName,String Image, Double productpriceStock) {
        this.productId = productId;
        this.productName = productName;
        this.Image =Image;
        this.productpriceStock= productpriceStock;
    }
}
