package com.phananhtai.shoppingOnline_service.config;

import com.phananhtai.shoppingOnline_service.dao.AccountDAO;
import com.phananhtai.shoppingOnline_service.entity.Account;
import com.phananhtai.shoppingOnline_service.service.CookieService;
import com.phananhtai.shoppingOnline_service.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
@Component
public class RememberListener implements HttpSessionListener  {
    private CookieService cookieService;
    private AccountDAO accountDAO;
    private SessionService sessionService;

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        cookieService = ApplicationContextProvider.getApplicationContext().getBean(CookieService.class);
        accountDAO = ApplicationContextProvider.getApplicationContext().getBean(AccountDAO.class);
        sessionService = ApplicationContextProvider.getApplicationContext().getBean(SessionService.class);

        Cookie tokenCookie = cookieService.get("token_access");
        if (tokenCookie != null) {
            String token = tokenCookie.getValue();
            Optional<Account> account = accountDAO.findByAccessToken(token);
            if (account.isPresent()) {
                System.out.println(account.get().getFullname());
                se.getSession().setAttribute("user",account.get());
            }
        }
    }




    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        // handle session destroyed event
    }

   
}
