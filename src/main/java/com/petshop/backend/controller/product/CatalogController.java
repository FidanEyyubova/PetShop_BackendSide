package com.petshop.backend.controller.product;

import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.product.CategoryResponse;
import com.petshop.backend.dto.response.product.PetTypeResponse;
import com.petshop.backend.dto.response.product.SubCategoryResponse;
import com.petshop.backend.service.product.CategoryService;
import com.petshop.backend.service.product.SubCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Catalogue", description = "Public product categories, subcategories and pet types (with product counts)")
public class CatalogController {

    private final CategoryService categoryService;
    private final SubCategoryService subCategoryService;

    public CatalogController(CategoryService categoryService, SubCategoryService subCategoryService) {
        this.categoryService = categoryService;
        this.subCategoryService = subCategoryService;
    }

    @GetMapping("/categories")
    @Operation(summary = "List categories", description = "Product categories with subcategories and product counts.")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> categories(
            @Parameter(description = "PRODUCT (default: all)") @RequestParam(required = false) String type) {
        return ResponseEntity.ok(ApiResponse.success("Categories fetched", categoryService.getPublicCategories(type)));
    }

    @GetMapping("/categories/{idOrSlug}")
    @Operation(summary = "Get a category", description = "By numeric id or slug.")
    public ResponseEntity<ApiResponse<CategoryResponse>> category(@PathVariable String idOrSlug) {
        return ResponseEntity.ok(ApiResponse.success("Category fetched", categoryService.getCategoryByIdOrSlug(idOrSlug)));
    }

    @GetMapping("/sub-categories")
    @Operation(summary = "List subcategories", description = "All subcategories, or those of one category.")
    public ResponseEntity<ApiResponse<List<SubCategoryResponse>>> subCategories(@RequestParam(required = false) Long categoryId) {
        List<SubCategoryResponse> list = categoryId == null
                ? subCategoryService.getAllSubCategories()
                : subCategoryService.getSubCategoriesByCategoryId(categoryId);
        return ResponseEntity.ok(ApiResponse.success("Subcategories fetched", list));
    }

    @GetMapping("/pet-types")
    @Operation(summary = "List pet types", description = "dogs, cats, birds, fish, small-pets, reptiles with English/Azerbaijani names and product counts.")
    public ResponseEntity<ApiResponse<List<PetTypeResponse>>> petTypes() {
        return ResponseEntity.ok(ApiResponse.success("Pet types fetched", categoryService.getPetTypes()));
    }
}
