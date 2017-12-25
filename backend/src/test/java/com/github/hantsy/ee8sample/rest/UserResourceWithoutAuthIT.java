/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest;

import com.github.hantsy.ee8sample.Bootstrap;
import com.github.hantsy.ee8sample.domain.Count;
import com.github.hantsy.ee8sample.domain.Existed;
import com.github.hantsy.ee8sample.domain.support.AbstractEntity;
import com.github.hantsy.ee8sample.domain.User;
import com.github.hantsy.ee8sample.domain.repository.UserRepository;
import com.github.hantsy.ee8sample.rest.user.EmailWasTakenExceptionMapper;
import com.github.hantsy.ee8sample.rest.user.RegisterForm;
import com.github.hantsy.ee8sample.rest.user.UserResource;
import com.github.hantsy.ee8sample.rest.user.UsernameWasTakenExceptionMapper;
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
import static javax.ws.rs.client.Entity.json;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.http.HttpStatus;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.ArquillianTest;
import org.jboss.arquillian.junit.ArquillianTestClass;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 *
 * @author hantsy
 */
// replaced with @ClassRule and @Rule
// @RunWith(Arquillian.class) 
public class UserResourceWithoutAuthIT {

    @ClassRule
    public static ArquillianTestClass arquillianTestClass = new ArquillianTestClass();
    
    @Rule 
    public ArquillianTest arquillianTest = new ArquillianTest();

    private static final Logger LOG = Logger.getLogger(UserResourceWithoutAuthIT.class.getName());

    @Deployment(testable = false)
    public static WebArchive createDeployment() {

        File[] extraJars = Maven.resolver().loadPomFromFile("pom.xml")
            .resolve(
                "org.projectlombok:lombok:1.16.8",
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
        client.register(EmailWasTakenExceptionMapper.class);
        client.register(UsernameWasTakenExceptionMapper.class);
    }

    @After
    public void teardown() throws MalformedURLException {
        client.close();
    }

    @Test
    public void testGetAllUsersShouldBeOK() throws MalformedURLException {
        //get all users
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
    public void testGetCountOfAllUsersShouldBeOK() throws MalformedURLException {
        //get count of all users
        final WebTarget targetGetCount = client.target(URI.create(new URL(base, "api/users/count").toExternalForm()));
        try (Response resGetCount = targetGetCount.request().accept(MediaType.APPLICATION_JSON_TYPE).get()) {
            assertEquals(200, resGetCount.getStatus());
            Count results = resGetCount.readEntity(Count.class);
            assertTrue(results != null);
            LOG.log(Level.INFO, "results.size()::{0}", results.getCount());
            assertTrue(results.getCount() == 2);
        }

    }
    
    @Test
    public void checkNonExistedUser_ShouldReturnFalseResult() throws MalformedURLException {

        final WebTarget targetCheck = client.target(URI.create(new URL(base, "api/users/exists").toExternalForm()));
        try (Response resCheck = targetCheck
            .queryParam("username", "non_existed")
            .request()
            .accept(MediaType.APPLICATION_JSON_TYPE)
            .get()
            ) {
            assertEquals(200, resCheck.getStatus());
            
            Existed existed = resCheck.readEntity(Existed.class);
            assertFalse(existed.isExisted());
            
        }

    }
    
    @Test
    public void checkExistedUser_ShouldReturnTrueResult() throws MalformedURLException {
        final WebTarget targetCheck = client.target(URI.create(new URL(base, "api/users/exists").toExternalForm()));
        try (Response resCheck = targetCheck
            .queryParam("username", "user")
            .request()
            .accept(MediaType.APPLICATION_JSON_TYPE)
            .get()
            ) {
            assertEquals(200, resCheck.getStatus());
            
            Existed existed = resCheck.readEntity(Existed.class);
            assertTrue(existed.isExisted());
            
        }

    }
    
    @Test
    public void checkUserWithParams_ShouldReturnBadRequest() throws MalformedURLException {

        final WebTarget targetCheck = client.target(URI.create(new URL(base, "api/users/exists").toExternalForm()));
        try (Response resCheck = targetCheck
            .request()
            .accept(MediaType.APPLICATION_JSON_TYPE)
            .get()
            ) {
            assertEquals(HttpStatus.SC_BAD_REQUEST, resCheck.getStatus());
                        
        }

    }

    @Test
    public void createUserWithoutAuth_shouldReturn401() throws MalformedURLException {
        final WebTarget targetCreate = client.target(URI.create(new URL(base, "api/users").toExternalForm()));
        RegisterForm  uesr = RegisterForm.builder().username("test").password("password").firstName("test first name").lastName("test last name").email("test@example.com").build();
        try (Response resCreate = targetCreate.request().accept(MediaType.APPLICATION_JSON_TYPE).post(json(uesr))) {
            assertEquals(401, resCreate.getStatus());
        }

    }
//
//    @Test
//    public void getNonexistedUser_shouldReturn404() throws MalformedURLException {
//
//        final WebTarget targetGet = client.target(URI.create(new URL(base, "api/users/non_existed").toExternalForm()));
//        try (Response resGetAll = targetGet.request().accept(MediaType.APPLICATION_JSON_TYPE).get()) {
//            assertEquals(404, resGetAll.getStatus());
//        }
//
//    }
//
//    @Test
//    public void updateUserWithoutAuth_shouldReturn401() throws MalformedURLException {
//
//        //put
//        final WebTarget targetUpdate = client.target(URI.create(new URL(base, "api/users/1").toExternalForm()));
//        UserForm  uesr = UserForm.builder().firstName("test name").lastName("test name").email("test@example.com").build();
//        try (Response resGetAll = targetUpdate.request().accept(MediaType.APPLICATION_JSON_TYPE).put(json(uesr))) {
//            assertEquals(401, resGetAll.getStatus());
//        }
//
//    }
//
//    @Test
//    public void deleteUserWithoutAuth_shouldReturn401() throws MalformedURLException {
//
//        //post
//        final WebTarget targetDelete = client.target(URI.create(new URL(base, "api/users/1").toExternalForm()));
//        try (Response resGetAll = targetDelete.request().accept(MediaType.APPLICATION_JSON_TYPE).delete()) {
//            assertEquals(401, resGetAll.getStatus());
//        }
//
//    }

}
