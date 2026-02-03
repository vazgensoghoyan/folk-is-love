package com.folkislove.love.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "tags")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "tags")
    @Builder.Default
    private Set<Post> posts = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "tags")
    @Builder.Default
    private Set<Event> events = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "interests")
    @Builder.Default
    private Set<User> users = new HashSet<>();
}
