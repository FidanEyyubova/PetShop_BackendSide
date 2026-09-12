package com.petshop.backend.entity.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_addresses")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private String title;
    
    @Column(name = "full_address", columnDefinition = "TEXT")
    private String fullAddress;

    private String city;

    @Column(name = "is_default")
    private Boolean isDefault;
}
