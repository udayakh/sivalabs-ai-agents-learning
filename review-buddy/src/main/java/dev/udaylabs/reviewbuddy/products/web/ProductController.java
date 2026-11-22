package dev.udaylabs.reviewbuddy.products.web;

import dev.udaylabs.reviewbuddy.products.domain.ProductService;
import dev.udaylabs.reviewbuddy.products.models.CreateProductCmd;
import dev.udaylabs.reviewbuddy.products.models.ProductDto;
import dev.udaylabs.reviewbuddy.products.models.ProductSearchCriteria;
import dev.udaylabs.reviewbuddy.products.models.ProductSort;
import dev.udaylabs.reviewbuddy.users.UserApi;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
class ProductController {

    private final ProductService productService;
    private final UserApi userApi;

    ProductController(ProductService productService, UserApi userApi) {
        this.productService = productService;
        this.userApi = userApi;
    }

    @GetMapping("/")
    String home() {
        return "redirect:/products";
    }

    @GetMapping("/products")
    String listProducts(
            @RequestParam(name = "q", defaultValue = "") String query,
            @RequestParam(name = "category", defaultValue = "") String category,
            @RequestParam(name = "sort", defaultValue = "newest") String sort,
            @RequestParam(name = "page", defaultValue = "1") int page,
            Model model) {
        var criteria = new ProductSearchCriteria(query, category, ProductSort.fromCode(sort), page);
        model.addAttribute("products", productService.searchProducts(criteria));
        model.addAttribute("categories", productService.getCategories());
        model.addAttribute("sortOptions", ProductSort.values());
        model.addAttribute("criteria", criteria);
        return "products/list";
    }

    @GetMapping("/products/new")
    String newProductForm(Model model) {
        model.addAttribute("form", ProductForm.empty());
        model.addAttribute("categories", productService.getCategories());
        return "products/new";
    }

    @PostMapping("/products")
    String createProduct(
            @Valid @ModelAttribute("form") ProductForm form, BindingResult result, Principal principal, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", productService.getCategories());
            return "products/new";
        }
        Long userId = userApi.getByEmail(principal.getName()).id();
        ProductDto product = productService.createProduct(new CreateProductCmd(
                form.name(),
                form.tagline(),
                form.description(),
                form.websiteUrl(),
                form.imageUrl(),
                form.categoryId(),
                userId));
        return "redirect:/products/" + product.slug();
    }
}
