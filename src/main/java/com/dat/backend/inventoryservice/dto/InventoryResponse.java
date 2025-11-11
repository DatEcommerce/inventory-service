package com.dat.backend.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponse {
    private String id;
    private String productId;
    private Integer stock;
    private Integer available_stock;
    private Integer reserve_stock;
    private LocalDateTime updateAt;
}
