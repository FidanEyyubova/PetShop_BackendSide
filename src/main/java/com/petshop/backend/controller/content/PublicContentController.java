package com.petshop.backend.controller.content;

import com.petshop.backend.dto.request.content.ContactMessageRequest;
import com.petshop.backend.dto.request.content.NewsletterRequest;
import com.petshop.backend.dto.response.common.ApiResponse;
import com.petshop.backend.dto.response.common.BlogResponse;
import com.petshop.backend.dto.response.common.PageResponse;
import com.petshop.backend.dto.response.content.*;
import com.petshop.backend.service.content.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Site Content", description = "Public home-page content, blog, contact form and newsletter")
public class PublicContentController {

    private final HeroSectionService heroService;
    private final AboutSectionService aboutService;
    private final AdminServiceService siteServiceService;
    private final ContactInfoService contactInfoService;
    private final SiteSettingsService settingsService;
    private final BlogService blogService;
    private final BlogSubCategoryService blogCategoryService;
    private final ContactMessageService contactMessageService;
    private final NewsletterService newsletterService;

    public PublicContentController(HeroSectionService heroService, AboutSectionService aboutService,
                                   AdminServiceService siteServiceService, ContactInfoService contactInfoService,
                                   SiteSettingsService settingsService, BlogService blogService,
                                   BlogSubCategoryService blogCategoryService, ContactMessageService contactMessageService,
                                   NewsletterService newsletterService) {
        this.heroService = heroService;
        this.aboutService = aboutService;
        this.siteServiceService = siteServiceService;
        this.contactInfoService = contactInfoService;
        this.settingsService = settingsService;
        this.blogService = blogService;
        this.blogCategoryService = blogCategoryService;
        this.contactMessageService = contactMessageService;
        this.newsletterService = newsletterService;
    }

    @GetMapping("/hero")
    @Operation(summary = "List hero banners", description = "Active home-page hero banners.")
    public ResponseEntity<ApiResponse<List<HeroSectionResponse>>> hero() {
        return ResponseEntity.ok(ApiResponse.success("Hero fetched", heroService.getActiveHeroSections()));
    }

    @GetMapping("/about")
    @Operation(summary = "Get about content", description = "Blocks of the About section.")
    public ResponseEntity<ApiResponse<List<AboutSectionResponse>>> about() {
        return ResponseEntity.ok(ApiResponse.success("About fetched", aboutService.getAllAboutSections()));
    }

    @GetMapping("/site-services")
    @Operation(summary = "List home-page service items", description = "Active items of the \"Our services\" banner.")
    public ResponseEntity<ApiResponse<List<AdminServiceResponse>>> siteServices() {
        return ResponseEntity.ok(ApiResponse.success("Site services fetched", siteServiceService.getActiveSiteServices()));
    }

    @GetMapping("/contact-info")
    @Operation(summary = "Get contact info", description = "Address, email, phone, working hours and map URL.")
    public ResponseEntity<ApiResponse<ContactInfoResponse>> contactInfo() {
        return ResponseEntity.ok(ApiResponse.success("Contact info fetched", contactInfoService.getContactInfo()));
    }

    @GetMapping("/settings")
    @Operation(summary = "Get site settings", description = "Logo and social-media links.")
    public ResponseEntity<ApiResponse<SiteSettingsResponse>> settings() {
        return ResponseEntity.ok(ApiResponse.success("Settings fetched", settingsService.getSettings()));
    }

    @GetMapping("/blogs")
    @Operation(summary = "List blog posts", description = "Newest first; search title and excerpt; filter by category. Content is omitted in the list.")
    public ResponseEntity<ApiResponse<PageResponse<BlogResponse>>> blogs(
            @Parameter(description = "Category slug or id") @RequestParam(required = false) String category,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(ApiResponse.success("Blogs fetched", blogService.getBlogs(category, q, page, size)));
    }

    @GetMapping("/blogs/{idOrSlug}")
    @Operation(summary = "Get a blog post", description = "By numeric id or slug, including the full content.")
    public ResponseEntity<ApiResponse<BlogResponse>> blog(@PathVariable String idOrSlug) {
        return ResponseEntity.ok(ApiResponse.success("Blog fetched", blogService.getBlog(idOrSlug)));
    }

    @GetMapping("/blogs/{id}/related")
    @Operation(summary = "List related posts", description = "Same category first, then the newest posts.")
    public ResponseEntity<ApiResponse<List<BlogResponse>>> related(@PathVariable Long id, @RequestParam(defaultValue = "3") int limit) {
        return ResponseEntity.ok(ApiResponse.success("Related posts fetched", blogService.getRelated(id, limit)));
    }

    @GetMapping("/blog-categories")
    @Operation(summary = "List blog categories", description = "Categories with their post counts.")
    public ResponseEntity<ApiResponse<List<BlogSubCategoryResponse>>> blogCategories() {
        return ResponseEntity.ok(ApiResponse.success("Blog categories fetched", blogCategoryService.getAllBlogSubCategories()));
    }

    @PostMapping("/contact-messages")
    @Operation(summary = "Send a contact message", description = "Public contact form; rate limited per IP.")
    public ResponseEntity<ApiResponse<ContactMessageResponse>> contact(@Valid @RequestBody ContactMessageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Message sent", contactMessageService.sendContactMessage(request)));
    }

    @PostMapping("/newsletter/subscriptions")
    @Operation(summary = "Subscribe to the newsletter", description = "409 ALREADY_SUBSCRIBED when the email is already subscribed.")
    public ResponseEntity<ApiResponse<NewsletterSubscriberResponse>> subscribe(@Valid @RequestBody NewsletterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subscribed", newsletterService.subscribe(request.getEmail())));
    }

    @PostMapping("/newsletter/unsubscribe")
    @Operation(summary = "Unsubscribe from the newsletter", description = "Always succeeds.")
    public ResponseEntity<ApiResponse<Void>> unsubscribe(@Valid @RequestBody NewsletterRequest request) {
        newsletterService.unsubscribe(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Unsubscribed"));
    }
}
