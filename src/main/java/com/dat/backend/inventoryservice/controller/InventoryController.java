package com.dat.backend.inventoryservice.controller;

import com.dat.backend.inventoryservice.dto.InventoryResponse;
import com.dat.backend.inventoryservice.dto.UpdateStock;
import com.dat.backend.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @PostMapping("/update")
    public ResponseEntity<InventoryResponse> update(@RequestBody UpdateStock updateStock) {
        return ResponseEntity.ok(inventoryService.update(updateStock));
    }
}
