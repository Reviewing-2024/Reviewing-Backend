package com.reviewing.review.course.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Platform {

    @Id
    private Long id;

    private String name;

}
