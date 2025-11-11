package com.dat.backend.inventoryservice.mapper;

import com.dat.backend.inventoryservice.dto.InventoryResponse;
import com.dat.backend.inventoryservice.entity.Inventory;

public class InventoryMapper {

    public static InventoryResponse inventoryToInventoryResponse(Inventory inventory) {
        InventoryResponse inventoryResponse = new InventoryResponse();
        inventoryResponse.setProductId(inventory.getProductId());
        inventoryResponse.setId(inventory.getId());
        inventoryResponse.setStock(inventory.getStock());
        inventoryResponse.setAvailable_stock(inventory.getAvailable_stock());
        inventoryResponse.setReserve_stock(inventory.getReserve_stock());
        inventoryResponse.setUpdateAt(inventory.getUpdateAt());
        return inventoryResponse;
    }
}
