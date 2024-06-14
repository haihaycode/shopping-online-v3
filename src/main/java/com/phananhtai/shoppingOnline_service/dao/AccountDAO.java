package com.phananhtai.shoppingOnline_service.dao;


import com.phananhtai.shoppingOnline_service.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountDAO extends JpaRepository<Account, String> {
  Account findByUsername(String UserName);
  Account findByUsernameAndActivated(String UserName,boolean active);
  List<Account> findAll();
  @Query("SELECT a FROM Account a " +
          "WHERE (:ac is null or a.activated = :ac) " +
          "AND (LOWER(a.username) LIKE %:keywords% " +
          "OR LOWER(a.fullname) LIKE %:keywords% " +
          "OR LOWER(a.email) LIKE %:keywords%)")
  Page<Account> findByActivatedAndKeywordsContaining(
          @Param("ac") Boolean ac,
          @Param("keywords") String keywords,
          Pageable pageable
  );

}
