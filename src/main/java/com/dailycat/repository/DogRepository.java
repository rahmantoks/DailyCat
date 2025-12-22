package com.dailycat.repository;

import com.dailycat.model.Dog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DogRepository extends JpaRepository<Dog, String> {

    @Query(value = "SELECT * FROM dogs ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Dog> findRandomDog();
}
