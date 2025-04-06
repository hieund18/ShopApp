package com.project.shopapp.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "fullname", length = 100)
    String fullName;

    @Column(nullable = false, name = "phone_number", length = 10)
    String phoneNumber;

    @Column(length = 200)
    String address;

    @Column(nullable = false, length = 100)
    String password;

    @Column(name = "is_active", nullable = false)
    Boolean isActive;

    @Column(name = "date_of_birth")
    LocalDate dateOfBirth;

    @Column(name = "facebook_account_id")
    Integer facebookAccountId;

    @Column(name = "google_account_id")
    Integer googleAccountId;

    @ManyToMany
    @JoinTable(name = "users_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id"))
    Set<Role> roles;
}
