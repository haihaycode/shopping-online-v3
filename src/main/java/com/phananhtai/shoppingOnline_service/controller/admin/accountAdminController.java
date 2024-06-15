package com.phananhtai.shoppingOnline_service.controller.admin;

import com.phananhtai.shoppingOnline_service.dao.AccountDAO;
import com.phananhtai.shoppingOnline_service.dto.UserDTO;
import com.phananhtai.shoppingOnline_service.entity.Account;
import com.phananhtai.shoppingOnline_service.entity.Category;
import com.phananhtai.shoppingOnline_service.utils.OrderUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
@Controller
public class accountAdminController {
    @Autowired
    AccountDAO accountDAO;


    private void loadAccountPage(Model model) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("username").ascending());
        Page<Account> accountsPage = accountDAO.findByActivatedAndKeywordsContaining(true, "", pageable);
        model.addAttribute("keywords", "");
        model.addAttribute("active", true);
        model.addAttribute("orderBy", "id");
        model.addAttribute("accountsPage", accountsPage);
    }

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

    @GetMapping("/admin/account/edit/{id}")
    public String showUpdateForm(@PathVariable("id") String id, Model model) {
        loadAccountPage(model);
        Optional<Account> account = accountDAO.findById(id);
        UserDTO userDTO= new UserDTO();
        if(account.isPresent()){



            userDTO.setUsername(account.get().getUsername());
            userDTO.setEmail(account.get().getEmail());
            userDTO.setFullname(account.get().getFullname());
            userDTO.setPassword(account.get().getPassword());

            userDTO.setActivated(account.get().isActivated());
            userDTO.setAdmin(account.get().isAdmin());


        }else{
            model.addAttribute("warning", "user không tồn tại");
        }

        model.addAttribute("account", userDTO);
        return "admin/account/AccountList";
    }

    @PostMapping("/admin/account")
    public String saveAccount(
            @Valid @ModelAttribute("account") UserDTO account,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            if(!account.isPasswordConfirmed()){
                bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
            }
            loadAccountPage(model);
            System.out.println(bindingResult.getAllErrors());
            return "admin/account/AccountList";
        }
        Account account1 =accountDAO.findByUsername(account.getUsername());
        if(account1==null){
            redirectAttributes.addFlashAttribute("warning", "user không tồn tại");
            return "redirect:/admin/account";
        }
        account1.setFullname(account.getFullname());
        account1.setEmail(account.getEmail());
        account1.setPassword(account.getPassword());

        account1.setAdmin(account.isAdmin());
        account1.setActivated(account.isActivated());

        accountDAO.save(account1);
        redirectAttributes.addFlashAttribute("success", "Cập nhật thành công");
        return "redirect:/admin/account";    // Chuyển hướng sau khi thêm thành công
    }

    @GetMapping("/admin/account/active/{id}")
    public String activeAccount(@PathVariable("id") String id, @RequestParam("active") Boolean active ) {
        Account account = accountDAO.getReferenceById(id);
        account.setActivated(active);
        accountDAO.save(account);
        return "redirect:/admin/account?active="+active;
    }
}
