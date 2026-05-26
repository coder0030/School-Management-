package com.example.demo.Controller;

import com.example.demo.DTO.FeeStructureDTO;
import com.example.demo.RequestDTO.FeeStructureRequestDTO;
import com.example.demo.Service.FeeStructureService;
import com.example.demo.ServiceImpl.FeeStructureServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/fee-structures")
public class FeeStructureController {
    private final FeeStructureService feeStructureService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public ResponseEntity<FeeStructureDTO> create(@Valid @RequestBody FeeStructureRequestDTO dto) {
        return ResponseEntity.ok(feeStructureService.create(dto));
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public ResponseEntity<FeeStructureDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(feeStructureService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public ResponseEntity<Page<FeeStructureDTO>> getAll(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {

        return ResponseEntity.ok(feeStructureService.getAll(pageNo, pageSize));
    }

    @PutMapping("/updateId/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    public ResponseEntity<FeeStructureDTO> update(@PathVariable Long id,
                                                  @Valid @RequestBody FeeStructureRequestDTO dto) {
        return ResponseEntity.ok(feeStructureService.updateById(id, dto));
    }

    @PatchMapping("/partialUpdateId/{id}")
    public ResponseEntity<FeeStructureDTO> partialUpdate(@PathVariable Long id,
                                                         @RequestBody FeeStructureRequestDTO dto) {
        return ResponseEntity.ok(feeStructureService.partialUpdateById(id, dto));
    }

    @DeleteMapping("/deleteId/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        feeStructureService.deleteById(id);
        return ResponseEntity.ok("Fee structure deleted successfully");
    }

    @DeleteMapping("/all")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<Void> deleteAll() {
        feeStructureService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
