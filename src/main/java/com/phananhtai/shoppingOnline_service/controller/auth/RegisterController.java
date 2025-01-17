package com.phananhtai.shoppingOnline_service.controller.auth;

import com.phananhtai.shoppingOnline_service.dao.AccountDAO;
import com.phananhtai.shoppingOnline_service.dto.UserDTO;

import com.phananhtai.shoppingOnline_service.entity.Account;
import com.phananhtai.shoppingOnline_service.service.MailerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Paths;

@Controller
public class RegisterController {
    @Autowired
    AccountDAO accountDAO;
    @Autowired
    MailerService mailerService;

    @GetMapping("/register")
    public String showRegistration(Model model){
        model.addAttribute("create", new UserDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String handleRegistration(@Valid @ModelAttribute("create") UserDTO user,
                                     BindingResult result,
                                     RedirectAttributes redirectAttributes) {
        // Kiểm tra lỗi xác thực
        if (result.hasErrors()) {
            return "auth/register";
        }

        // Kiểm tra sự tồn tại của tài khoản
        Account existingAccount = accountDAO.findByUsername(user.getUsername().trim());
        if (existingAccount != null) {
            result.rejectValue("username", "register.username", "Username already exists!");
            return "auth/register";
        }

        // Kiểm tra mật khẩu và xác nhận mật khẩu có khớp không
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            String errorMsg = "register.password.mismatch";
            result.rejectValue("password", "register.password", errorMsg);
            result.rejectValue("confirmPassword", "register.confirmPassword", errorMsg);
            return "auth/register";
        }

        // Tạo tài khoản mới và lưu vào cơ sở dữ liệu
        Account newAccount = new Account();
        newAccount.setUsername(user.getUsername().trim());
        newAccount.setPassword(user.getPassword());
        newAccount.setEmail(user.getEmail());
        newAccount.setAdmin(false);
        newAccount.setActivated(true);

        accountDAO.save(newAccount);

        // Thêm thông báo thành công và chuyển hướng
        redirectAttributes.addFlashAttribute("success", "register.success");
        try {
            String htmlTemplate = new String(Files.readAllBytes(Paths.get("src/main/resources/templates/welcome.html")));
            String htmlContent = htmlTemplate.replace("{{username}}", newAccount.getUsername());
            mailerService.send(newAccount.getEmail(), "Welcome to Our Service Shopping Online!", htmlContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/login?success=Registration successful!";
    }

}
