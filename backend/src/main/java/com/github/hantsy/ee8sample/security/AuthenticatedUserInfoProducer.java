package com.github.hantsy.ee8sample.security;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;

/**
 *
 * @author hantsy
 */
@Dependent
public class AuthenticatedUserInfoProducer {

    @Produces
    @Authenticated
    private UserInfo currentUser;

    public void handleAuthenticationEvent(@Observes @Authenticated UserInfo authenticatedUser) {
        this.currentUser = authenticatedUser;
    }

}
