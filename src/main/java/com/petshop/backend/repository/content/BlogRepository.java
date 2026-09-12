package com.petshop.backend.repository.content;

import com.petshop.backend.entity.content.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Long> {
    Optional<Blog> findBySlug(String slug);
    List<Blog> findByTagsContaining(String tag);
}
