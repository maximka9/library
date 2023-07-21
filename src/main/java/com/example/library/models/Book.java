package com.example.library.models;

import javax.persistence.*;
import lombok.*;


@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "author")
    private String author;

    @Column(name = "year")
    private int year;

    @Column(name = "archived")
    private boolean archived;

    protected boolean canEqual(final Object other) {
        return other instanceof Book;
    }
}