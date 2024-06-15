package com.phananhtai.shoppingOnline_service.dao;


import com.phananhtai.shoppingOnline_service.entity.Account;
import com.phananhtai.shoppingOnline_service.entity.Order;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.Optional;

public interface OrderDAO extends JpaRepository<Order, Long> {
    Optional<Order> findByPhoneAndOderCode(Integer phone,String orderCode );
    @Query("SELECT o FROM Order o " +
            "WHERE (:idOrderStatus IS NULL OR o.orderStatus.id = :idOrderStatus) " +
            "AND (LOWER(o.address) LIKE %:keywords% " +
            "OR LOWER(o.fullName) LIKE %:keywords% " +
            "OR LOWER(o.oderCode) LIKE %:keywords%)" +
            "AND (:startDate IS NULL OR :endDate IS NULL OR o.createDate BETWEEN :startDate AND :endDate)")
    Page<Order> findByOrderStatusAndKeywordsContainingAndCreateDateBetween(
            @Param("idOrderStatus") Long idOrderStatus,
            @Param("keywords") String keywords,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            Pageable pageable
    );
}