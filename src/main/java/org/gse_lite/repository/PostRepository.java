package org.gse_lite.repository;

import org.gse_lite.model.Post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class PostRepository implements Repository<Post, Long> {
    private final CopyOnWriteArrayList<Post> posts = new CopyOnWriteArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    private static final PostRepository INSTANCE = new PostRepository();

    private PostRepository() {
        seedData();
    }

    public static PostRepository getPostRepoInstance() {
        return INSTANCE;
    }

    @Override
    public void save(Post post) {
        Post newPost = new Post(
                idGenerator.getAndIncrement(),
                post.getAuthor(),
                post.getContent(),
                post.getTimestamp(),
                post.getLikesCount()
        );

        posts.add(newPost);

    }

    @Override
    public Optional<Post> findById(Long id) {
        return posts.stream()
                .filter(post -> post.getId() == id)
                .findFirst();
    }


    @Override
    public List<Post> findAll() {
        return List.copyOf(posts);
    }

    @Override
    public List<Post> findPaginated(int pageNo, int chunk) {
        if(pageNo<=0 || chunk<=0) {
            return List.of();
        }

        int startIndex = (pageNo-1)*chunk;

        if(startIndex>=posts.size()) {
            return List.of();
        }

        int endIndex = Math.min(startIndex+chunk-1, posts.size()-1);

        return new ArrayList<>(posts.subList(startIndex, endIndex+1));
    }

    @Override
    public long count() {
        return posts.size();
    }

    private void seedData() {
        String[] authors = {
                "Aarav",
                "Ananya",
                "Dev",
                "Diya",
                "Puja",
                "Shaan",
                "Rohan",
                "Kunal",
                "Vihaan",
                "Sara"
        };

        String[] contents = {
                "Learning Servlets and JSP.",
                "Java concurrency is fascinating.",
                "Jakarta EE 11 migration completed.",
                "Reflection makes frameworks powerful.",
                "Building my own MVC framework.",
                "Infinite scrolling is coming next.",
                "CopyOnWriteArrayList is thread-safe.",
                "Thread safety matters in web apps.",
                "Working on GitHub Issues today.",
                "Issue #3 is almost complete."
        };

        for (int i = 0; i<30; i++) {
            posts.add(
                    new Post(
                            idGenerator.getAndIncrement(),
                            authors[i % authors.length],
                            contents[i % contents.length],
                            LocalDateTime.now().minusHours(i),
                            (i+1)*5
                    )
            );
        }
    }


}
