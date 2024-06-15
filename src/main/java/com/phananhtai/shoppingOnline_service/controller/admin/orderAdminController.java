package com.phananhtai.shoppingOnline_service.controller.admin;

import com.phananhtai.shoppingOnline_service.dao.OderStatusDAO;
import com.phananhtai.shoppingOnline_service.dao.OrderDAO;
import com.phananhtai.shoppingOnline_service.entity.Category;
import com.phananhtai.shoppingOnline_service.entity.Order;
import com.phananhtai.shoppingOnline_service.entity.OrderDetail;
import com.phananhtai.shoppingOnline_service.entity.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class orderAdminController {

    @Autowired
    OrderDAO orderDAO;
    @Autowired
    OderStatusDAO oderStatusDAO;
    @GetMapping("/admin/orders")
    public String showOrder(
            @RequestParam("keywords") Optional<String> keywords,
            @RequestParam("before") @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> before,
            @RequestParam("after") @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> after,
            @RequestParam("idOrderStatus") Optional<Long> idOrderStatus,
            @RequestParam("p") Optional<Integer> page,
            @RequestParam("orderBy") Optional<String> orderBy,
            Model model
    ) {
        String kw = keywords.orElse("");
        int currentPage = page.orElse(0);
        int pageSize = 10;
        String orderByField = orderBy.orElse("id");
        Long orderStatus=idOrderStatus.orElse(1L);

        Date beforeDate = before.orElse(new Date(Long.MIN_VALUE));
        Date afterDate = after.orElse(new Date());


        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by(orderByField).ascending());
        Page<Order> orderPage = orderDAO.findByOrderStatusAndKeywordsContainingAndCreateDateBetween(orderStatus, kw,beforeDate, afterDate, pageable);

        model.addAttribute("orderPage", orderPage);
        model.addAttribute("keywords", kw);
        model.addAttribute("p", currentPage);
        model.addAttribute("idOrderStatus", idOrderStatus);
        model.addAttribute("orderBy", orderByField);
        model.addAttribute("start", beforeDate);
        model.addAttribute("end", afterDate);


        return "admin/order/order";
    }
    @ModelAttribute("OrderStatus")
    public List<OrderStatus> orderStatusList(){
        return oderStatusDAO.findAll();
    }
    @PostMapping("/admin/orders/updateStatus")
    public String updateOrderStatus(@RequestParam Long orderId, @RequestParam Long orderStatusId) {
        Optional<Order> order = orderDAO.findById(orderId);
        if(order.isPresent()){
            OrderStatus newStatus = oderStatusDAO.findById(orderStatusId).orElseThrow(() -> new IllegalArgumentException("Invalid status ID"));
            order.get().setOrderStatus(newStatus);
            orderDAO.save(order.get());
        }
        return "redirect:/admin/orders";
    }


}
