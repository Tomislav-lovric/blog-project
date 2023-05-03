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
@Table(name = "post_tags")
public class PostTag {

    @EmbeddedId
    private PostTagId postTagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(
            name = "post_id",
            foreignKey = @ForeignKey(
                    name = "post_tags_post_id_fk"
            )
    )
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    @JoinColumn(
            name = "tag_id",
            foreignKey = @ForeignKey(
                    name = "post_tags_tag_id_fk"
            )
    )
    private Tag tag;

}
