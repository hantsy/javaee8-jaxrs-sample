package com.github.hantsy.ee8sample.domain;

import com.github.hantsy.ee8sample.Utils;
import static com.github.hantsy.ee8sample.domain.Post.Status.DRAFT;
import com.github.hantsy.ee8sample.domain.support.AbstractAuditableEntity;
import java.util.Comparator;
import java.util.function.Function;

import javax.persistence.Column;
import javax.persistence.Entity;
import static javax.persistence.EnumType.STRING;
import javax.persistence.Enumerated;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post extends AbstractAuditableEntity<Long> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static enum Status {
        DRAFT, PUBLISHED
    }

    private String slug;

    @Column(name = "title")
    @NotBlank
    private String title;

    @Column(name = "content")
    @NotBlank
    private String content;

    @Enumerated(STRING)
    private Status status = DRAFT;

    public static Comparator<Post> DEFAULT_COMPARATOR = Comparator
            .comparing(Post::getCreatedDate).reversed()
            .thenComparing(Post::getTitle);

    public static Function<Post, String> TO_STRING = p
            -> "Post[title:" + p.getTitle()
            + "\n slug:" + p.getSlug()
            + "\n content:" + p.getContent()
            + "\n status:" + p.getStatus()
            + "\n createdAt:" + p.getCreatedDate()
            + "\n lastModifiedAt:" + p.getLastModifiedDate()
            + "]";

    @PrePersist
    void beforeSave() {
        this.slug = Utils.slugify(this.title);
    }

}
