//package com.revpasswordmanager.controller;
//
//import com.revpasswordmanager.dto.VaultEntryDTO;
//import com.revpasswordmanager.service.VaultService;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/vault")
//public class VaultController {
//
//    private final VaultService vaultService;
//
//    public VaultController(VaultService vaultService) {
//        this.vaultService = vaultService;
//    }
//    
//    @PostMapping
//    public ResponseEntity<String> addVaultEntry(@RequestBody VaultEntryDTO dto) {
//        vaultService.addVaultEntry(dto);
//        return ResponseEntity.ok("Vault entry added successfully");
//    }
//    
//
//    @GetMapping("/{userId}")
//    public ResponseEntity<List<VaultEntryDTO>> getUserVault(@PathVariable Long userId) {
//
//        return ResponseEntity.ok(vaultService.getAllEntries(userId));
//    }
//
//    @PutMapping("/{entryId}")
//    public ResponseEntity<String> updateVaultEntry(
//            @PathVariable Long entryId,
//            @RequestBody VaultEntryDTO dto) {
//
//        vaultService.updateVaultEntry(entryId, dto);
//
//        return ResponseEntity.ok("Vault entry updated successfully");
//    }
//
//    @DeleteMapping("/{entryId}")
//    public ResponseEntity<String> deleteVaultEntry(@PathVariable Long entryId) {
//
//        vaultService.deleteVaultEntry(entryId);
//
//        return ResponseEntity.ok("Vault entry deleted successfully");
//    }
//
//    @PutMapping("/{entryId}/favorite")
//    public ResponseEntity<String> updateFavoriteStatus(
//            @PathVariable Long entryId,
//            @RequestParam Boolean favorite) {
//
//        vaultService.markAsFavorite(entryId, favorite);
//
//        return ResponseEntity.ok("Favorite status updated");
//    }
//    
//}


package com.revpasswordmanager.controller;

import com.revpasswordmanager.dto.VaultEntryDTO;
import com.revpasswordmanager.service.VaultService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vault")
public class VaultController {

    private final VaultService vaultService;

    public VaultController(VaultService vaultService) {
        this.vaultService = vaultService;
    }
    
    @PostMapping
    public ResponseEntity<String> addVaultEntry(@RequestBody VaultEntryDTO dto) {
        try {
            vaultService.addVaultEntry(dto);
            return ResponseEntity.ok("Vault entry added successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add entry. Please try again.");
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<VaultEntryDTO>> getUserVault(@PathVariable Long userId) {
        return ResponseEntity.ok(vaultService.getAllEntries(userId));
    }

    @PutMapping("/{entryId}")
    public ResponseEntity<String> updateVaultEntry(
            @PathVariable Long entryId,
            @RequestBody VaultEntryDTO dto) {
        try {
            vaultService.updateVaultEntry(entryId, dto);
            return ResponseEntity.ok("Vault entry updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update entry. Please try again.");
        }
    }

    @DeleteMapping("/{entryId}")
    public ResponseEntity<String> deleteVaultEntry(@PathVariable Long entryId) {
        vaultService.deleteVaultEntry(entryId);
        return ResponseEntity.ok("Vault entry deleted successfully");
    }

    @PutMapping("/{entryId}/favorite")
    public ResponseEntity<String> updateFavoriteStatus(
            @PathVariable Long entryId,
            @RequestParam Boolean favorite) {
        vaultService.markAsFavorite(entryId, favorite);
        return ResponseEntity.ok("Favorite status updated");
    }
    
}