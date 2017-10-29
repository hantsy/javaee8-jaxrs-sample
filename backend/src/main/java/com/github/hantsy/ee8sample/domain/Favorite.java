/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.domain;

import com.github.hantsy.ee8sample.domain.support.AbstractEntity;
import java.time.LocalDateTime;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author hantsy
 */
@Entity
@Table(name = "favorites")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Favorite extends AbstractEntity {

    @Embedded
    @AttributeOverride(name = "slug", column = @Column(name = "post_slug"))
    private Slug post;

    private LocalDateTime createdDate;

    @Embedded
    //@AttributeOverride(name = "username", column = @Column(name = "user"))
    private Username user;
}
