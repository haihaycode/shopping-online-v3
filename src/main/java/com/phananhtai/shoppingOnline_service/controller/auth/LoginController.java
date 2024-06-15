package com.phananhtai.shoppingOnline_service.controller.auth;

import com.phananhtai.shoppingOnline_service.dao.AccountDAO;
import com.phananhtai.shoppingOnline_service.entity.Account;
import com.phananhtai.shoppingOnline_service.service.CookieService;
import com.phananhtai.shoppingOnline_service.service.SessionService;
import com.phananhtai.shoppingOnline_service.utils.OrderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;
import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private AccountDAO accountDAO;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private MessageSource messageSource;
    @Autowired
    CookieService cookieService;

    @GetMapping("/login")
    public String showLogin() {
        return "auth/login";
    }

    @PostMapping("/login")
    public ModelAndView handleLogin(@RequestParam("username") String username,
                                    @RequestParam("password") String password,
                                    @RequestParam("rememberme") Optional<Boolean> rememberme,
                                    RedirectAttributes redirectAttributes,
                                    Locale locale) {
        ModelAndView mav = new ModelAndView("auth/login");
        Account account = accountDAO.findByUsernameAndActivated(username, true);

        boolean remember =rememberme.orElse(false);

        if (account == null) {
            mav.addObject("error", messageSource.getMessage("login.account.notfound", null, locale));
        } else if (!account.getPassword().equals(password)) {
            mav.addObject("error", messageSource.getMessage("login.error", null, locale));
        } else {
            if(remember){
                String token = OrderUtils.generateToken(account.getUsername().trim());
                account.setAccessToken(token);
                accountDAO.save(account);
                cookieService.add("token_access",token,10);

            }
            sessionService.set("user", account);
            redirectAttributes.addFlashAttribute("success", messageSource.getMessage("login.success", null, locale));
            return new ModelAndView("redirect:/");

        }

        return mav;
    }
}
