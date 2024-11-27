package com.reviewing.review.course.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Category {

    @Id
    private Long id;

    private String name;
    private String slug;

    @ManyToOne
    private Platform platform;

}
