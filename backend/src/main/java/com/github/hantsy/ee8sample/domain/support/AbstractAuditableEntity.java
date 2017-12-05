/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.domain.support;

import com.github.hantsy.ee8sample.domain.Username;
import java.time.LocalDateTime;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author hantsy
 */
@MappedSuperclass
@Setter
@Getter
//@EntityListeners(AuditEntityListener.class)
public class AbstractAuditableEntity extends AbstractEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Column(name = "created_at")
    private LocalDateTime createdDate;

    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedDate;

//    @ManyToOne()
//    @JoinColumn(name="created_by")
//    @Column(name = "created_by")
    @AttributeOverride(name = "username", column = @Column(name = "created_by"))
    private Username createdBy;

//    @ManyToOne()
//    @JoinColumn(name="updated_by")
//    @Column(name = "last_modified_by")
    @AttributeOverride(name = "username", column = @Column(name = "last_modified_by"))
    private Username lastModifiedBy;
}
