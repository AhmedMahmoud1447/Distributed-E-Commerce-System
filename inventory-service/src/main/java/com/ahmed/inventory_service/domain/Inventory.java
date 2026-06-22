package com.ahmed.inventory_service.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventories")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private Integer availableQuantity;
}