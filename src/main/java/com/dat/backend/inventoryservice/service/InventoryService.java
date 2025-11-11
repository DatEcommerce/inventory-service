package com.dat.backend.inventoryservice.service;

import com.dat.backend.inventoryservice.dto.InventoryResponse;
import com.dat.backend.inventoryservice.dto.UpdateStock;
import com.dat.backend.inventoryservice.entity.Inventory;
import com.dat.backend.inventoryservice.mapper.InventoryMapper;
import com.dat.backend.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryResponse update(UpdateStock updateStock) {
        Integer new_stock = updateStock.getNew_stock();
        String productId = updateStock.getProductId();

        Inventory inventory = inventoryRepository.findByProductId(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        inventory.setStock(new_stock);
        inventoryRepository.save(inventory);

        return InventoryMapper.inventoryToInventoryResponse(inventory);
    }

    @KafkaListener(id = "inventory-new-product-listener",
            topics = "inventory-new-product",
            containerFactory = "recordListenerContainerFactory",
            groupId = "product-group"
    )
    public void updateNewProduct(ConsumerRecord<String, ?> record) {
        // TODO: create new product in DB when shopper add new product
    }

    @KafkaListener(id = "reservation-listener",
            topics = "inventory-reserve-stock",
            containerFactory = "recordListenerContainerFactory",
            groupId = "product-group"
    )
    public void reserveStock(ConsumerRecord<String, ?> record) {
        // TODO: increase reserve stock when new order create
    }
}
