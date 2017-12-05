/**
 *  The JWT authentication implementation is based on:
 *  https://github.com/javaee-samples/javaee8-samples
 * 
 * <ul>
 *  <li> Replace the dummy IdentityStore with JPA backend IdentityStore.
 *  <li> When the authentication is successful, fire an @Anthenticated CDI event. 
 * </ul>
 */
package com.github.hantsy.ee8sample.security.jwt;
