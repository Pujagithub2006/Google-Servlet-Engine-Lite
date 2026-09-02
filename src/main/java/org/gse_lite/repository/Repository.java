package org.gse_lite.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, ID> {
    void save(T entity); // creates posts

    Optional<T> findById(ID id);  // find post of type T by ID


    List<T> findAll(); // find all posts of type T

    List<T> findPaginated(int pageNo, int chunk);  // find a particular page I want to view with a specific chunk of pages

    long count(); // return the number of posts of type T

}