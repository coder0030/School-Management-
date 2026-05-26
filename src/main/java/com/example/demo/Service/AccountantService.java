package com.example.demo.Service;

import com.example.demo.DTO.AccountantDTO;
import com.example.demo.Helper.Role;
import com.example.demo.RequestDTO.AccountantRequestDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AccountantService {
    AccountantDTO createAccountant(@Valid AccountantRequestDTO requestDTO);

    AccountantDTO getAccountantById(Long id);

    AccountantDTO getAccountantByEmail(String email);

    AccountantDTO getAccountantByCode(String accountantCode);

    Page<AccountantDTO> getAllAccountants(int pageNo, int pageSize);

    List<AccountantDTO> getAccountantsByRole(String role);

    List<AccountantDTO> getActiveAccountants();

    AccountantDTO updateAccountant(Long id, @Valid AccountantRequestDTO requestDTO);

    AccountantDTO partialUpdateAccountant(Long id, @Valid AccountantRequestDTO requestDTO);

    void deleteAccountant(Long id);

    AccountantDTO activateAccountant(Long id);

    AccountantDTO deactivateAccountant(Long id);

    AccountantDTO addRoleToAccountant(Long accountantId, Role role);

    AccountantDTO removeRoleFromAccountant(Long accountantId, Role role);
}
