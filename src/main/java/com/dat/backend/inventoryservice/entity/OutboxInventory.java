package com.dat.backend.inventoryservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "outbox_inventory")
public class OutboxInventory {
    @Id
    @UuidGenerator
    private String id;

    private String productId;
    private Integer available_stock;
}
