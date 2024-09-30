package com.test.controller.controller;

import com.test.controller.entity.Item;
import com.test.controller.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PostMapping
    public ResponseEntity<?> addItems(@RequestBody List<Item> items) {
        if (items == null || items.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Item list is null or empty");
        }

        List<Item> conflicts = new ArrayList<>();
        List<Item> addedItems = new ArrayList<>();

        for (Item item : items) {
            Optional<Item> existingItem = itemService.findById(item.getId());

            if (existingItem.isPresent()) {
                conflicts.add(item);  // Si el item ya existe, lo añadimos a la lista de conflictos.
            } else {
                itemService.save(item);  // Si el item no existe, lo guardamos en la base de datos.
                addedItems.add(item);
            }
        }

        if (!conflicts.isEmpty()) {
            // Crear una lista de los IDs o nombres de los ítems que están en conflicto
            List<String> conflictDetails = new ArrayList<>();
            for (Item conflictItem : conflicts) {
                conflictDetails.add("ID: " + conflictItem.getId() + ", Name: " + conflictItem.getName());
            }

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Some items already exist: " + conflictDetails);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(addedItems);
    }
}
