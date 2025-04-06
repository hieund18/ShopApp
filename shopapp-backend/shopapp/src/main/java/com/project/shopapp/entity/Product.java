package com.project.shopapp.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "products")
public class Product extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 350)
    String name;

    @Column(nullable = false)
    Float price;

    @Column(length = 300)
    String thumbnail;

    String description;

    @Column(nullable = false)
    Integer quantity;

    @Column(name = "is_Active", nullable = false)
    Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;
}
