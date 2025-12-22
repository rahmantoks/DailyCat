package com.dailycat.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dogs")
public class Dog {
    @Id
    private String id;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "dog_breed_join", joinColumns = @JoinColumn(name = "dog_id"), inverseJoinColumns = @JoinColumn(name = "breed_id"), uniqueConstraints = {
            @UniqueConstraint(columnNames = { "dog_id", "breed_id" })
    })
    private List<DogBreed> breeds = new ArrayList<>();
}
