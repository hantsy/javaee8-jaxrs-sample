/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.security;

import static java.util.Arrays.asList;
import java.util.HashSet;
import java.util.Set;
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
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    private String name;
    private Set<String> roles = new HashSet<>();

//    public boolean isAuthenticated() {
//        return this.name != null;
//    }

    public boolean hasRole(String _role) {
        return this.roles.contains(_role);
    }

    public boolean hasAnyRoles(String... _roles) {
        return this.roles.stream().anyMatch(c -> asList(_roles).contains(c));
    }

}
