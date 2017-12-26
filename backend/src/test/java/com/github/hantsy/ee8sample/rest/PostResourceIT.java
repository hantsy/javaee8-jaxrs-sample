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
import com.github.hantsy.ee8sample.Constants;
import com.github.hantsy.ee8sample.domain.Comment;
import com.github.hantsy.ee8sample.domain.support.AbstractEntity;
import com.github.hantsy.ee8sample.domain.Post;
import com.github.hantsy.ee8sample.domain.repository.PostRepository;
import com.github.hantsy.ee8sample.rest.auth.AuthResource;
import com.github.hantsy.ee8sample.rest.post.CommentForm;
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
public class PostResourceIT {

    private static final Logger LOG = Logger.getLogger(PostResourceIT.class.getName());

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
        client.register(PostNotFoundExceptionMapper.class);
        client.register(CommentNotFoundExceptionMapper.class);

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

    private static final String TITLE = "test_title";
    private static final String CONTENT = "test_content";

    @Test
    public void testGetPostsShouldBeOk() throws MalformedURLException {

        LOG.log(Level.INFO, "base url @{0}", base);

        //get all posts
        final WebTarget targetGetAll = client.target(URI.create(new URL(base, "api/posts").toExternalForm()));
        try (Response resGetAll = targetGetAll.request().accept(MediaType.APPLICATION_JSON_TYPE).get()) {
            assertEquals(200, resGetAll.getStatus());
            List<Post> results = resGetAll.readEntity(new GenericType<List<Post>>() {
            });
            assertTrue(results != null);
            LOG.log(Level.INFO, "results.size()::{0}", results.size());
            assertTrue(results.size() == 3);
        }

    }

    @Test
    public void testCompletePostCommentsFlow() throws MalformedURLException {

        LOG.log(Level.INFO, "base url @{0}", base);

        //post
        final WebTarget targetPostAll = client.target(URI.create(new URL(base, "api/posts").toExternalForm()));
        Post post = Post.builder().title(TITLE).content(CONTENT).build();

        String location;
        try (Response resPostAll = targetPostAll.request().accept(MediaType.APPLICATION_JSON_TYPE).post(json(post))) {

            assertEquals(201, resPostAll.getStatus());
            location = resPostAll.getHeaderString("Location");

            assertNotNull(location);
        }

        //get
        final WebTarget targetGet = client.target(URI.create(new URL(base, location).toExternalForm()));
        try (Response resGet = targetGet.request().accept(MediaType.APPLICATION_JSON_TYPE).get()) {
            assertEquals(200, resGet.getStatus());
            Post result = resGet.readEntity(Post.class);
            assertTrue(result != null);
            assertEquals(TITLE, result.getTitle());
            assertEquals(CONTENT, result.getContent());
        }

        //update
        final WebTarget targetUpdateAll = client.target(URI.create(new URL(base, location).toExternalForm()));
        Post updatedPost = Post.builder().title(TITLE + "1").content(CONTENT + "2").build();

        try (Response resPutAll = targetUpdateAll.request().put(json(updatedPost))) {
            assertEquals(204, resPutAll.getStatus());
        }

        //verifty update.
        //get
        final WebTarget targetVerifyUpdateGet = client.target(URI.create(new URL(base, location).toExternalForm()));
        try (Response resVerifyUpdateGet = targetVerifyUpdateGet.request().accept(MediaType.APPLICATION_JSON_TYPE).get()) {
            assertEquals(200, resVerifyUpdateGet.getStatus());
            Post result = resVerifyUpdateGet.readEntity(Post.class);
            assertTrue(result != null);
            assertEquals(TITLE + "1", result.getTitle());
            assertEquals(CONTENT + "2", result.getContent());
        }

        String commentsLocation = location + "/comments";

        //comments 
        //create new comment of post
        final WebTarget targetCreateComment = client.target(URI.create(new URL(base, commentsLocation).toExternalForm()));
        String commentLocation;
        CommentForm comment = CommentForm.builder().content("comment of post").build();
        try (Response resPostAll = targetCreateComment.request().post(json(comment))) {

            assertEquals(201, resPostAll.getStatus());
            commentLocation = resPostAll.getHeaderString("Location");
            
            LOG.log(Level.INFO, "comment location::" +commentLocation);
            assertNotNull(commentLocation);
        }

        //get all comments of a post
        final WebTarget targetGetAllComments = client.target(URI.create(new URL(base, commentsLocation).toExternalForm()));
        try (Response resGetAll = targetGetAllComments.request().accept(MediaType.APPLICATION_JSON_TYPE).get()) {
            assertEquals(200, resGetAll.getStatus());
            List<Comment> results = resGetAll.readEntity(new GenericType<List<Comment>>() {
            });
            assertTrue(results != null);
            LOG.log(Level.INFO, "comments results.size()::{0}", results.size());
            assertTrue(results.size() == 1);
        }

        //update comments
        final WebTarget targetUpdateComment = client.target(URI.create(new URL(base, commentLocation).toExternalForm()));
        CommentForm updatedComment = CommentForm.builder().content("comment of post updated").build();

        try (Response resPutAll = targetUpdateComment.request().put(json(updatedComment))) {
            assertEquals(204, resPutAll.getStatus());
        }

        //del comment.
        final WebTarget targetDelComment = client.target(URI.create(new URL(base, commentLocation).toExternalForm()));

        try (Response resDel = targetDelComment.request().delete()) {
            assertEquals(204, resDel.getStatus());
        }
        
        // verify comments deleted
        final WebTarget targetVerifyComments = client.target(URI.create(new URL(base, commentsLocation).toExternalForm()));
        try (Response resGetAll = targetVerifyComments.request().accept(MediaType.APPLICATION_JSON_TYPE).get()) {
            assertEquals(200, resGetAll.getStatus());
            List<Comment> results = resGetAll.readEntity(new GenericType<List<Comment>>() {
            });
            assertTrue(results != null);
            LOG.log(Level.INFO, "comments results.size()::{0}", results.size());
            assertTrue(results.isEmpty());
        }

        //delete Post
        final WebTarget targetDelPost = client.target(URI.create(new URL(base, location).toExternalForm()));

        try (Response resDel = targetDelPost.request().delete()) {
            assertEquals(204, resDel.getStatus());
        }
    }

}
