package com.example.demo.Controller;

import com.example.demo.DTO.AccountantDTO;
import com.example.demo.Helper.Role;
import com.example.demo.RequestDTO.AccountantRequestDTO;
import com.example.demo.Service.AccountantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accountants")
@RequiredArgsConstructor
public class AccountantController {

    private final AccountantService accountantService;

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @PostMapping("/create")
    public ResponseEntity<AccountantDTO> createAccountant(
            @Valid @RequestBody AccountantRequestDTO requestDTO) {

        AccountantDTO response = accountantService.createAccountant(requestDTO);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("""
            hasRole('SUPERADMIN') or
            (hasRole('ADMIN') and @securityUtil.isCurrAccountant(#id))
            """)
    @GetMapping("/id/{id}")
    public ResponseEntity<AccountantDTO> getAccountantById(@PathVariable Long id) {

        AccountantDTO response = accountantService.getAccountantById(id);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/code/{accountantCode}")
    public ResponseEntity<AccountantDTO> getAccountantByCode(
            @PathVariable String accountantCode) {

        AccountantDTO response =
                accountantService.getAccountantByCode(accountantCode);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @GetMapping("/email/{email}")
    public ResponseEntity<AccountantDTO> getAccountantByEmail(
            @PathVariable String email) {

        AccountantDTO response =
                accountantService.getAccountantByEmail(email);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    @GetMapping("/all")
    public ResponseEntity<Page<AccountantDTO>> getAllAccountants(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "20") int pageSize) {

        Page<AccountantDTO> responses =
                accountantService.getAllAccountants(pageNo, pageSize);

        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    @GetMapping("/role/{role}")
    public ResponseEntity<List<AccountantDTO>> getAccountantsByRole(
            @PathVariable String role) {

        List<AccountantDTO> responses =
                accountantService.getAccountantsByRole(role);

        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @GetMapping("/active")
    public ResponseEntity<List<AccountantDTO>> getActiveAccountants() {

        List<AccountantDTO> responses =
                accountantService.getActiveAccountants();

        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("""
            hasRole('SUPERADMIN')
            or
            @securityUtil.isCurrAccountant(#id)
            """)
    @PutMapping("/updateId/{id}")
    public ResponseEntity<AccountantDTO> updateAccountant(
            @PathVariable Long id,
            @Valid @RequestBody AccountantRequestDTO requestDTO) {

        AccountantDTO response =
                accountantService.updateAccountant(id, requestDTO);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("""
            hasRole('SUPERADMIN')
            or
            @securityUtil.isCurrAccountant(#id)
            """)
    @PatchMapping("/partialUpdateId/{id}")
    public ResponseEntity<AccountantDTO> partialUpdateAccountant(
            @PathVariable Long id,
            @Valid @RequestBody AccountantRequestDTO requestDTO) {

        AccountantDTO response =
                accountantService.partialUpdateAccountant(id, requestDTO);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/deleteId/{id}")
    @PreAuthorize("hasRole('SUPERADMIN') and not @securityUtil.isCurrAccountant(#id)")
    public ResponseEntity<Void> deleteAccountant(@PathVariable Long id) {
        accountantService.deleteAccountant(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<AccountantDTO> activateAccountant(
            @PathVariable Long id) {

        AccountantDTO response =
                accountantService.activateAccountant(id);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('SUPERADMIN') and not @securityUtil.isCurrAccountant(#id)")
    public ResponseEntity<AccountantDTO> deactivateAccountant(@PathVariable Long id) {
        AccountantDTO response = accountantService.deactivateAccountant(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("""
            hasRole('SUPERADMIN')
            and not @securityUtil.isCurrAccountant(#accountantId)
            and #role.name() != 'ROLE_SUPERADMIN'
            """)
    @PostMapping("/{accountantId}/addRole")
    public ResponseEntity<AccountantDTO> addRoleToAccountant(
            @PathVariable Long accountantId,
            @RequestBody Role role) {

        AccountantDTO accountantDTO =
                accountantService.addRoleToAccountant(accountantId, role);

        return ResponseEntity.ok(accountantDTO);
    }

    @PreAuthorize("""
            hasRole('SUPERADMIN')
            and not @securityUtil.isCurrAccountant(#accountantId)
            """)
    @PostMapping("/{accountantId}/removeRole")
    public ResponseEntity<AccountantDTO> removeRoleFromAccountant(
            @PathVariable Long accountantId,
            @RequestBody Role role) {

        AccountantDTO accountantDTO =
                accountantService.removeRoleFromAccountant(accountantId, role);

        return ResponseEntity.ok(accountantDTO);
    }
}