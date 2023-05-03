package com.example.blog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "post_categories")
public class PostCategory {

    @EmbeddedId
    private PostCategoryId postCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(
            name = "post_id",
            foreignKey = @ForeignKey(
                    name = "post_categories_post_id_fk"
            )
    )
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId")
    @JoinColumn(
            name = "category_id",
            foreignKey = @ForeignKey(
                    name = "post_categories_category_id_fk"
            )
    )
    private Category category;

}
