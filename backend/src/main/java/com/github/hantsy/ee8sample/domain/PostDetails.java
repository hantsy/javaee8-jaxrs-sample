/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.domain;

import com.github.hantsy.ee8sample.domain.Post.Status;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author hantsy
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDetails implements Serializable {

    private Long id;

    private Slug slug;

    private String title;

    private String content;

    private Status status;

    private boolean favorited = false;

    private int commentsCount = 0;

    private LocalDateTime createdDate;

    private String createdBy;

    private LocalDateTime lastModifiedDate;

    private String lastModifiedBy;

}
