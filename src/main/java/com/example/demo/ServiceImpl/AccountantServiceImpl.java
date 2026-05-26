package com.example.demo.ServiceImpl;

import com.example.demo.DTO.AccountantDTO;
import com.example.demo.ENTITY.Accountant;
import com.example.demo.ENTITY.User;
import com.example.demo.Helper.Role;
import com.example.demo.Mapper.AccountantMapper;
import com.example.demo.MyException.AccessDeniedException;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.Repository.AccountantRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.RequestDTO.AccountantRequestDTO;
import com.example.demo.Security.SecurityUtil;
import com.example.demo.Service.AccountantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AccountantServiceImpl implements AccountantService {
    private final AllRepositoryMethods allRepositoryMethods;
    private final AccountantRepository accountantRepository;
    private final AccountantMapper accountantMapper;
    private final UserRepository userRepository;

    private void checkExistenceForCreate(String email, String phone) {
        if (email != null && accountantRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already exists: " + email);
        }
        if (phone != null && accountantRepository.existsByPhone(phone)) {
            throw new BadRequestException("Phone already exists: " + phone);
        }
    }

    private void checkExistenceForUpdate(String email, String phone, Long excludeAccountantId) {
        if (email != null && accountantRepository.existsByEmailAndIdNot(email, excludeAccountantId)) {
            throw new BadRequestException("Email already exists: " + email);
        }
        if (phone != null && accountantRepository.existsByPhoneAndIdNot(phone, excludeAccountantId)) {
            throw new BadRequestException("Phone already exists: " + phone);
        }
    }

    private void checkRolesExist(Set<Role> roles) {
        User user = SecurityUtil.getCurrentUserDetails();

        boolean valid = roles.stream()
                .allMatch(role -> user.getRoles().contains(role));

        if (!valid) {
            throw new AccessDeniedException("Access denied!, Required roles not found");
        }
    }

    @Override
    public AccountantDTO createAccountant(AccountantRequestDTO requestDTO) {
        checkExistenceForCreate(requestDTO.getEmail(), requestDTO.getPhone());
        User user = userRepository.findByEmailOrUsernameAndIsActive(requestDTO.getEmail(), requestDTO.getEmail(), true)
                .orElseThrow(() -> new BadRequestException
                        ("No active user account found with this email. User must signup first."));

        checkRolesExist(Set.of(Role.ROLE_ADMIN, Role.ROLE_SUPERADMIN));

        Accountant accountant = new Accountant();
        accountant = accountantMapper.toEntity(requestDTO, accountant);
        accountant.setUser(user);

        Accountant saved = accountantRepository.save(accountant);
        return accountantMapper.toDto(saved);
    }

    @Override
    public AccountantDTO getAccountantById(Long id) {
        Accountant accountant = allRepositoryMethods.getAccountantById(id);
        return accountantMapper.toDto(accountant);
    }

    @Override
    public AccountantDTO getAccountantByCode(String accountantCode) {
        // Accountant accountant = allRepositoryMethods.getAccountantByCode(accountantCode);
        return null;
    }

    @Override
    public AccountantDTO getAccountantByEmail(String email) {
        Accountant accountant = allRepositoryMethods.getAccountantByEmail(email);
        return accountantMapper.toDto(accountant);
    }

    @Override
    public Page<AccountantDTO> getAllAccountants(int pageNo, int pageSize) {
        checkRolesExist(Set.of(Role.ROLE_ADMIN, Role.ROLE_SUPERADMIN));

        if (pageSize > AttendanceServiceImpl.maxPageSize) pageSize = AttendanceServiceImpl.maxPageSize;

        Sort sort = Sort.by("assigned").descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Accountant> accountantPage = accountantRepository.findAll(pageable);

        if (accountantPage.getContent().isEmpty()) {
            return Page.empty(pageable);
        }

        return accountantPage.map(accountantMapper::toDto);
    }

    @Override
    public List<AccountantDTO> getAccountantsByRole(String role) {
        Role roles = Role.getRoles(role);
        List<Accountant> accountants = accountantRepository.findByUser_RolesAndIsActive(roles, true);
        if (accountants.isEmpty()) {
            return List.of();
        }

        return accountantMapper.toDtoList(accountants);
    }

    @Override
    public List<AccountantDTO> getActiveAccountants() {
        checkRolesExist(Set.of(Role.ROLE_ADMIN, Role.ROLE_SUPERADMIN));

        List<Accountant> accountants = accountantRepository.findAll();

        if (accountants.isEmpty()) {
            return List.of();
        }

        List<Accountant> activeAccountant = accountants.stream()
                .filter(a -> a.getIsActive().equals(true))
                .toList();

        return accountantMapper.toDtoList(activeAccountant);
    }

    @Override
    public AccountantDTO updateAccountant(Long id, AccountantRequestDTO requestDTO) {
        Accountant accountant = allRepositoryMethods.getAccountantById(id);
        String email = accountant.getEmail(), phone = accountant.getPhone();

        if (requestDTO.getEmail() != null && !requestDTO.getEmail().equals(email)) {
            email = requestDTO.getEmail();
        }

        if (requestDTO.getPhone() != null && !requestDTO.getPhone().equals(phone)) {
            phone = requestDTO.getPhone();
        }

        checkExistenceForUpdate(email, phone, id);
        accountant.setEmail(email);
        accountant.setPhone(phone);

        accountant = accountantMapper.updateToEntity(requestDTO, accountant);
        Accountant saved = accountantRepository.save(accountant);
        return accountantMapper.toDto(saved);
    }

    @Override
    public AccountantDTO partialUpdateAccountant(Long id, AccountantRequestDTO requestDTO) {

        Accountant accountant = allRepositoryMethods.getAccountantById(id);
        String email = accountant.getEmail(), phone = accountant.getPhone();

        if (requestDTO.getEmail() != null && !requestDTO.getEmail().equals(email)) {
            email = requestDTO.getEmail();
        }

        if (requestDTO.getPhone() != null && !requestDTO.getPhone().equals(phone)) {
            phone = requestDTO.getPhone();
        }

        checkExistenceForUpdate(email, phone, id);
        accountant.setEmail(email);
        accountant.setPhone(phone);

        accountant = accountantMapper.updateToEntity(requestDTO, accountant);
        Accountant saved = accountantRepository.save(accountant);
        return accountantMapper.toDto(saved);
    }

    @Override
    public void deleteAccountant(Long id) {
        Accountant accountant = allRepositoryMethods.getAccountantById(id);
        accountantRepository.delete(accountant);
    }

    @Override
    public AccountantDTO activateAccountant(Long id) {
        Accountant accountant = allRepositoryMethods.getAccountantById(id);
        if (accountant.getIsActive().equals(false)) accountant.setIsActive(true);
        return accountantMapper.toDto(accountantRepository.save(accountant));
    }

    @Override
    public AccountantDTO deactivateAccountant(Long id) {
        Accountant accountant = allRepositoryMethods.getAccountantById(id);
        if (accountant.getIsActive().equals(true)) accountant.setIsActive(false);
        return accountantMapper.toDto(accountantRepository.save(accountant));
    }

    @Override
    public AccountantDTO addRoleToAccountant(Long accountantId, Role role) {
        Accountant accountant = allRepositoryMethods.getAccountantById(accountantId);
        accountant.addRoles(role);
        return accountantMapper.toDto(accountantRepository.save(accountant));
    }

    @Override
    public AccountantDTO removeRoleFromAccountant(Long accountantId, Role role) {
        Accountant accountant = allRepositoryMethods.getAccountantById(accountantId);
        accountant.removeRoles(role);
        return accountantMapper.toDto(accountantRepository.save(accountant));
    }
}