package com.phananhtai.shoppingOnline_service.controller.auth;

import com.phananhtai.shoppingOnline_service.dao.AccountDAO;
import com.phananhtai.shoppingOnline_service.dao.OderStatusDAO;
import com.phananhtai.shoppingOnline_service.dao.OrderDAO;
import com.phananhtai.shoppingOnline_service.dto.PasswordDTO;
import com.phananhtai.shoppingOnline_service.dto.ProductDTO;
import com.phananhtai.shoppingOnline_service.dto.UserDTO;
import com.phananhtai.shoppingOnline_service.dto.profileDTO;
import com.phananhtai.shoppingOnline_service.entity.Account;
import com.phananhtai.shoppingOnline_service.entity.Order;
import com.phananhtai.shoppingOnline_service.entity.OrderStatus;
import com.phananhtai.shoppingOnline_service.service.ImageStorageService;
import com.phananhtai.shoppingOnline_service.service.SessionService;
import com.phananhtai.shoppingOnline_service.utils.OrderUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class profileController {
    @Autowired
    SessionService sessionService;
    @Autowired
    AccountDAO accountDAO;
    @Autowired
    ImageStorageService imageStorageService;
    @Autowired
    OrderDAO orderDAO;
    @Autowired
    OderStatusDAO oderStatusDAO;
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
                               @RequestParam("username") String username,
                               @RequestParam("avatar") MultipartFile avatar) {
        Account account= accountDAO.findByUsernameAndActivated(username,true);
        if (account.getPhoto()==null) {
            String image = OrderUtils.generateImage();
            account.setPhoto(image+".png");
            try {
                imageStorageService.storeImageProfile(avatar, image);
            } catch (IOException e) {
                System.err.println("Error saving image: " + e.getMessage());
                throw new RuntimeException("Error saving image", e);
            }
        }else{
            try {
                imageStorageService.deleteImageProfile(account.getPhoto());
                String image = OrderUtils.generateImage();
                account.setPhoto(image+".png");
                imageStorageService.storeImageProfile(avatar, image);
            } catch (IOException e) {
                System.err.println("Error saving image: " + e.getMessage());
                throw new RuntimeException("Error saving image", e);
            }
        }
        accountDAO.save(account);

        return "redirect:/account/updateAvatar";
    }
    @ModelAttribute("OrderStatus")
    public List<OrderStatus> orderStatusList(){
        return oderStatusDAO.findAll();
    }
    @GetMapping("/account/order")
    public String showOrder(
            @RequestParam("keywords") Optional<String> keywords,
            @RequestParam("before") @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> before,
            @RequestParam("after") @DateTimeFormat(pattern = "yyyy-MM-dd") Optional<Date> after,
            @RequestParam("idOrderStatus") Optional<Long> idOrderStatus,
            @RequestParam("p") Optional<Integer> page,
            @RequestParam("orderBy") Optional<String> orderBy,
            Model model
    ) {
        Account account= sessionService.get("user");
        String kw = keywords.orElse("");
        int currentPage = page.orElse(0);
        int pageSize = 10;
        String orderByField = orderBy.orElse("id");
        Long orderStatus=idOrderStatus.orElse(1L);

        Date beforeDate = before.orElse(new Date(Long.MIN_VALUE));
        Date afterDate = after.orElse(new Date());


        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by(orderByField).ascending());
        Page<Order> orderPage = orderDAO.findByOrderStatusAndKeywordsContainingAndCreateDateBetweenAndUsername(orderStatus, kw,beforeDate, afterDate,account.getUsername(), pageable);

        model.addAttribute("orderPage", orderPage);
        model.addAttribute("keywords", kw);
        model.addAttribute("p", currentPage);
        model.addAttribute("idOrderStatus", idOrderStatus);
        model.addAttribute("orderBy", orderByField);
        model.addAttribute("start", beforeDate);
        model.addAttribute("end", afterDate);


        return "auth/order";
    }
}
