package com.example.demo.ServiceLayerTesting;

import com.example.demo.DTO.TeacherDTO;
import com.example.demo.ENTITY.*;
import com.example.demo.Helper.Role;
import com.example.demo.Mapper.TeacherMapper;
import com.example.demo.MyException.AccessDeniedException;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.Repository.*;
import com.example.demo.RequestDTO.TeacherRequestDTO;
import com.example.demo.Security.SecurityUtil;
import com.example.demo.ServiceImpl.AllRepositoryMethods;
import com.example.demo.ServiceImpl.TeacherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceImplTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private TeacherMapper teacherMapper;

    @Mock
    private AllRepositoryMethods allRepositoryMethods;

    @Mock
    private ClassTeacherRepository classTeacherRepository;

    @Mock
    private TeacherSubjectRepository teacherSubjectRepository;

    @Mock
    private ClassSubjectRepository classSubjectRepository;

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TeacherServiceImpl teacherService;

    private Teacher teacher;
    private TeacherDTO teacherDTO;
    private TeacherRequestDTO teacherRequestDTO;
    private User user;
    private ClassEntity classEntity;
    private Subject subject;
    private TeacherSubject teacherSubject;
    private Long teacherId;
    private Long subjectId;
    private Long classId;

    @BeforeEach
    void setUp() {
        teacherId = 1L;
        subjectId = 1L;
        classId = 10L;

        user = User.builder()
                .id(1L)
                .roles(new HashSet<>(Set.of(Role.ROLE_USER)))
                .username("john.doe@example.com")
                .email("john.doe@example.com")
                .password("1234")
                .build();

        teacher = Teacher.builder()
                .id(teacherId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .address("123 Main St")
                .user(user)
                .role(Role.ROLE_TEACHER)
                .teacherSubjects(new ArrayList<>())
                .build();

        teacherDTO = TeacherDTO.builder()
                .id(teacherId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .address("123 Main St")
                .role(Role.ROLE_TEACHER)
                .build();

        teacherRequestDTO = TeacherRequestDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .address("123 Main St")
                .role(Role.ROLE_TEACHER)
                .build();

        classEntity = ClassEntity.builder()
                .id(classId)
                .teacherSubjectList(new ArrayList<>())
                .build();

        subject = Subject.builder()
                .id(subjectId)
                .teacherSubjects(new ArrayList<>())
                .build();

        teacherSubject = TeacherSubject.builder()
                .teacher(teacher)
                .subject(subject)
                .classEntity(classEntity)
                .build();
    }


    @Test
    @DisplayName("Should create teacher successfully")
    @Order(1)
    void createTeacher_Success() {
        when(userRepository.findByEmailOrUsernameAndIsActive(anyString(), anyString(),
                eq(true))).thenReturn(Optional.ofNullable(user));

        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);

        when(teacherMapper.updateEntityFromRequest(any(Teacher.class), any(TeacherRequestDTO.class)))
                .thenReturn(teacher);

        when(teacherMapper.toDto(any(Teacher.class))).thenReturn(teacherDTO);

        TeacherDTO result = teacherService.createTeacher(teacherRequestDTO);

        assertNotNull(result);
        assertEquals(teacherDTO.getEmail(), result.getEmail());
        assertEquals(teacherDTO.getId(), result.getId());

        verify(teacherRepository, times(1)).save(any(Teacher.class));
        verify(userRepository, times(1)).findByEmailOrUsernameAndIsActive(anyString(), anyString(), eq(true));
        verify(teacherMapper, times(1)).toDto(any());
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    @Order(2)
    void createTeacher_EmailAlreadyExists() {
        when(teacherRepository.existsByEmail(anyString())).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> {
            teacherService.createTeacher(teacherRequestDTO);
        });

        assertEquals("Email already exists.", ex.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("Should throw exception when phone already exists")
    void createTeacher_PhoneAlreadyExists() {
        when(teacherRepository.existsByPhone(anyString())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> {
            teacherService.createTeacher(teacherRequestDTO);
        });
    }


    @Test
    @Order(4)
    @DisplayName("Should throw exception when user not found")
    void createTeacher_UserNotFound() {
        when(userRepository.findByEmailOrUsernameAndIsActive(anyString(), anyString(), eq(true)))
                .thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> {
            teacherService.createTeacher(teacherRequestDTO);
        });
    }


    @Test
    @Order(5)
    @DisplayName("Should update teacher successfully as admin")
    void updateTeacher_Success_AsAdmin() {
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(SecurityUtil::isAdmin).thenReturn(true);
            securityUtil.when(() -> SecurityUtil.isOwner(anyLong())).thenReturn(false);

            when(allRepositoryMethods.getTeacherById(teacherId)).thenReturn(teacher);
            when(teacherMapper.UpdateToEntity(any(TeacherRequestDTO.class), any(Teacher.class)))
                    .thenReturn(teacher);
            when(teacherMapper.updateEntityFromRequest(any(Teacher.class), any(TeacherRequestDTO.class)))
                    .thenReturn(teacher);
            when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);
            when(teacherMapper.toDto(any(Teacher.class))).thenReturn(teacherDTO);

            TeacherDTO result = teacherService.updateTeacher(teacherId, teacherRequestDTO);

            assertNotNull(result);
            verify(teacherRepository, times(1)).save(any(Teacher.class));
        }
    }


    @Test
    @Order(6)
    @DisplayName("Should throw AccessDeniedException when not admin or owner")
    void updateTeacher_AccessDenied() {
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(SecurityUtil::isAdmin).thenReturn(false);
            securityUtil.when(() -> SecurityUtil.isOwner(anyLong())).thenReturn(false);

            when(allRepositoryMethods.getTeacherById(teacherId)).thenReturn(teacher);

            assertThrows(AccessDeniedException.class, () -> {
                teacherService.updateTeacher(teacherId, teacherRequestDTO);
            });
        }
    }


    @Test
    @Order(7)
    @DisplayName("Should delete teacher successfully")
    void deleteTeacher_Success() {
        when(allRepositoryMethods.getTeacherById(teacherId)).thenReturn(teacher);
        doNothing().when(teacherRepository).delete(any(Teacher.class));

        assertDoesNotThrow(() -> teacherService.deleteTeacher(teacherId));
        verify(teacherRepository, times(1)).delete(teacher);
    }


    @Test
    @Order(8)
    @DisplayName("Should get teacher by id successfully")
    void getTeacherById_Success() {
        when(allRepositoryMethods.getTeacherById(teacherId)).thenReturn(teacher);
        when(teacherMapper.toDto(any(Teacher.class))).thenReturn(teacherDTO);

        TeacherDTO result = teacherService.getTeacherById(teacherId);

        assertNotNull(result);
        assertEquals(teacherId, result.getId());
    }


    @Test
    @Order(9)
    @DisplayName("Should get all teachers successfully")
    void getAllTeachers_Success() {
        List<Teacher> teachers = Arrays.asList(teacher);
        List<TeacherDTO> teacherDTOs = Arrays.asList(teacherDTO);

        when(teacherRepository.findAll()).thenReturn(teachers);
        when(teacherMapper.toDtoList(anyList())).thenReturn(teacherDTOs);

        List<TeacherDTO> result = teacherService.getAllTeacher();

        assertNotNull(result);
        assertEquals(1, result.size());
    }


    @Test
    @Order(10)
    @DisplayName("Should return empty list when no teachers exist")
    void getAllTeachers_EmptyList() {
        when(teacherRepository.findAll()).thenReturn(List.of());

        List<TeacherDTO> result = teacherService.getAllTeacher();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    @Order(11)
    @DisplayName("Should assign teacher subject to class successfully")
    void assignTeacherSubjectToClass_Success() {
        when(allRepositoryMethods.getTeacherById(teacherId)).thenReturn(teacher);
        when(allRepositoryMethods.getSubjectById(subjectId)).thenReturn(subject);
        when(allRepositoryMethods.getClassById(classId)).thenReturn(classEntity);
        when(classTeacherRepository.existsByTeacher_IdAndClassEntity_Id(teacherId, classId))
                .thenReturn(true);
        when(classSubjectRepository.existsBySubject_IdAndClassEntity_Id(subjectId, classId))
                .thenReturn(true);
        when(teacherSubjectRepository.existsByTeacher_IdAndSubject_IdAndClassEntity_Id(teacherId, subjectId, classId))
                .thenReturn(false);
        when(teacherSubjectRepository.save(any(TeacherSubject.class))).thenReturn(teacherSubject);

        assertDoesNotThrow(() ->
                teacherService.assignTeacherSubjectToClass(teacherId, subjectId, classId)
        );
        verify(teacherSubjectRepository, times(1)).save(any(TeacherSubject.class));
    }


    @Test
    @Order(12)
    @DisplayName("Should throw exception when teacher not in class")
    void assignTeacherSubjectToClass_TeacherNotInClass() {
        when(classTeacherRepository.existsByTeacher_IdAndClassEntity_Id(teacherId, classId))
                .thenReturn(false);

        assertThrows(BadRequestException.class, () -> {
            teacherService.assignTeacherSubjectToClass(teacherId, subjectId, classId);
        });
    }


    @Test
    @Order(13)
    @DisplayName("Should add role to teacher successfully")
    void addRoleToTeacher_Success() {
        when(allRepositoryMethods.getTeacherById(teacherId)).thenReturn(teacher);
        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);
        when(teacherMapper.toDto(any(Teacher.class))).thenReturn(teacherDTO);

        TeacherDTO result = teacherService.addRoleToTeacher(teacherId, Role.ROLE_ADMIN);

        assertNotNull(result);
        verify(teacherRepository, times(1)).save(any(Teacher.class));
    }


    @Test
    @Order(14)
    @DisplayName("Should throw exception when role already assigned")
    void addRoleToTeacher_RoleAlreadyExists() {
        teacher.addRoles(Role.ROLE_ADMIN);
        when(allRepositoryMethods.getTeacherById(teacherId)).thenReturn(teacher);

        assertThrows(BadRequestException.class, () -> {
            teacherService.addRoleToTeacher(teacherId, Role.ROLE_ADMIN);
        });
    }


    @Test
    @Order(15)
    @DisplayName("Should remove role from teacher successfully")
    void removeRoleFromTeacher_Success() {
        teacher.addRoles(Role.ROLE_ADMIN);
        when(allRepositoryMethods.getTeacherById(teacherId)).thenReturn(teacher);
        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);
        when(teacherMapper.toDto(any(Teacher.class))).thenReturn(teacherDTO);

        TeacherDTO result = teacherService.removeRoleFromTeacher(teacherId, Role.ROLE_ADMIN);

        assertNotNull(result);
        verify(teacherRepository, times(1)).save(any(Teacher.class));
    }
}