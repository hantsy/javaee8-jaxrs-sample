/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample;

import static com.github.hantsy.ee8sample.Constants.API_PREFIX;
import static com.github.hantsy.ee8sample.Constants.ROLE_ADMIN;
import static com.github.hantsy.ee8sample.Constants.ROLE_USER;
import javax.annotation.security.DeclareRoles;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 * @author hantsy
 */
@DeclareRoles({ROLE_USER, ROLE_ADMIN})
@ApplicationPath(API_PREFIX)
public class JaxrsActiviator extends Application {
    
}
