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
@Table(name = "cats")
public class Cat {
    @Id
    private String id;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "cat_breeds",
        joinColumns = @JoinColumn(name = "cat_id"),
        inverseJoinColumns = @JoinColumn(name = "breed_id"),
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"cat_id", "breed_id"})
        }
    )
    private List<Breed> breeds = new ArrayList<>();
}