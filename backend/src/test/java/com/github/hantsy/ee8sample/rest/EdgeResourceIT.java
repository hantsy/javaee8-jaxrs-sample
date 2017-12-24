/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.rest;

import com.github.hantsy.ee8sample.Bootstrap;
import com.github.hantsy.ee8sample.Constants;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author hantsy
 */
@RunWith(Arquillian.class)
public class EdgeResourceIT {

    private static final Logger LOG = Logger.getLogger(EdgeResourceIT.class.getName());

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
            .addPackages(true, Bootstrap.class.getPackage())
            // .addAsResource("test-log4j.properties", "log4j.properties")
            //Add JPA persistence configration.
            //WARN: In a war package, persistence.xml should be put into /WEB-INF/classes/META-INF/, not /META-INF
            //.addAsManifestResource("META-INF/test-persistence.xml", "persistence.xml")
            .addAsResource("META-INF/persistence.xml")
            .addAsResource("META-INF/orm.xml");

//            .addAsWebInfResource("test-web.xml", "web.xml")
//            // Enable CDI
//            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        //  .addAsWebInfResource("test-jboss-deployment-structure.xml", "jboss-deployment-structure.xml");
        war.merge(
            ShrinkWrap.create(GenericArchive.class)
                .as(ExplodedImporter.class)
                .importDirectory("src/main/webapp")
                .as(GenericArchive.class),
            "/"
        );
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

    @Test
    public void testGetPostsViaEdgeAPIs() throws MalformedURLException {

        LOG.log(Level.INFO, "base url @{0}", base);
        final WebTarget targetUser = client.target(URI.create(new URL(base, "api/edge/posts").toExternalForm()));
        try (Response resPosts = targetUser.request().accept(MediaType.APPLICATION_JSON).get()) {
            assertEquals(200, resPosts.getStatus());
            LOG.log(Level.INFO, "response content info @{0}", resPosts.readEntity(String.class));
        }

    }

}
