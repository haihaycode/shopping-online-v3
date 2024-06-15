package com.phananhtai.shoppingOnline_service.controller.admin;

import com.phananhtai.shoppingOnline_service.dao.CategoryDAO;
import com.phananhtai.shoppingOnline_service.entity.Category;
import com.phananhtai.shoppingOnline_service.entity.Product;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class categoriesAdminController {

    @Autowired
    CategoryDAO categoryDAO;

    private void loadCategoryPage(Model model) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<Category> categoryPage = categoryDAO.findByActiveAndNameContaining(true, "", pageable);
        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("keywords", "");
        model.addAttribute("active", true);
        model.addAttribute("orderBy", "id");
        model.addAttribute("categoryPage", categoryPage);
    }

    @GetMapping("/admin/categories")
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
        Page<Category> categoryPage = categoryDAO.findByActiveAndNameContaining(ac, kw, pageable);

        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("keywords", kw);
        model.addAttribute("active", ac);
        model.addAttribute("orderBy", orderByField);

        model.addAttribute("category", new Category());

        return "admin/category/category";
    }

    @GetMapping("/admin/categories/edit/{id}")
    public String showUpdateForm(@PathVariable("id") String id, Model model) {
        loadCategoryPage(model);
        Optional<Category> category = categoryDAO.findById(id);
        if (category.isPresent()){
            model.addAttribute("category", category.get());
        }else{
            model.addAttribute("danger"," id not found");
            model.addAttribute("category", new Category());
        }
        return "admin/category/category";
    }

    @PostMapping("/admin/categories")
    public String saveCategory(@Valid @ModelAttribute("category") Category category,
                               BindingResult bindingResult, Model model, @RequestParam("status") String status,RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            loadCategoryPage(model);
            return "admin/category/category"; // Nếu có lỗi, quay lại form nhập liệu
        }
        if ("add".equals(status)) {
            category.setId(OrderUtils.generateUniqueId());
        }
        redirectAttributes.addFlashAttribute("success", "Cập nhật thành công");
        categoryDAO.save(category);
        return "redirect:/admin/categories"; // Chuyển hướng sau khi thêm thành công
    }

    @GetMapping("/admin/categories/active/{id}")
    public String activeProduct(@PathVariable("id") String id, @RequestParam("active") Boolean active, RedirectAttributes redirectAttributes) {
        Category category = categoryDAO.getReferenceById(id);
        if(category==null){
            redirectAttributes.addFlashAttribute("danger", "Không tìm thấy id :"+id);
        }else{
            category.setActive(active);
            categoryDAO.save(category);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thành công");
        }

        return "redirect:/admin/categories?active="+active;
    }
}
