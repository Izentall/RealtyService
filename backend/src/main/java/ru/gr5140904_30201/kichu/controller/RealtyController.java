package ru.gr5140904_30201.kichu.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gr5140904_30201.kichu.model.PropertySearchCriteria;
import ru.gr5140904_30201.kichu.model.Realty;
import ru.gr5140904_30201.kichu.serivce.RealtyService;

import java.util.List;

@RestController
@RequestMapping("/api/realty")
@RequiredArgsConstructor
public class RealtyController {
    private final RealtyService realtyService;

    // Добавить недвижимость
    @PostMapping
    public ResponseEntity<Realty> addRealty(@RequestBody Realty realty) {
        realtyService.addRealty(realty);
        return new ResponseEntity<>(realty, HttpStatus.CREATED);
    }

    // Обновить недвижимость
    @PutMapping
    public ResponseEntity<?> updateRealty(@RequestBody Realty realty) {
        realtyService.updateRealty(realty);
        return ResponseEntity.ok().build();
    }

    // Удалить недвижимость
    @DeleteMapping("/{id}/{ownerId}")
    public ResponseEntity<?> deleteRealty(@PathVariable Long id, @PathVariable Long ownerId) {
        realtyService.deleteRealty(id, ownerId);
        return ResponseEntity.noContent().build();
    }

    // Получить всю недвижимость по ID владельца
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Realty>> getRealtyByOwner(@PathVariable Long ownerId) {
        List<Realty> realtyList = realtyService.getRealtyByOwner(ownerId);
        return ResponseEntity.ok(realtyList);
    }

    // Получить всю недвижимость по ID владельца
    @GetMapping("")
    public ResponseEntity<List<Realty>> getRealtyByIds(@RequestParam List<Long> ids) {
        List<Realty> realtyList = realtyService.getRealtyByIds(ids);
        return ResponseEntity.ok(realtyList);
    }

    @PostMapping("/search")
    public ResponseEntity<List<Realty>> searchProperties(@RequestBody PropertySearchCriteria criteria) {
        List<Realty> properties = realtyService.searchProperties(criteria);

        if (properties.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(properties);
        }
        return ResponseEntity.ok(properties);
    }
}

