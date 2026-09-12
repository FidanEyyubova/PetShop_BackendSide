package com.petshop.backend.dto.request.common;

import jakarta.validation.constraints.NotBlank;

public class CreateBlogRequest {
    @NotBlank(message = "MÉ™qalÉ™ baÅŸlÄ±ÄŸÄ± boÅŸ ola bilmÉ™z")
    private String title;

    private String excerpt;

    @NotBlank(message = "MÉ™qalÉ™ mÉ™tni mÃ¼tlÉ™qdir")
    private String content;

    private String coverImage;
    private String tags;
    private String readTime;

    public CreateBlogRequest() {
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getExcerpt() { return excerpt; }
    public void setExcerpt(String excerpt) { this.excerpt = excerpt; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getReadTime() { return readTime; }
    public void setReadTime(String readTime) { this.readTime = readTime; }
}