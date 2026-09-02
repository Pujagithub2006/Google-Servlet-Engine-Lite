package org.gse_lite.service;

import org.gse_lite.model.Post;
import org.gse_lite.repository.PostRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class PostService {
    private final PostRepository postRepository;

    public PostService() {
        this.postRepository = PostRepository.getPostRepoInstance();
    }

    public Optional<Post> getPostById(long id) {
        return postRepository.findById(id);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public List<Post> getPaginated(int pageNo, int chunk) {
        return postRepository.findPaginated(pageNo, chunk);
    }

    public void createPost(String author, String content) {
        Post post = new Post (
            0,
            author,
            content,
            LocalDateTime.now(),
            0
        );

        postRepository.save(post);
    }

    public long getTotalPosts() {
        return postRepository.count();
    }





}