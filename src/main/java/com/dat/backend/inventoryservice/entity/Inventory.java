package com.dat.backend.inventoryservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "inventory")
public class Inventory {

    @Id
    @UuidGenerator
    private String id;

    private String productId;
    private Integer stock;
    private Integer reserve_stock;
    private Integer available_stock;

    @UpdateTimestamp
    private LocalDateTime updateAt;
}
