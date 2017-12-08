/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.hantsy.ee8sample.domain.repository;

import com.github.hantsy.ee8sample.Utils;
import com.github.hantsy.ee8sample.repository.PostRepository;
import com.github.hantsy.ee8sample.domain.Post;
import java.util.List;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author hantsy
 */
@RunWith(Arquillian.class)
public class PostRepositoryIT {

    @Deployment(name = "test")
    public static Archive<?> createDeployment() {
        JavaArchive archive = ShrinkWrap.create(JavaArchive.class)
                .addPackages(true, Post.class.getPackage(), PostRepository.class.getPackage())
                // base classes
                .addClasses(Utils.class)
                .addAsManifestResource("META-INF/test-persistence.xml", "persistence.xml")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        // System.out.println(archive.toString(true));
        return archive;
    }

    @Inject
    PostRepository posts;

    private static final String TITLE = "test_title";
    private static final String CONTENT = "test_content";

    Post _saved;

    @Before
    public void setUp() throws Exception {
        Post post = Post.builder().title(TITLE).content(CONTENT).build();
        _saved = posts.save(post);
    }

    @After
    public void tearDown() throws Exception {
        posts.delete(_saved);
    }

    @Test
    public void testStreamResultWithNullKeyword() {
        List<Post> _posts = posts.findByKeyword(null, 10, 0);
        assertTrue(!_posts.isEmpty());
        assertTrue(_posts.size() == posts.countByKeyword("test"));
    }

    @Test
    public void testStreamResult() {
        List<Post> _posts = posts.findByKeyword("test", 10, 0);
        assertTrue(!_posts.isEmpty());
        assertTrue(_posts.size() == posts.countByKeyword("test"));
    }

    @Test
    public void testFindByKeyword() {
        List<Post> foundPosts = posts.findByKeyword("test", 10, 0);
        assertEquals(1, foundPosts.size());
    }

    @Test
    public void testFindByKeywordNotFound() {
        List<Post> foundPosts = posts.findByKeyword("test123", 10, 0);
        assertEquals(0, foundPosts.size());
    }

    @Test
    public void testFindById() {
        Post found = posts.findById(_saved.getId());
        assertNotNull(found);
        assertEquals(TITLE, found.getTitle());
        assertEquals(CONTENT, found.getContent());
        assertNotNull(found.getId());
        assertNotNull(found.getVersion());
    }

    @Test
    public void testCRUD() {
        Post post = Post.builder().title(TITLE + "1").content(CONTENT + "1").build();
        Post saved = posts.save(post);

        assertEquals(TITLE + "1", saved.getTitle());
        assertEquals(CONTENT + "1", saved.getContent());
        assertNotNull(saved.getId());
        assertNotNull(saved.getVersion());

        saved.setTitle(TITLE + "updated");
        saved.setContent(CONTENT + "updated");

        Post updated = posts.save(saved);

        assertEquals(TITLE + "updated", updated.getTitle());
        assertEquals(CONTENT + "updated", updated.getContent());

        Long id = updated.getId();
        assertNotNull(id);

        Post found = posts.findById(id);
        assertEquals(TITLE + "updated", found.getTitle());
        assertEquals(CONTENT + "updated", found.getContent());

        posts.delete(found);

        assertNull(posts.findById(id));
    }

}
