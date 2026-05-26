package com.example.demo.ServiceImpl;

import com.example.demo.DTO.LibrarianDTO;
import com.example.demo.ENTITY.Librarian;
import com.example.demo.ENTITY.User;
import com.example.demo.Helper.Role;
import com.example.demo.Mapper.LibrarianMapper;
import com.example.demo.MyException.AccessDeniedException;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.Repository.LibrarianRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.RequestDTO.LibrarianRequestDTO;
import com.example.demo.Security.SecurityUtil;
import com.example.demo.Service.LibrarianService;
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
public class LibrarianServiceImpl implements LibrarianService {
    private final AllRepositoryMethods allRepositoryMethods;
    private final LibrarianRepository librarianRepository;
    private final LibrarianMapper librarianMapper;
    private final UserRepository userRepository;


    private void checkExistenceForCreate(String email, String phone) {
        if (email != null && librarianRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already exists: " + email);
        }
        if (phone != null && librarianRepository.existsByPhone(phone)) {
            throw new BadRequestException("Phone already exists: " + phone);
        }
    }

    private void checkExistenceForUpdate(String email, String phone, Long excludeStudentId) {
        if (email != null && librarianRepository.existsByEmailAndIdNot(email, excludeStudentId)) {
            throw new BadRequestException("Email already exists: " + email);
        }
        if (phone != null && librarianRepository.existsByPhoneAndIdNot(phone, excludeStudentId)) {
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
    public LibrarianDTO createLibrarian(LibrarianRequestDTO requestDTO) {
        checkExistenceForCreate(requestDTO.getEmail(), requestDTO.getPhone());
        User user = userRepository.findByEmailOrUsernameAndIsActive(requestDTO.getEmail(), requestDTO.getEmail(), true)
                .orElseThrow(() -> new BadRequestException
                ("No active user account found with this email. User must signup first."));

        checkRolesExist(Set.of(Role.ROLE_ADMIN, Role.ROLE_SUPERADMIN));


        Librarian librarian = new Librarian();
        librarian = librarianMapper.toEntity(requestDTO, librarian);
        librarian.setUser(user);

        Librarian saved = librarianRepository.save(librarian);
        return librarianMapper.toDto(librarian);
    }

    @Override
    public LibrarianDTO getLibrarianById(Long id) {
        Librarian librarian = allRepositoryMethods.getLibrarianById(id);
        return librarianMapper.toDto(librarian);
    }

    @Override
    public LibrarianDTO getLibrarianByCode(String librarianCode) {
       // Librarian librarian = allRepositoryMethods.getLibrarianByCode(librarianCode);
        return null;
    }

    @Override
    public LibrarianDTO getLibrarianByEmail(String email) {
        Librarian librarian = allRepositoryMethods.getLibrarianByEmail(email);
        return librarianMapper.toDto(librarian);
    }

    @Override
    public Page<LibrarianDTO> getAllLibrarians(int pageNo, int pageSize) {
        checkRolesExist(Set.of(Role.ROLE_ADMIN, Role.ROLE_SUPERADMIN));
        if (pageSize > AttendanceServiceImpl.maxPageSize) pageSize = AttendanceServiceImpl.maxPageSize;

        Sort sort = Sort.by("assigned").descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);


        Page<Librarian> LibrarianPage = librarianRepository.findAll(pageable);

        if (LibrarianPage.getContent().isEmpty()) {
            return Page.empty(pageable);
        }

        return LibrarianPage.map(librarianMapper::toDto);
    }

    @Override
    public List<LibrarianDTO> getLibrariansByRole(String role) {
        Role roles = Role.getRoles(role);
        List<Librarian> librarians = librarianRepository.findByUser_RolesAndIsActive(roles, true);
        if(librarians.isEmpty()) {
            return List.of();
        }

        return librarianMapper.toDtoList(librarians);
    }

    @Override
    public List<LibrarianDTO> getActiveLibrarians() {
        checkRolesExist(Set.of(Role.ROLE_ADMIN, Role.ROLE_SUPERADMIN));
        List<Librarian> librarians = librarianRepository.findAll();

        if(librarians.isEmpty()) {
            return List.of();
        }

        List<Librarian> activeLibrarian = librarians.stream()
                .filter(l -> l.getIsActive().equals(true))
                .toList();

        return librarianMapper.toDtoList(activeLibrarian);
    }

    @Override
    public LibrarianDTO updateLibrarian(Long id, LibrarianRequestDTO requestDTO) {
        Librarian librarian = allRepositoryMethods.getLibrarianById(id);
        String email = librarian.getEmail(), phone = librarian.getPhone();

        if(requestDTO.getEmail() != null && !requestDTO.getEmail().equals(email)) {
            email = requestDTO.getEmail();
        }

        if(requestDTO.getPhone() != null && !requestDTO.getPhone().equals(phone)) {
            phone = requestDTO.getPhone();
        }

        checkExistenceForUpdate(email, phone, id);
        librarian.setEmail(email);
        librarian.setPhone(phone);

        librarian = librarianMapper.updateToEntity(requestDTO, librarian);
        Librarian saved = librarianRepository.save(librarian);
        return librarianMapper.toDto(saved);
    }

    @Override
    public LibrarianDTO partialUpdateLibrarian(Long id, LibrarianRequestDTO requestDTO) {
        Librarian librarian = allRepositoryMethods.getLibrarianById(id);
        String email = librarian.getEmail(), phone = librarian.getPhone();

        if(requestDTO.getEmail() != null && !requestDTO.getEmail().equals(email)) {
            email = requestDTO.getEmail();
        }

        if(requestDTO.getPhone() != null && !requestDTO.getPhone().equals(phone)) {
            phone = requestDTO.getPhone();
        }

        checkExistenceForUpdate(email, phone, id);
        librarian.setEmail(email);
        librarian.setPhone(phone);

        librarian = librarianMapper.updateToEntity(requestDTO, librarian);
        Librarian saved = librarianRepository.save(librarian);
        return librarianMapper.toDto(saved);
    }

    @Override
    public void deleteLibrarian(Long id) {
     Librarian librarian = allRepositoryMethods.getLibrarianById(id);
     librarianRepository.delete(librarian);
    }

    @Override
    public LibrarianDTO activateLibrarian(Long id) {
        Librarian librarian = allRepositoryMethods.getLibrarianById(id);
        if(librarian.getIsActive().equals(false)) librarian.setIsActive(true);
        return librarianMapper.toDto(librarianRepository.save(librarian));
    }

    @Override
    public LibrarianDTO deactivateLibrarian(Long id) {
        Librarian librarian = allRepositoryMethods.getLibrarianById(id);
        if(librarian.getIsActive().equals(true)) librarian.setIsActive(false);
        return librarianMapper.toDto(librarianRepository.save(librarian));    }

    @Override
    public LibrarianDTO addRoleToLibrarian(Long librarianId, Role role) {
        Librarian librarian = allRepositoryMethods.getLibrarianById(librarianId);
        librarian.addRoles(role);
        return librarianMapper.toDto(librarianRepository.save(librarian));
    }

    @Override
    public LibrarianDTO removeRoleFromLibrarian(Long librarianId, Role role) {
        Librarian librarian = allRepositoryMethods.getLibrarianById(librarianId);
        librarian.removeRoles(role);
        return librarianMapper.toDto(librarianRepository.save(librarian));
    }
}
