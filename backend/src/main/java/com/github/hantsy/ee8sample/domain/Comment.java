/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.domain;

import com.github.hantsy.ee8sample.domain.support.AbstractAuditableEntity;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author hantsy
 */
@Entity
@Table(name = "comments")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends AbstractAuditableEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "content")
    @NotBlank
    @NonNull
    private String content;

    @Embedded
    @AttributeOverride(name = "slug", column = @Column(name = "post_slug"))
    private Slug post;

}
