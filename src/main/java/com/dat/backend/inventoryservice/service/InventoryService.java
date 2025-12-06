package com.dat.backend.inventoryservice.service;

import com.dat.backend.inventoryservice.dto.InventoryResponse;
import com.dat.backend.inventoryservice.dto.UpdateStock;
import com.dat.backend.inventoryservice.entity.Inventory;
import com.dat.backend.inventoryservice.entity.OutboxInventory;
import com.dat.backend.inventoryservice.mapper.InventoryMapper;
import com.dat.backend.inventoryservice.repository.InventoryRepository;
import com.dat.backend.inventoryservice.repository.OutBoxInventoryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final OutBoxInventoryRepository outBoxInventoryRepository;

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
            groupId = "inventory-group"
    )
    public void updateNewProduct(ConsumerRecord<String, ?> record) {
        // TODO: create new product in DB when shopper add new product
    }

    @KafkaListener(id = "listener-increase-reverse",
            topics = "outbox.db_order.outbox_order",
            containerFactory = "recordListenerContainerFactory",
            groupId = "inventory-group"
    )
    public void increaseReverseStock(ConsumerRecord<String, Object> record) {
        String response = record.value().toString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.convertValue(response, JsonNode.class);
        String productId = node.path("after").path("productId").asText();
        String productQuantity = node.path("after").path("productQuantity").asText();
        Inventory inventory = inventoryRepository.findByProductId(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        int reverse_stock = inventory.getReserve_stock();
        int stock = inventory.getStock();
        int available_stock = inventory.getAvailable_stock() - Integer.parseInt(productQuantity);
        inventory.setReserve_stock(reverse_stock + Integer.parseInt(productQuantity));
        inventoryRepository.save(inventory);

        // Save data to outbox for update in product-service
        OutboxInventory outboxInventory = new OutboxInventory();
        outboxInventory.setProductId(productId);
        outboxInventory.setAvailable_stock(available_stock);
        outBoxInventoryRepository.save(outboxInventory);
    }

    @KafkaListener(id = "listener-status-order",
            topics = "outbox.db_order.outbox_order",
            containerFactory = "recordListenerContainerFactory",
            groupId = "inventory-group"
    )
    public void updateByOrderStatus(ConsumerRecord<String, Object> record) {
        String response = record.value().toString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.convertValue(response, JsonNode.class);
        String orderStatus = node.path("after").path("orderStatus").asText();
        if ("CANCELLED".equalsIgnoreCase(orderStatus)) {
            // Logic rollback
            String productId = node.path("after").path("productId").asText();
            String productQuantity = node.path("after").path("productQuantity").asText();

            // Step 1: decrease reverse stock
            Inventory inventory = inventoryRepository.findByProductId(productId).orElseThrow(() -> new RuntimeException("Product not found"));
            int reverse_stock = inventory.getReserve_stock();
            int newReserve_stock = inventory.getReserve_stock() + Integer.parseInt(productQuantity);
            inventory.setReserve_stock(newReserve_stock);
            int available_stock = inventory.getStock() - newReserve_stock;
            inventory.setAvailable_stock(available_stock);
            inventoryRepository.save(inventory);

            // Step 2: send available stock to outbox
            OutboxInventory outboxInventory = outBoxInventoryRepository.findByProductId(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            outboxInventory.setAvailable_stock(available_stock);
            outBoxInventoryRepository.save(outboxInventory);
        }
    }
}
