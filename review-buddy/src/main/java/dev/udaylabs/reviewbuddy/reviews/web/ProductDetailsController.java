package dev.udaylabs.reviewbuddy.reviews.web;

import dev.udaylabs.reviewbuddy.products.ProductApi;
import dev.udaylabs.reviewbuddy.products.models.ProductDto;
import dev.udaylabs.reviewbuddy.reviews.ai.ReviewSummaryService;
import dev.udaylabs.reviewbuddy.reviews.domain.ReviewService;
import dev.udaylabs.reviewbuddy.reviews.domain.SubmitReviewCmd;
import dev.udaylabs.reviewbuddy.shared.BadRequestException;
import dev.udaylabs.reviewbuddy.shared.ResourceNotFoundException;
import dev.udaylabs.reviewbuddy.users.UserApi;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Product details page lives in the reviews module: it depends on products (and users), never the
 * other way around. This keeps the module dependency graph acyclic.
 */
@Controller
class ProductDetailsController {

    private final ProductApi productApi;
    private final ReviewService reviewService;
    private final ReviewSummaryService reviewSummaryService;
    private final UserApi userApi;

    ProductDetailsController(
            ProductApi productApi,
            ReviewService reviewService,
            ReviewSummaryService reviewSummaryService,
            UserApi userApi) {
        this.productApi = productApi;
        this.reviewService = reviewService;
        this.reviewSummaryService = reviewSummaryService;
        this.userApi = userApi;
    }

    @GetMapping("/products/{slug}")
    String showProduct(@PathVariable String slug, Principal principal, Model model) {
        ProductDto product = getProduct(slug);
        populateModel(model, product, principal);
        model.addAttribute("form", ReviewForm.empty());
        return "products/details";
    }

    @PostMapping("/products/{slug}/reviews")
    String submitReview(
            @PathVariable String slug,
            @Valid @ModelAttribute("form") ReviewForm form,
            BindingResult result,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes) {
        ProductDto product = getProduct(slug);
        if (!result.hasErrors()) {
            Long userId = userApi.getByEmail(principal.getName()).id();
            try {
                reviewService.submitReview(
                        new SubmitReviewCmd(product.id(), userId, form.rating(), form.likes(), form.improvements()));
                redirectAttributes.addFlashAttribute("successMessage", "Thanks! Your review was published.");
                return "redirect:/products/" + slug + "#reviews";
            } catch (BadRequestException e) {
                result.reject("review.rejected", e.getMessage());
            }
        }
        populateModel(model, product, principal);
        return "products/details";
    }

    private ProductDto getProduct(String slug) {
        return productApi
                .findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + slug));
    }

    private void populateModel(Model model, ProductDto product, Principal principal) {
        model.addAttribute("product", product);
        model.addAttribute("reviews", reviewService.getReviews(product.id()));
        model.addAttribute(
                "summary", reviewSummaryService.findSummary(product.id()).orElse(null));
        model.addAttribute("minReviews", reviewSummaryService.minReviewsForSummary());
        boolean canReview = false;
        String reviewBlockedReason = "Log in to write a review.";
        if (principal != null) {
            Long userId = userApi.getByEmail(principal.getName()).id();
            if (userId.equals(product.submittedBy())) {
                reviewBlockedReason = "You submitted this product, so you cannot review it.";
            } else if (reviewService.hasReviewed(product.id(), userId)) {
                reviewBlockedReason = "You have already reviewed this product.";
            } else {
                canReview = true;
            }
        }
        model.addAttribute("canReview", canReview);
        model.addAttribute("reviewBlockedReason", reviewBlockedReason);
    }
}
