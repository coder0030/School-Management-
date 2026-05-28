package com.example.demo.ControllerTesting;

import com.example.demo.Controller.StudentFeeController;  // ← ADD THIS IMPORT
import com.example.demo.DTO.StudentFeeResponseDTO;
import com.example.demo.ENTITY.Student;
import com.example.demo.Helper.Gender;
import com.example.demo.Helper.PaymentMode;
import com.example.demo.Helper.StudentFeeStatus;
import com.example.demo.RequestDTO.FeePaymentRequestDTO;
import com.example.demo.RequestDTO.StudentFeeRequestDTO;
import com.example.demo.Security.AuthUtil;
import com.example.demo.Security.JwtAuthFilter;
import com.example.demo.Security.SecurityUtil;
import com.example.demo.Security.WebSecurityConfiguration;
import com.example.demo.Service.StudentFeeService;
import com.example.demo.ServiceImpl.StudentFeeServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentFeeController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(WebSecurityConfiguration.class)
public class StudentFeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SecurityUtil securityUtil;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private StudentFeeService studentFeeService;

    private StudentFeeRequestDTO studentFeeRequestDTO;
    private StudentFeeResponseDTO studentFeeResponseDTO;
    private FeePaymentRequestDTO feePaymentRequestDTO;
    private List<StudentFeeResponseDTO> feeList;

    @BeforeEach
    void setup() {
        studentFeeRequestDTO = StudentFeeRequestDTO.builder()
                .studentId(1L)
                .classEntityId(1L)
                .feeStructureId(1L)
                .totalAmount(5000.0)
                .paidAmount(0.0)
                .remainingAmount(5000.0)
                .dueDate(LocalDate.now().plusMonths(1))
                .status("PENDING")
                .phone("9876543210")
                .build();

        studentFeeResponseDTO = StudentFeeResponseDTO.builder()
                .id(1L)
                .studentId(1L)
                .studentName("John Doe")
                .classEntityId(1L)
                .className("Class 10")
                .feeStructureId(1L)
                .feeStructureName("Annual Fee")
                .totalAmount(5000.0)
                .paidAmount(0.0)
                .remainingAmount(5000.0)
                .dueDate(LocalDate.now().plusMonths(1))
                .status(StudentFeeStatus.PENDING)
                .build();

        feePaymentRequestDTO = FeePaymentRequestDTO.builder()
                .studentFeeId(1L)
                .amountPaid(1000.0)
                .paymentMode(PaymentMode.CASH)
                .feeType("TUITION")
                .transactionId("TXN123456")
                .studentId(1L)
                .classId(1L)
                .rollNumber(101)
                .mobileNumber("9876543210")
                .build();

        feeList = Arrays.asList(studentFeeResponseDTO);
    }

    @Test
    @Order(1)
    @WithMockUser(roles = "ADMIN")
    void createStudentFee_WithValidRequest_ShouldReturnCreatedFee() throws Exception {
        when(studentFeeService.createStudentFee(any(StudentFeeRequestDTO.class)))
                .thenReturn(studentFeeResponseDTO);

        mockMvc.perform(post("/api/student-fees/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentFeeRequestDTO)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.studentName").value("John Doe"),
                        jsonPath("$.totalAmount").value(5000.0),
                        jsonPath("$.status").value("PENDING"));
    }

    @Test
    @Order(2)
    @WithMockUser(roles = "ACCOUNTANT")
    void getStudentFeeById_WithValidId_ShouldReturnFee() throws Exception {
        when(studentFeeService.getStudentFeeById(1L)).thenReturn(studentFeeResponseDTO);

        mockMvc.perform(get("/api/student-fees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.feeStructureId").value(1L))
                .andExpect(jsonPath("$.classEntityId").value(1L));  // ← FIXED: Use correct field name
    }

    @Test
    @Order(3)
    @WithMockUser(roles = "ADMIN")
    void updateStudentFee_WithValidRequest_ShouldReturnUpdated() throws Exception {
        studentFeeRequestDTO.setClassEntityId(11L);
        studentFeeResponseDTO.setClassEntityId(11L);
        when(studentFeeService.updateStudentFee(eq(1L), any(StudentFeeRequestDTO.class))).thenReturn(studentFeeResponseDTO);

        mockMvc.perform(put("/api/student-fees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentFeeRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.classEntityId").value(11L))  // ← FIXED: Use correct field
                .andExpect(jsonPath("$.totalAmount").value(5000.0));  // ← FIXED: Use totalAmount, not totalFees
    }

    @Test
    @Order(4)
    @WithMockUser(roles = "ACCOUNTANT")
    void getFeesByStudentId_WithValidStudentId_ShouldReturnList() throws Exception {
        when(studentFeeService.getFeesByStudentId(1L)).thenReturn(feeList);

        mockMvc.perform(get("/api/student-fees/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentId").value(1L))
                .andExpect(jsonPath("$[0].studentName").value("John Doe"));
    }

    @Test
    @Order(5)
    @WithMockUser(roles = "ACCOUNTANT")
    void payFeeByClassAndRoll_WithValidParams_ShouldReturnList() throws Exception {
        when(studentFeeService.getFeeByStudentClassAndRollNo(10L, 101)).thenReturn(feeList);

        mockMvc.perform(get("/api/student-fees/class/10/roll/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].classEntityId").value(1L));
    }

    @Test
    @Order(6)
    @WithMockUser(roles = "SUPERADMIN")
    void getAllStudentFees_ShouldReturnList() throws Exception {
        when(studentFeeService.getAllStudentFees()).thenReturn(feeList);

        mockMvc.perform(get("/api/student-fees/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$.length()").value(1));
    }
}