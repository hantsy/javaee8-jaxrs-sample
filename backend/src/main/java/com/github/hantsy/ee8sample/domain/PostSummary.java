/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.domain;

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
public class PostSummary {

    private Long id;

    private String slug;

    private String title;

    private int commentsCount;
}
