package com.project.shopapp.entity;

import java.util.Date;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "invalidated_tokens")
@Entity
public class InvalidatedToken {
    @Id
    String id;

    @Column(name = "expiry_time", nullable = false)
    Date expiryTime;
}
