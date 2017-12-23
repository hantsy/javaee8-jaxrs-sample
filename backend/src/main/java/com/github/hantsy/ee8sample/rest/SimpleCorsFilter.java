/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author hantsy
 */
@Provider
@PreMatching
public class SimpleCorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private final static Logger LOG = Logger.getLogger(SimpleCorsFilter.class.getName());

    final static String DEFAULT_ALLOW_METHODS = "GET,POST,PUT,DELETE,OPTIONS,HEAD";
    final static String DEFAULT_ALLOW_HEADERS = "origin,content-type,accept,authorization";
    final static int MAX_AGE = 24 * 60 * 60;

    @Override
    public void filter(ContainerRequestContext requestCtx) throws IOException {
        LOG.info("...entering SimpleCorsFilter request filter.");

        // When HttpMethod comes as OPTIONS, just acknowledge that it accepts...
        if (requestCtx.getRequest().getMethod().equals("OPTIONS")) {
            LOG.info("HTTP Method (OPTIONS) - Detected!");

            // Just send a OK signal back to the browser
            requestCtx.abortWith(Response.status(Response.Status.OK).build());
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        LOG.info("...entering SimpleCorsFilter response filter.");

        // if (isPreflightRequest(requestContext)) {
        //LOG.info("...handling preflight request.");
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");

        responseContext.getHeaders().add("Access-Control-Allow-Headers", createRequestedHeaders(requestContext));
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");

        responseContext.getHeaders().add("Access-Control-Max-Age", MAX_AGE);
        responseContext.getHeaders().add("Access-Control-Allow-Methods", DEFAULT_ALLOW_METHODS);
        //}

    }

    private boolean isPreflightRequest(ContainerRequestContext requestContext) {
        return requestContext.getRequest().getMethod().equals("OPTIONS");
    }

    private String createRequestedHeaders(ContainerRequestContext requestContext) {
        String headers = requestContext.getHeaderString("Access-Control-Request-Headers");
        LOG.log(Level.INFO, "Access-Control-Request-Headers:{0}", headers);
        return headers != null ? headers : DEFAULT_ALLOW_HEADERS;
    }
}
