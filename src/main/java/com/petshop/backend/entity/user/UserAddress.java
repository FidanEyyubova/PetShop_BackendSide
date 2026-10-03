package com.petshop.backend.entity.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private String title;

    private String city;

    private String district;

    private String street;

    /** Floor / apartment, free text. */
    private String floor;

    @Column(columnDefinition = "TEXT")
    private String note;

    /** Single-line address; built from the parts when the client does not send it. */
    @Column(name = "full_address", columnDefinition = "TEXT")
    private String fullAddress;

    @Column(name = "is_default")
    private Boolean isDefault;
}
