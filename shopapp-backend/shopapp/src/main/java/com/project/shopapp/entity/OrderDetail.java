package com.project.shopapp.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "order_details")
@Entity
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;

    @Column(nullable = false)
    Float price;

    @Column(name = "number_of_products", nullable = false)
    Integer numberOfProducts;

    @Column(name = "total_money", nullable = false)
    Float totalMoney;

    String color;
}
