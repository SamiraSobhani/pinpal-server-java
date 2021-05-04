package com.pinpal.pinpalproject.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.Valid;
import java.util.Set;

@Entity
@Table(name = "categories", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
}, indexes = {
        @Index(columnList = "name", name = "name_idx")})
@Getter
@Setter
@RequiredArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String icon;

    @Valid
    @JsonIgnore
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE)
    private Set<Poster> posters;
}
