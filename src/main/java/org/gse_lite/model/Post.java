package org.gse_lite.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Post {
    private final long id;
    private final String author;
    private final String content;
    private final LocalDateTime timestamp;
    private final int likesCount;

    public Post (long id, String author, String content, LocalDateTime timestamp, int likesCount) {
        if(id<0) throw new IllegalArgumentException("Id can not be negative");
        if(author == null || author.isBlank()) throw new IllegalArgumentException("Author can not be null or blank");
        if(content == null || content.isBlank()) throw new IllegalArgumentException("Content can not be null or blank");
        if(likesCount<0) throw new IllegalArgumentException("Likes count can not be negative");

        this.id = id;
        this.author = author;
        this.content = content;
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp can not be null");
        this.likesCount = likesCount;
    }

    public long getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getLikesCount() {
        return likesCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return id == post.id && likesCount == post.likesCount && Objects.equals(author, post.author) && Objects.equals(content, post.content) && Objects.equals(timestamp, post.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, author, content, timestamp, likesCount);
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", author='" + author + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", likesCount=" + likesCount +
                '}';
    }
}