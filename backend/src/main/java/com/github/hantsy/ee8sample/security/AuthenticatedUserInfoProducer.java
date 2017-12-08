package com.github.hantsy.ee8sample.security;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 *
 * @author hantsy
 */
@RequestScoped
public class AuthenticatedUserInfoProducer {

    @Inject
    Logger LOG;

    private UserInfo currentUser;

    @Produces
    @Authenticated
    public UserInfo getUserInfo() {
        return this.currentUser;
    }

    public void handleAuthenticationEvent(@Observes @Authenticated UserInfo authenticatedUser) {
        LOG.log(Level.INFO, "handling authentication event:{0}", authenticatedUser);
        this.currentUser = authenticatedUser;
    }

}
