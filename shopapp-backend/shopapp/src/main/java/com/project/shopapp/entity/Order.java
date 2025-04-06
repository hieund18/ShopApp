package com.project.shopapp.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "orders")
@Entity
public class Order extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "fullname", length = 100)
    String fullName;

    @Column(length = 100)
    String email;

    @Column(nullable = false, name = "phone_number", length = 10)
    String phoneNumber;

    @Column(length = 200, nullable = false)
    String address;

    @Column(length = 100)
    String note;

    @Column(length = 20)
    String status;

    @Column(name = "total_money", nullable = false)
    Float totalMoney;

    @Column(name = "shipping_method")
    String shippingMethod;

    @Column(name = "shipping_address")
    String shippingAddress;

    @Column(name = "shipping_date")
    LocalDate shippingDate;

    @Column(name = "tracking_number")
    String trackingNumber;

    @Column(name = "payment_method")
    String paymentMethod;

    @Column(name = "is_active")
    Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
}
