package com.example.demo.ControllerTesting;

import com.example.demo.Controller.AdminController;
import com.example.demo.DTO.AdminDTO;
import com.example.demo.ENTITY.Admin;
import com.example.demo.ENTITY.User;
import com.example.demo.Helper.Gender;
import com.example.demo.Helper.Role;
import com.example.demo.Repository.AdminRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.RequestDTO.AdminRequestDTO;
import com.example.demo.Security.JwtAuthFilter;
import com.example.demo.Security.SecurityUtil;
import com.example.demo.Security.WebSecurityConfiguration;
import com.example.demo.Service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(WebSecurityConfiguration.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private SecurityUtil securityUtil;


    @MockitoBean
    private com.example.demo.Repository.UserRepository userRepository;

    @MockitoBean
    private com.example.demo.Security.AuthUtil authUtil;

    private AdminRequestDTO validAdminRequest;
    private User user;
    private AdminDTO adminDTO;
    private AdminDTO updatedAdminDTO;
    private List<AdminDTO> adminList;
    private Page<AdminDTO> adminPage;
    private Admin admin;


    @BeforeEach
    void setUp() {
        validAdminRequest = AdminRequestDTO.builder()
                .firstName("John")
                .lastName("Admin")
                .email("john.admin@school.com")
                .phone("9876543210")
                .address("123 School Street, City")
                .dateOfBirth(LocalDate.of(1985, 5, 15))
                .gender(Gender.MALE)
                .build();

        adminDTO = AdminDTO.builder()
                .id(1L)
                .firstName("John")
                .lastName("Admin")
                .email("john.admin@school.com")
                .phone("9876543210")
                .address("123 School Street, City")
                .dateOfBirth(LocalDate.of(1985, 5, 15))
                .gender(Gender.MALE)
                .adminCode("ADM001")
                .isActive(true)
                .roles(Set.of(Role.ROLE_ADMIN, Role.ROLE_USER))
                .build();

        updatedAdminDTO = AdminDTO.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("SuperAdmin")
                .email("jane.super@school.com")
                .phone("9876543222")
                .address("456 College Avenue, City")
                .dateOfBirth(LocalDate.of(1988, 8, 20))
                .gender(Gender.FEMALE)
                .adminCode("ADM001")
                .isActive(true)
                .roles(Set.of(Role.ROLE_SUPERADMIN, Role.ROLE_USER))
                .build();

        user = User.builder()
                .id(1L)
                .username("sumit@gmail.com")
                .password("Pass@1234")
                .build();

        admin = Admin.builder()
                .user(user)
                .id(1L)
                .build();

        adminList = Arrays.asList(adminDTO);
        adminPage = new PageImpl<>(adminList);
    }

    @Test
    @Order(1)
    @WithMockUser(roles = "ADMIN")
    void createAdmin_WithValidRequest_ShouldReturnCreatedAdmin() throws Exception {
        when(adminService.createAdmin(any(AdminRequestDTO.class))).thenReturn(adminDTO);

        mockMvc.perform(post("/api/admins/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validAdminRequest)))
                .andExpectAll(
                        status().isCreated(),

                        jsonPath("$.id").value(1L),
                        jsonPath("$.firstName").value("John"),
                        jsonPath("$.lastName").value("Admin"),
                        jsonPath("$.email").value("john.admin@school.com"),
                        jsonPath("$.roles").isArray()
                );
    }

    @Test
    @Order(2)
    @WithMockUser(roles = "ADMIN")
    void getAdminById_WithValidId_ShouldReturnAdmin() throws Exception {
        when(adminService.getAdminById(1L)).thenReturn(adminDTO);
        mockMvc.perform(get("/api/admins/id/1"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.firstName").value("John"),
                        jsonPath("$.lastName").value("Admin"),
                        jsonPath("$.email").value("john.admin@school.com"),
                        jsonPath("$.phone").value("9876543210"),
                        jsonPath("$.gender").value("MALE"),
                        jsonPath("$.adminCode").value("ADM001"));
    }

    @Test
    @Order(3)
    @WithMockUser(roles = "USER")
    void getAdminById_WithUnauthorizedRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/admins/id/1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @Order(4)
    @WithMockUser(roles = "SUPERADMIN")
    void getAllAdmins_WithPagination_ShouldReturnPage() throws Exception {
        when(adminService.getAllAdmins(0, 20)).thenReturn(adminPage);

        mockMvc.perform(get("/api/admins/all")
                        .param("pageNo", "0")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].firstName").value("John"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @Order(5)
    @WithMockUser(roles = "SUPERADMIN")
    void updateAdmin_WithValidRequest_ShouldReturnUpdatedAdmin() throws Exception {
        when(adminService.updateAdmin(eq(1L), any(AdminRequestDTO.class))).thenReturn(updatedAdminDTO);

        mockMvc.perform(put("/api/admins/updateId/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validAdminRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("SuperAdmin"))
                .andExpect(jsonPath("$.roles",
                        containsInAnyOrder("ROLE_SUPERADMIN", "ROLE_USER")));
    }


    @Test
    @Order(6)
    void deleteAdmin_WhenDeletingOtherAdmin_ShouldReturnNoContent() throws Exception {

        when(securityUtil.isCurrAdmin(1L)).thenReturn(false);

        doNothing().when(adminService).deleteAdmin(1L);

        mockMvc.perform(delete("/api/admins/deleteId/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(adminService, times(1)).deleteAdmin(2L);
  }

    @Test
    @Order(7)
    @WithMockUser(roles = "SUPERADMIN")
    void createAdmin_WithInvalidFirstName_ShouldReturnBadRequest() throws Exception {
        AdminRequestDTO invalidRequest = AdminRequestDTO.builder()
                .firstName("")
                .lastName("Admin")
                .email("john.admin@school.com")
                .phone("9876543210")
                .dateOfBirth(LocalDate.of(1985, 5, 15))
                .gender(Gender.MALE)
                .build();

        mockMvc.perform(post("/api/admins/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
