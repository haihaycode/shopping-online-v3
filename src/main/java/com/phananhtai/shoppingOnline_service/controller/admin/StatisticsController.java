package com.phananhtai.shoppingOnline_service.controller.admin;

import com.phananhtai.shoppingOnline_service.dao.OrderDetailDAO;
import com.phananhtai.shoppingOnline_service.dto.ProductStatisticsDTO;
import com.phananhtai.shoppingOnline_service.entity.OrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.Optional;

@Controller
public class StatisticsController {
    @Autowired
    OrderDetailDAO orderDetailDAO;
    @GetMapping("/admin/product-sales")
    public String getProductStatistics(
            @RequestParam("before") @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> startDate,
            @RequestParam("after") @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> endDate,
            @RequestParam("p") Optional<Integer> page,
            @RequestParam("orderBy") Optional<String> orderBy,

            Model model

           ) {
        int currentPage = page.orElse(0);
        int pageSize = 10;

        Date beforeDate = startDate.orElse(new Date(Long.MIN_VALUE));
        Date afterDate = endDate.orElse(new Date());

        String orderByField = orderBy.orElse("id");


        Pageable pageable = PageRequest.of(currentPage, pageSize,Sort.by(orderByField).ascending());

        Page<ProductStatisticsDTO> productStatistics = orderDetailDAO.findProductStatistics(beforeDate, afterDate, pageable);
        model.addAttribute("p", currentPage);
        model.addAttribute("orderBy", orderByField);
        model.addAttribute("start", beforeDate);
        model.addAttribute("end", afterDate);
        model.addAttribute("productStatistics",productStatistics);


        return "/admin/Statistics/Product_Sales_Statistics";
    }
}
