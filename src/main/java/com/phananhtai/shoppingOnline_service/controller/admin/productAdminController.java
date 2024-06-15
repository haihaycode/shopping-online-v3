package com.phananhtai.shoppingOnline_service.controller.admin;

import com.phananhtai.shoppingOnline_service.dao.CategoryDAO;
import com.phananhtai.shoppingOnline_service.dao.ProductDAO;
import com.phananhtai.shoppingOnline_service.dto.ProductDTO;
import com.phananhtai.shoppingOnline_service.entity.Category;
import com.phananhtai.shoppingOnline_service.entity.Product;
import com.phananhtai.shoppingOnline_service.service.ImageStorageService;
import com.phananhtai.shoppingOnline_service.utils.OrderUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Controller
public class productAdminController {
    @Autowired
    ProductDAO productDAO;
    @Autowired
    ImageStorageService imageStorageService;
    @Autowired
    CategoryDAO categoryDAO;


    @GetMapping("/admin/product")
    public String showProducts(
            @RequestParam("keywords") Optional<String> keywords,
            @RequestParam("category") Optional<Integer> categoryId,
            @RequestParam("p") Optional<Integer> p,
            @RequestParam("orderByPrice") Optional<Integer> orderByPrice,
            @RequestParam("active") Optional<Boolean> active,
            Model model
    ) {
        int totalProduct = productDAO.findAll().size();
        int totalProductActive = productDAO.getAllByActive(true).size();

        String kw = keywords.orElse("");
        Integer catId = categoryId.orElse(null);
        Boolean orderByPriceAsc = orderByPrice.orElse(1) == 1;
        Boolean actives = active.orElse(true);

        Pageable pageable = PageRequest.of(p.orElse(0), 12);
        Page<Product> products;
        if (catId != null) {
            products = orderByPriceAsc ?
                    productDAO.findByKeywordsAndActiveAndCategoryOrderByPriceAsc(kw, actives, catId, pageable) :
                    productDAO.findByKeywordsAndActiveAndCategoryOrderByPriceDesc(kw, actives, catId, pageable);
        } else {
            products = orderByPriceAsc ?
                    productDAO.findByKeywordsAndActiveOrderByPriceAsc(kw, actives , pageable) :
                    productDAO.findByKeywordsAndActiveOrderByPriceDesc(kw, actives, pageable);
        }

        model.addAttribute("page", products);
        model.addAttribute("kw", kw);
        model.addAttribute("categoryId", catId);
        model.addAttribute("orderByPrice", orderByPriceAsc);
        model.addAttribute("totalProducts", totalProduct);
        model.addAttribute("totalProductActive", totalProductActive);

        return "admin/product/product";
    }
    @GetMapping("/admin/product/add")
    public String showAddProduct(Model model) {
        model.addAttribute("productNew", new ProductDTO());
        return "/admin/product/addProduct";
    }


    @PostMapping("/admin/product/add")
    public String addProduct(@Valid @ModelAttribute("productNew") ProductDTO product, BindingResult result,Model model,RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "/admin/product/addProduct";
        }

        Product newProduct = new Product();
        categoryDAO.findByIdAndActive(Integer.toString(product.getIdCategory()), true).ifPresent(newProduct::setCategory);

        newProduct.setName(product.getName());
        newProduct.setActive(product.getActive());
        newProduct.setDescription(product.getDescription());
        newProduct.setPrice(product.getPrice());
        newProduct.setCreateDate(new Date());

        if (!product.getPhoto().isEmpty()) {
            String image = OrderUtils.generateImage();
            newProduct.setImage(image+".png");
            try {
                imageStorageService.storeImage(product.getPhoto(), image);
            } catch (IOException e) {
                System.err.println("Error saving image: " + e.getMessage());
                throw new RuntimeException("Error saving image", e);
            }
        }
        productDAO.save(newProduct);
        redirectAttributes.addFlashAttribute("success", "Thêm sản phẩm thành công");
        return "redirect:/admin/product";
    }

    @GetMapping("/admin/product/update/{id}")
    public String showUpdateProduct(@PathVariable("id") int productId, Model model) {
        Optional<Product> productOptional = productDAO.findById(productId);

        if (productOptional.isPresent()) {
            ProductDTO productDTO= new ProductDTO();
            productDTO.setId(productOptional.get().getId());
            productDTO.setName(productOptional.get().getName());
            productDTO.setPrice(productOptional.get().getPrice());
            productDTO.setIdCategory(Integer.parseInt(productOptional.get().getCategory().getId()));
            productDTO.setDescription(productOptional.get().getDescription());
            productDTO.setActive(productOptional.get().getActive());
            productDTO.setImage(productOptional.get().getImage());
            model.addAttribute("productNew", productDTO);

        } else {
            model.addAttribute("productNew", new ProductDTO());
            // Nếu không tồn tại, ném ra một ngoại lệ hoặc thực hiện xử lý phù hợp
            model.addAttribute("danger", "Product not found with id: " + productId);
        }

        return "/admin/product/updateProduct";
    }

    @PostMapping("/admin/product/update")
    public String updateProduct(@Valid @ModelAttribute("productNew") ProductDTO product, BindingResult result, Model model,RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "/admin/product/updateProduct";
        }

        Product newProduct = productDAO.getProductById(product.getId());
        if (newProduct == null) {
            redirectAttributes.addFlashAttribute("danger", "id not found");
            return "redirect:/admin/product?error=true";
        }

        categoryDAO.findByIdAndActive(Integer.toString(product.getIdCategory()), true).ifPresent(newProduct::setCategory);
        newProduct.setName(product.getName());
        newProduct.setActive(product.getActive());
        newProduct.setDescription(product.getDescription());
        newProduct.setPrice(product.getPrice());
        newProduct.setCreateDate(new Date());

        if (!product.getPhoto().isEmpty()) {
            String image = OrderUtils.generateImage();

            try {
                imageStorageService.storeImage(product.getPhoto(), image);
                if (newProduct.getImage() != null) {
                    imageStorageService.deleteImage(newProduct.getImage());
                }
                newProduct.setImage(image + ".png");
            } catch (IOException e) {
                System.err.println("Error saving image: " + e.getMessage());
                throw new RuntimeException("Error saving image", e);
            }
        } else {
            newProduct.setImage(newProduct.getImage());
        }

        productDAO.save(newProduct);
        redirectAttributes.addFlashAttribute("success", "Cập nhật thành công");
        return "redirect:/admin/product";
    }


    @GetMapping("/admin/product/active/{id}")
    public String activeProduct(@PathVariable("id") int id, @RequestParam("active") Boolean active, Model model, RedirectAttributes redirectAttributes) {
        Product product = productDAO.getProductById(id);

        if (product == null) {
            redirectAttributes.addFlashAttribute("danger", "Product with ID " + id + " not found.");
            return "redirect:/admin/product?error=true";
        }

        product.setActive(active);
        productDAO.save(product);
        redirectAttributes.addFlashAttribute("success", "Cập nhật thành công");
        return "redirect:/admin/product?active=" + active;
    }



}
