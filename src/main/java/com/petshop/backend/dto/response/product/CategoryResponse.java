package com.petshop.backend.dto.response.product;

public class CategoryResponse {
    private Long id;
    private String name;
    private String slug;
    private String imageUrl;
    private String type;

    public CategoryResponse() {
    }

    public CategoryResponse(Long id, String name, String slug, String imageUrl, String type) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.imageUrl = imageUrl;
        this.type = type;
    }

    public static CategoryResponseBuilder builder() {
        return new CategoryResponseBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public static class CategoryResponseBuilder {
        private Long id;
        private String name;
        private String slug;
        private String imageUrl;
        private String type;

        CategoryResponseBuilder() {
        }

        public CategoryResponseBuilder id(Long id) { this.id = id; return this; }
        public CategoryResponseBuilder name(String name) { this.name = name; return this; }
        public CategoryResponseBuilder slug(String slug) { this.slug = slug; return this; }
        public CategoryResponseBuilder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public CategoryResponseBuilder type(String type) { this.type = type; return this; }

        public CategoryResponse build() {
            return new CategoryResponse(id, name, slug, imageUrl, type);
        }
    }
}