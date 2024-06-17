package com.phananhtai.shoppingOnline_service.controller.auth;

import com.phananhtai.shoppingOnline_service.dao.AccountDAO;
import com.phananhtai.shoppingOnline_service.entity.Account;
import com.phananhtai.shoppingOnline_service.service.MailerService;
import com.phananhtai.shoppingOnline_service.service.SessionService;
import com.phananhtai.shoppingOnline_service.utils.OTPGenerator;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;


@Controller
public class forgotPasswordController {
    @Autowired
    AccountDAO accountDAO;
    @Autowired
    MailerService mailer;
    @Autowired
    SessionService sessionService;

    @GetMapping("/forgot-Password")
    public String showFormforgotPassword() {
        return "auth/forgotPass";
    }

    @PostMapping("/send-otp")
    public String send_otp(
            @RequestParam("email") String email,
            Model model) {
        Optional<Account> account = accountDAO.findByEmailAndActivated(email, true);
        if (account.isPresent()) {
          String otp= OTPGenerator.generateOTP();
            try {
                mailer.send(account.get().getEmail(),"Shopping Online"," Mã otp của bạn là : "+ otp);
                sessionService.set("otp",otp);
                sessionService.set("email",email);
            } catch (MessagingException e) {
                model.addAttribute("error","Gửi mail thất bại vui lòng thử lại sau ít phút");
                return "auth/forgotPass";
            }

        } else {
          model.addAttribute("error","Email không tồn tại");
          return "auth/forgotPass";

        }

      return "auth/otpAndPasswordNew";
    }

    @PostMapping("/reset-password")
    public String checkOTPAndUpdatePassword(
            Model model,
            RedirectAttributes redirectAttributes,
            @RequestParam("otp") String otp,
            @RequestParam("new_password") String new_password
    ){

        Optional<Account> account = accountDAO.findByEmailAndActivated(sessionService.get("email"), true);
        if(account.isPresent()){
            if(otp.equals(sessionService.get("otp"))){
              account.get().setPassword(new_password);
              accountDAO.save(account.get());
            }else{
                redirectAttributes.addFlashAttribute("error","sai otp");
                return "redirect:/forgot-Password";
            }

        }else {
            redirectAttributes.addFlashAttribute("error","Email không tồn tại");
            return "redirect:/forgot-Password";
        }
        redirectAttributes.addFlashAttribute("success","cập nhật thành công");
        return "redirect:/login?forgotpass";

    }

}