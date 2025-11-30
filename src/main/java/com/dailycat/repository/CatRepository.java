package com.dailycat.repository;

import com.dailycat.model.Cat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CatRepository extends JpaRepository<Cat, String> {
    
    @Query(value = "SELECT * FROM cats ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Cat> findRandomCat();
}
