package dev.udaylabs.reviewbuddy.products.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import dev.udaylabs.reviewbuddy.AbstractIntegrationTest;
import dev.udaylabs.reviewbuddy.products.models.ProductDto;
import dev.udaylabs.reviewbuddy.shared.PagedResult;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;

class ProductControllerTests extends AbstractIntegrationTest {

    @Test
    void anonymousUserCanBrowsePaginatedProducts() throws Exception {
        MvcResult result = mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"))
                .andReturn();

        PagedResult<ProductDto> page = products(result);
        assertThat(page.data()).hasSize(9);
        assertThat(page.pageNumber()).isEqualTo(1);
        assertThat(page.totalPages()).isGreaterThanOrEqualTo(2);
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    void secondPageContainsRemainingProducts() throws Exception {
        MvcResult result = mockMvc.perform(get("/products").param("page", "2")).andReturn();
        PagedResult<ProductDto> page = products(result);
        assertThat(page.pageNumber()).isEqualTo(2);
        assertThat(page.hasPrevious()).isTrue();
    }

    @Test
    void searchesByNameOrTaglineCaseInsensitive() throws Exception {
        MvcResult result = mockMvc.perform(get("/products").param("q", "PIXEL")).andReturn();
        assertThat(products(result).data()).extracting(ProductDto::slug).contains("pixelpress");
    }

    @Test
    void filtersByCategory() throws Exception {
        MvcResult result =
                mockMvc.perform(get("/products").param("category", "ai")).andReturn();
        assertThat(products(result).data())
                .isNotEmpty()
                .allSatisfy(p -> assertThat(p.category().slug()).isEqualTo("ai"));
    }

    @Test
    void sortsByTopRated() throws Exception {
        MvcResult result =
                mockMvc.perform(get("/products").param("sort", "top-rated")).andReturn();
        var ratings =
                products(result).data().stream().map(ProductDto::averageRating).toList();
        assertThat(ratings).isSortedAccordingTo((a, b) -> Double.compare(b, a));
    }

    @Test
    void sortsByMostReviewed() throws Exception {
        MvcResult result =
                mockMvc.perform(get("/products").param("sort", "most-reviewed")).andReturn();
        var counts =
                products(result).data().stream().map(ProductDto::reviewCount).toList();
        assertThat(counts).isSortedAccordingTo((a, b) -> Integer.compare(b, a));
    }

    @Test
    void submitProductPageRequiresLogin() throws Exception {
        mockMvc.perform(get("/products/new")).andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithUserDetails("user@gmail.com")
    void loggedInUserCanSubmitProduct() throws Exception {
        mockMvc.perform(get("/products/new")).andExpect(status().isOk()).andExpect(view().name("products/new"));

        mockMvc.perform(post("/products")
                        .with(csrf())
                        .param("name", "Spring Agent Kit")
                        .param("tagline", "Agent skills for Spring Boot")
                        .param("description", "A curated set of agent skills.")
                        .param("websiteUrl", "https://example.com/agent-kit")
                        .param("imageUrl", "")
                        .param("categoryId", "1"))
                .andExpect(redirectedUrl("/products/spring-agent-kit"));

        mockMvc.perform(get("/products/spring-agent-kit"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Agent skills for Spring Boot")))
                .andExpect(content().string(containsString("by Demo User")));
    }

    @Test
    @WithUserDetails("user@gmail.com")
    void duplicateNamesGetUniqueSlugs() throws Exception {
        for (String expected : new String[] {"/products/twin-tool", "/products/twin-tool-2"}) {
            mockMvc.perform(post("/products")
                            .with(csrf())
                            .param("name", "Twin Tool")
                            .param("tagline", "Same name")
                            .param("description", "Same name twice")
                            .param("websiteUrl", "https://example.com")
                            .param("categoryId", "1"))
                    .andExpect(redirectedUrl(expected));
        }
    }

    @Test
    @WithUserDetails("user@gmail.com")
    void reservedSlugIsAvoided() throws Exception {
        mockMvc.perform(post("/products")
                        .with(csrf())
                        .param("name", "New")
                        .param("tagline", "Named new")
                        .param("description", "Would clash with /products/new")
                        .param("websiteUrl", "https://example.com")
                        .param("categoryId", "1"))
                .andExpect(redirectedUrl("/products/product-new"));
    }

    @Test
    @WithUserDetails("user@gmail.com")
    void validatesProductForm() throws Exception {
        mockMvc.perform(post("/products")
                        .with(csrf())
                        .param("name", "")
                        .param("tagline", "")
                        .param("description", "")
                        .param("websiteUrl", "ftp://bad")
                        .param("imageUrl", "not-a-url"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/new"))
                .andExpect(model().attributeHasFieldErrors(
                                "form", "name", "tagline", "description", "websiteUrl", "imageUrl", "categoryId"));
    }

    @Test
    void unknownProductReturns404() throws Exception {
        mockMvc.perform(get("/products/does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/404"));
    }

    @SuppressWarnings("unchecked")
    private static PagedResult<ProductDto> products(MvcResult result) {
        return (PagedResult<ProductDto>) result.getModelAndView().getModel().get("products");
    }
}
