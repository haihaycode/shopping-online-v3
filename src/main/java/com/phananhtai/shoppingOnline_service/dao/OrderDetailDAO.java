package com.phananhtai.shoppingOnline_service.dao;

import com.phananhtai.shoppingOnline_service.dto.ProductStatisticsDTO;
import com.phananhtai.shoppingOnline_service.entity.OrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface OrderDetailDAO extends JpaRepository<OrderDetail, Long> {
    @Query("SELECT new com.phananhtai.shoppingOnline_service.dto.ProductStatisticsDTO("+
            "od.product.id, " +
            "od.product.name, " +
            "od.price, " +
            "SUM(od.quantity), " +
            "SUM(CAST(od.price * od.quantity AS DOUBLE))) " + // Ép kiểu về Double
            "FROM OrderDetail od " +
            "WHERE od.order.createDate BETWEEN :startDate AND :endDate " +
            "GROUP BY od.product.id, od.product.name")
    Page<ProductStatisticsDTO> findProductStatistics(@Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);


    @Query("SELECT new com.phananhtai.shoppingOnline_service.dto.ProductStatisticsDTO(" +
            "od.product.id, " +
            "od.product.name, " +
            "od.product.image, " +
            "od.product.price) " +
            "FROM OrderDetail od " +
            "WHERE od.order.createDate BETWEEN :startDate AND :endDate " +
            "AND od.product.active = true " + // Thêm điều kiện active = true
            "GROUP BY od.product.id, od.product.name " +
            "ORDER BY SUM(od.quantity) DESC ")
    Page<ProductStatisticsDTO> findTop10ProductsBySalesQuantity(@Param("startDate") Date startDate,
                                                                @Param("endDate") Date endDate,
                                                                Pageable pageable);


}
