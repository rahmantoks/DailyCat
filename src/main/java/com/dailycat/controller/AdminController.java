package com.dailycat.controller;

import com.dailycat.service.CatSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CatSyncService catSyncService;

    /**
     * Manual trigger to sync cats from The Cat API
     * Example: POST /api/admin/sync?count=10
     */
    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncCats(@RequestParam(defaultValue = "10") int count) {
        try {
            int savedCount = catSyncService.syncCatsNow(count);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Successfully synced " + savedCount + " cats",
                "count", savedCount
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}
