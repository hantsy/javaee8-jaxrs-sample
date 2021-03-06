/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest;

import com.github.hantsy.ee8sample.rest.post.CommentNotFoundExceptionMapper;
import com.github.hantsy.ee8sample.rest.post.PostResource;
import com.github.hantsy.ee8sample.rest.post.PostNotFoundExceptionMapper;
import com.github.hantsy.ee8sample.Bootstrap;
import com.github.hantsy.ee8sample.domain.support.AbstractEntity;
import com.github.hantsy.ee8sample.domain.Post;
import com.github.hantsy.ee8sample.domain.repository.PostRepository;
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
public class PostResourceWithoutAuthIT {

    private static final Logger LOG = Logger.getLogger(PostResourceWithoutAuthIT.class.getName());

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
            .addPackage(Post.class.getPackage())
            // repository package.
            .addPackage(PostRepository.class.getPackage())

            //base classes
            .addPackage(Bootstrap.class.getPackage())
            //Add JAXRS resources classes
            .addPackage(PostResource.class.getPackage())
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
        client.register(PostNotFoundExceptionMapper.class);
        client.register(CommentNotFoundExceptionMapper.class);
//        final WebTarget targetAuthGetAll = client.target(URI.create(new URL(base, "api/auth/login").toExternalForm()));
//        final Response resAuthGetAll = targetAuthGetAll.request()
//                .accept(MediaType.APPLICATION_JSON_TYPE)
//                .post(Entity.json(new Credentials("admin", "admin123", true)));
//        assertEquals(200, resAuthGetAll.getStatus());
////        IdToken token = resAuthGetAll.readEntity(IdToken.class);
//
//        client.register(new JwtTokenAuthentication(token.getToken()));
    }

    @After
    public void teardown() throws MalformedURLException {
        client.close();
    }

    private static final String TITLE = "test_title";
    private static final String CONTENT = "test_content";

    @Test
    public void testPosts() throws MalformedURLException {

        LOG.log(Level.INFO, "base url @{0}", base);

        //get all posts
        final WebTarget targetGetAll = client.target(URI.create(new URL(base, "api/posts").toExternalForm()));
        try (Response resGetAll = targetGetAll.request().accept(MediaType.APPLICATION_JSON_TYPE).get()) {
            assertEquals(200, resGetAll.getStatus());
            List<Post> results = resGetAll.readEntity(new GenericType<List<Post>>() {
            });
            assertTrue(results != null);
            LOG.info("results.size()::" + results.size());
            assertTrue(results.size() == 3);
        }

    }

    @Test
    public void createPostWithoutAuth_shouldReturn401() throws MalformedURLException {

        LOG.log(Level.INFO, "base url @{0}", base);

        //post
        final WebTarget targetCreatePost = client.target(URI.create(new URL(base, "api/posts").toExternalForm()));
        Post post = Post.builder().title("created title").content("created content").build();
        try (Response resGetAll = targetCreatePost.request().accept(MediaType.APPLICATION_JSON_TYPE).post(json(post))) {
            assertEquals(401, resGetAll.getStatus());
        }

    }
    
    @Test
    public void getNonexistedPost_shouldReturn404() throws MalformedURLException {

        LOG.log(Level.INFO, "base url @{0}", base);

        final WebTarget targetGet = client.target(URI.create(new URL(base, "api/posts/non_existed").toExternalForm()));
        try (Response resGetAll = targetGet.request().accept(MediaType.APPLICATION_JSON_TYPE).get()) {
            assertEquals(404, resGetAll.getStatus());
        }

    }
    
     @Test
    public void updatePostWithoutAuth_shouldReturn401() throws MalformedURLException {

        LOG.log(Level.INFO, "base url @{0}", base);

        //post
        final WebTarget targetGetAll = client.target(URI.create(new URL(base, "api/posts/1").toExternalForm()));
        Post post = Post.builder().title("created title").content("created content").build();
        try (Response resGetAll = targetGetAll.request().accept(MediaType.APPLICATION_JSON_TYPE).put(json(post))) {
            assertEquals(401, resGetAll.getStatus());
        }

    }
    
    @Test
    public void deletePostWithoutAuth_shouldReturn401() throws MalformedURLException {

        LOG.log(Level.INFO, "base url @{0}", base);

        //post
        final WebTarget targetGetAll = client.target(URI.create(new URL(base, "api/posts/1").toExternalForm()));
        try (Response resGetAll = targetGetAll.request().accept(MediaType.APPLICATION_JSON_TYPE).delete()) {
            assertEquals(401, resGetAll.getStatus());
        }

    }

}
