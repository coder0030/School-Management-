package com.example.demo.Repository;

import com.example.demo.ENTITY.LibraryBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibraryBookRepository extends JpaRepository<LibraryBook,Long> {
    boolean existsByTitleAndAuthor(String title, String author);

    @Query("SELECT l FROM LibraryBook l WHERE l.title LIKE CONCAT(:keyword, '%')")
    List<LibraryBook> findByTitleStartingWith(@Param("keyword") String keyword);

    boolean existsByTitleAndAuthorAndIdNot(String title, String author, Long id);
}
