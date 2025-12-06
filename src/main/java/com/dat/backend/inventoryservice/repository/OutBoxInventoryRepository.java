package com.dat.backend.inventoryservice.repository;

import com.dat.backend.inventoryservice.entity.OutboxInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OutBoxInventoryRepository extends JpaRepository<OutboxInventory, String> {
    Optional<OutboxInventory> findByProductId(String productId);
}
