/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.security.identitystore;

import com.github.hantsy.ee8sample.repository.UserRepository;
import com.github.hantsy.ee8sample.security.hash.Crypto;
import static com.github.hantsy.ee8sample.security.hash.Crypto.Type.BCRYPT;
import com.github.hantsy.ee8sample.security.hash.PasswordEncoder;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import static javax.security.enterprise.identitystore.CredentialValidationResult.INVALID_RESULT;
import static javax.security.enterprise.identitystore.CredentialValidationResult.NOT_VALIDATED_RESULT;
import javax.security.enterprise.identitystore.IdentityStore;

/**
 *
 * @author hantsy
 */
@RequestScoped
public class JpaIdentityStore implements IdentityStore {

    @Inject
    private Logger LOG;

    @Inject
    private UserRepository users;

    @Inject
    @Crypto(BCRYPT)
    private PasswordEncoder passwordHash;

    @PostConstruct
    public void init() {
        LOG.log(Level.INFO, "initializing JpaIdentityStore...");
    }

    @Override
    public CredentialValidationResult validate(Credential credential) {
        CredentialValidationResult result;

        if (credential instanceof UsernamePasswordCredential) {
            UsernamePasswordCredential usernamePassword = (UsernamePasswordCredential) credential;

            result = users.findByUsername(usernamePassword.getCaller())
                .map(
                    u -> passwordHash.matches(new String(usernamePassword.getPassword().getValue()), u.getPassword())
                    ? new CredentialValidationResult(usernamePassword.getCaller(), u.getAuthorities())
                    : INVALID_RESULT
                )
                .orElse(INVALID_RESULT);

        } else {
            result = NOT_VALIDATED_RESULT;
        }
        return result;
    }

    @Override
    public Set<String> getCallerGroups(CredentialValidationResult validationResult) {
//        return users.findByUsername(validationResult.getCallerPrincipal().getName())
//                .map(user -> user.getAuthorities())
//                .orElse(emptySet());
        return validationResult.getCallerGroups();

    }

}
