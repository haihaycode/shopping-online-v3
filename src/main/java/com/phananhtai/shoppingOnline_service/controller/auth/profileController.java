package com.phananhtai.shoppingOnline_service.controller.auth;

import com.phananhtai.shoppingOnline_service.dao.AccountDAO;
import com.phananhtai.shoppingOnline_service.dto.PasswordDTO;
import com.phananhtai.shoppingOnline_service.dto.ProductDTO;
import com.phananhtai.shoppingOnline_service.dto.UserDTO;
import com.phananhtai.shoppingOnline_service.dto.profileDTO;
import com.phananhtai.shoppingOnline_service.entity.Account;
import com.phananhtai.shoppingOnline_service.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class profileController {
    @Autowired
    SessionService sessionService;
    @Autowired
    AccountDAO accountDAO;
    @GetMapping("/account")
    public String showProfile(Model model) {
        Account user = sessionService.get("user");
        profileDTO userDTO= new profileDTO();

        userDTO.setUsername(user.getUsername());
        userDTO.setFullname(user.getFullname());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhone(user.getPhone());
        userDTO.setAddress(user.getAddress());

        model.addAttribute("user", user);
        return "auth/profile";
    }

    @PostMapping("/account")
    public String updateProfile(@ModelAttribute("user") @Valid profileDTO userDTO, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "auth/profile";
        }
        // Cập nhật thông tin người dùng và lưu vào cơ sở dữ liệu
        Account user = sessionService.get("user");
        user.setFullname(userDTO.getFullname());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setAddress(userDTO.getAddress());
        accountDAO.save(user);

        return "redirect:/account";
    }
    @GetMapping("/account/change-password")
    public String showChangePasswordForm(Model model) {
        model.addAttribute("passwordDTO", new PasswordDTO());
        return "auth/change-password";
    }
    @PostMapping("/account/change-password")
    public String changePassword(@ModelAttribute("passwordDTO") @Valid PasswordDTO passwordDTO, BindingResult result) {
          Account account= sessionService.get("user");
        if (result.hasErrors() || !passwordDTO.isPasswordConfirmed()) {
            if (!passwordDTO.isPasswordConfirmed()) {
                result.rejectValue("confirmPassword", "error.passwordDTO", "Passwords do not match");
            }
            if (passwordDTO.getCurrentPassword().equals(account.getPassword())==false) {
                result.rejectValue("currentPassword", "error.currentPassword", "Current password is incorrect");
            }
            return "auth/change-password";
        }

        account.setPassword(passwordDTO.getNewPassword());
        accountDAO.save(account);

        return "redirect:/account/change-password";
    }

    @GetMapping("/account/updateAvatar")
    public String showAvatar(Model model) {
        Account account= sessionService.get("user");
        account=accountDAO.findByUsername(account.getUsername());
        model.addAttribute("username",account.getUsername());
        model.addAttribute("avatar",account.getPhoto());
        return "auth/updateAvatar";
    }
    @PostMapping("/account/updateAvatar")
    public String UpdateAvatar(Model model,
                               @RequestParam("username") String username) {
        Account account= accountDAO.findByUsernameAndActivated(username,true);


        return "auth/updateAvatar";
    }
}
