/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest;

import com.github.hantsy.ee8sample.Bootstrap;
import com.github.hantsy.ee8sample.Constants;
import com.github.hantsy.ee8sample.domain.support.AbstractEntity;
import com.github.hantsy.ee8sample.domain.User;
import com.github.hantsy.ee8sample.domain.repository.UserRepository;
import com.github.hantsy.ee8sample.rest.auth.AuthResource;
import com.github.hantsy.ee8sample.rest.user.RegisterForm;
import com.github.hantsy.ee8sample.rest.user.UserForm;
import com.github.hantsy.ee8sample.rest.user.UserResource;
import com.github.hantsy.ee8sample.security.Authenticated;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import static javax.ws.rs.client.Entity.form;
import static javax.ws.rs.client.Entity.json;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.http.HttpHeaders;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author hantsy
 */
@RunWith(Arquillian.class)
public class UserResourceIT {

    private static final Logger LOG = Logger.getLogger(UserResourceIT.class.getName());

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
            .addPackage(AbstractEntity.class.getPackage())
            //domain package.
            .addPackage(User.class.getPackage())
            // repository package.
            .addPackage(UserRepository.class.getPackage())
            //base classes
            .addPackage(Bootstrap.class.getPackage())
            //Add JAXRS resources classes
            .addPackage(UserResource.class.getPackage())
            .addPackage(AuthResource.class.getPackage())
            .addPackages(true, Authenticated.class.getPackage())
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

        final WebTarget targetAuth = client.target(URI.create(new URL(base, "api/auth/login").toExternalForm()));
        final Response resAuthGetAll = targetAuth.request()
            .accept(MediaType.APPLICATION_JSON)
            .post(form(new Form("username", "user").param("password", "password")));

        assertEquals(200, resAuthGetAll.getStatus());

        String token = resAuthGetAll.getHeaderString(HttpHeaders.AUTHORIZATION);

        client.register(new JwtTokenAuthentication(token.substring(Constants.AUTHORIZATION_PREFIX.length())));
    }

    @After
    public void teardown() throws MalformedURLException {
        client.close();
    }

    private static final String USERNAME = "test_user";
    private static final String PASSWORD = "test_password";

    @Test
    public void testGetUsersShouldBeOk() throws MalformedURLException {

        LOG.log(Level.INFO, "base url @{0}", base);

        //get all posts
        final WebTarget targetGetAll = client.target(URI.create(new URL(base, "api/users").toExternalForm()));
        try (Response resGetAll = targetGetAll.request().accept(MediaType.APPLICATION_JSON_TYPE).get()) {
            assertEquals(200, resGetAll.getStatus());
            List<User> results = resGetAll.readEntity(new GenericType<List<User>>() {
            });
            assertTrue(results != null);
            LOG.log(Level.INFO, "results.size()::{0}", results.size());
            assertTrue(results.size() == 2);
        }

    }

    @Test
    public void testCompleteUsersFlow() throws MalformedURLException {

        LOG.log(Level.INFO, "base url @{0}", base);

        //post
        final WebTarget targetCreateUser = client.target(URI.create(new URL(base, "api/users").toExternalForm()));
        RegisterForm post = RegisterForm.builder()
            .username(USERNAME)
            .password(PASSWORD)
            .firstName("test firstName")
            .lastName("test lastName")
            .email("test@example.com")
            .build();

        String location;
        try (Response resCreate = targetCreateUser.request().accept(MediaType.APPLICATION_JSON_TYPE).post(json(post))) {

            assertEquals(201, resCreate.getStatus());
            location = resCreate.getHeaderString("Location");

            assertNotNull(location);
        }

        //get
        final WebTarget targetGet = client.target(URI.create(new URL(base, location).toExternalForm()));
        try (Response resGet = targetGet.request().accept(MediaType.APPLICATION_JSON_TYPE).get()) {
            assertEquals(200, resGet.getStatus());
            User result = resGet.readEntity(User.class);
            assertTrue(result != null);
            assertEquals(USERNAME, result.getUsername());
            assertEquals("test@example.com", result.getEmail());
        }

        //update
        final WebTarget targetUpdateAll = client.target(URI.create(new URL(base, location).toExternalForm()));
         UserForm updateForm = UserForm.builder()
            .firstName("update firstName")
            .lastName("update lastName")
            .email("test@example.com")
            .build();

        try (Response resPutAll = targetUpdateAll.request().put(json(updateForm))) {
            assertEquals(204, resPutAll.getStatus());
        }

        //verifty update.
        //get
        final WebTarget targetVerifyUpdateGet = client.target(URI.create(new URL(base, location).toExternalForm()));
        try (Response resVerifyUpdateGet = targetVerifyUpdateGet.request().accept(MediaType.APPLICATION_JSON_TYPE).get()) {
            assertEquals(200, resVerifyUpdateGet.getStatus());
            User result = resVerifyUpdateGet.readEntity(User.class);
            assertTrue(result != null);
            assertEquals("update firstName", result.getFirstName());
            assertEquals("update lastName", result.getLastName());
        }


        //delete User
        final WebTarget targetDelUser = client.target(URI.create(new URL(base, location).toExternalForm()));

        try (Response resDel = targetDelUser.request().delete()) {
            assertEquals(204, resDel.getStatus());
        }
    }

}
