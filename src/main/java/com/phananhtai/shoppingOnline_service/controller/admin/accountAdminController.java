package com.phananhtai.shoppingOnline_service.controller.admin;

import com.phananhtai.shoppingOnline_service.dao.AccountDAO;
import com.phananhtai.shoppingOnline_service.dto.UserDTO;
import com.phananhtai.shoppingOnline_service.entity.Account;
import com.phananhtai.shoppingOnline_service.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
@Controller
public class accountAdminController {
    @Autowired
    AccountDAO accountDAO;
    @GetMapping("/admin/account")
    public String showCategory(
            @RequestParam("keywords") Optional<String> keywords,
            @RequestParam("active") Optional<Boolean> active,
            @RequestParam("p") Optional<Integer> page,
            @RequestParam("orderBy") Optional<String> orderBy,
            Model model
    ) {
        String kw = keywords.orElse("");
        int currentPage = page.orElse(0);
        int pageSize = 10;
        Boolean ac = active.orElse(true);
        String orderByField = orderBy.orElse("id");

        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by(orderByField).ascending());
        Page<Account> accountsPage = accountDAO.findByActivatedAndKeywordsContaining(ac, kw, pageable);

        model.addAttribute("accountsPage", accountsPage);
        model.addAttribute("keywords", kw);
        model.addAttribute("active", ac);
        model.addAttribute("orderBy", orderByField);

        model.addAttribute("account", new UserDTO());

        return "admin/account/AccountList";
    }
}
