/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest;

import com.github.hantsy.ee8sample.rest.auth.AuthResource;
import com.github.hantsy.ee8sample.Bootstrap;
import com.github.hantsy.ee8sample.Constants;
import static com.github.hantsy.ee8sample.Constants.AUTHORIZATION_PREFIX;
import com.github.hantsy.ee8sample.JaxrsActiviator;
import com.github.hantsy.ee8sample.Resources;
import com.github.hantsy.ee8sample.Utils;
import com.github.hantsy.ee8sample.domain.Post;
import com.github.hantsy.ee8sample.domain.repository.PostRepository;
import com.github.hantsy.ee8sample.security.UserInfo;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import static javax.ws.rs.client.Entity.form;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author hantsy
 */
@RunWith(Arquillian.class)
public class AuthResourceIT {

    private static final Logger LOG = Logger.getLogger(AuthResourceIT.class.getName());

    @Deployment(testable = false)
    public static WebArchive createDeployment() {

        File[] extraJars = Maven.resolver().loadPomFromFile("pom.xml")
            .resolve(
                "org.projectlombok:lombok:1.16.8",
                // "org.modelmapper:modelmapper:0.7.5",
                // "org.apache.commons:commons-lang3:3.4",
                // "com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.6.3",
                "io.jsonwebtoken:jjwt:0.8.0"
            )
            .withTransitivity()
            .asFile();

        final WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war")
            .addAsLibraries(extraJars)
            //domain package.
            .addPackages(true, Post.class.getPackage())
            // repository package.
            .addPackages(true, PostRepository.class.getPackage())
            //security
            .addPackages(true, UserInfo.class.getPackage())
            //base classes
            .addClasses(
                Bootstrap.class,
                JaxrsActiviator.class,
                PostDataInitializer.class,
                Constants.class,
                Resources.class,
                Utils.class
            )
            //Add JAXRS resources classes
            .addClasses(
                AuthResource.class
            )
            // .addAsResource("test-log4j.properties", "log4j.properties")
            //Add JPA persistence configration.
            //WARN: In a war package, persistence.xml should be put into /WEB-INF/classes/META-INF/, not /META-INF
            //.addAsManifestResource("META-INF/test-persistence.xml", "persistence.xml")
            .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml")
            // .addAsResource("META-INF/test-orm.xml", "META-INF/orm.xml")

            .addAsWebInfResource("test-web.xml", "web.xml")
            // Enable CDI
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        //  .addAsWebInfResource("test-jboss-deployment-structure.xml", "jboss-deployment-structure.xml");

        LOG.log(Level.INFO, "war to string @{0}", war.toString(true));
        return war;
    }

    @ArquillianResource
    private URL base;

    private Client client;

    @Before
    public void setup() throws MalformedURLException {
        client = ClientBuilder.newClient();
    }

    @After
    public void teardown() throws MalformedURLException {
        client.close();
    }

    private static final String TITLE = "test_title";
    private static final String CONTENT = "test_content";

    @Test
    public void testAuthWithUser() throws MalformedURLException {

        LOG.log(Level.INFO, "base url @{0}", base);

        //get an authentication
        final WebTarget targetAuth = client.target(URI.create(new URL(base, "api/auth/login").toExternalForm()));
        String token;
        try (Response resAuth = targetAuth.request().post(form(new Form().param("name", "user").param("password", "password")))) {
            assertEquals(200, resAuth.getStatus());
            token = (String) resAuth.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            LOG.info("resAuth.getHeaders().getFirst(\"Bearer\"):" + token);
            assertTrue(token != null);
        }

        client.register(new JwtTokenAuthentication(token.substring(AUTHORIZATION_PREFIX.length())));
        final WebTarget targetUser = client.target(URI.create(new URL(base, "api/auth/user").toExternalForm()));
        try (Response resUser = targetUser.request().accept(MediaType.APPLICATION_JSON_TYPE).get()) {
            assertEquals(200, resUser.getStatus());
            final UserInfo userInfo = resUser.readEntity(UserInfo.class);
            LOG.log(Level.INFO, "get user info @" + userInfo);
            assertTrue("user".equals(userInfo.getName()));
        }

    }

}
