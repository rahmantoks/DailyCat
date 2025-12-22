package com.dailycat.repository;

import com.dailycat.model.DogBreed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DogBreedRepository extends JpaRepository<DogBreed, String> {
}
