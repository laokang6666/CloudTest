package com.car.bookservice.repository;

import com.car.bookservice.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Book b set b.availableStock = b.availableStock - 1 where b.id = :id and b.availableStock > 0")
    int decreaseAvailableStock(@Param("id") Long id);
}
