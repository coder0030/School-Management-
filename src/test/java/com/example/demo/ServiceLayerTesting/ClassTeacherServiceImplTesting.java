package com.example.demo.ServiceLayerTesting;

import com.example.demo.DTO.ClassTeacherDTO;
import com.example.demo.ENTITY.*;
import com.example.demo.Helper.Role;
import com.example.demo.Mapper.ClassTeacherMapper;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.Repository.ClassRepository;
import com.example.demo.Repository.ClassTeacherRepository;
import com.example.demo.Repository.TeacherRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.RequestDTO.ClassTeacherRequestDTO;
import com.example.demo.ServiceImpl.AllRepositoryMethods;
import com.example.demo.ServiceImpl.ClassTeacherServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

    @ExtendWith(MockitoExtension.class)
    class ClassTeacherServiceImplTest {

        @Mock
        private ClassTeacherRepository classTeacherRepository;

        @Mock
        private ClassTeacherMapper classTeacherMapper;

        @Mock
        private ClassRepository classRepository;

        @Mock
        private TeacherRepository teacherRepository;

        @Mock
        private AllRepositoryMethods allRepositoryMethods;

        @Mock
        private UserRepository userRepository;

        @InjectMocks
        private ClassTeacherServiceImpl classTeacherService;

        private ClassTeacherRequestDTO requestDTO;
        private Teacher teacher;
        private ClassEntity classEntity;
        private ClassTeacher classTeacher;
        private ClassTeacherDTO classTeacherDTO;
        private User user;
        private Long teacherId;
        private Long classId;
        private Long classTeacherId;

        @BeforeEach
        void setUp() {
            teacherId = 1L;
            classId = 10L;
            classTeacherId = 100L;

            user = User.builder()
                    .id(1L)
                    .email("teacher@example.com")
                    .username("teacher@example.com")
                    .roles(new HashSet<>())
                    .build();

            teacher = Teacher.builder()
                    .id(teacherId)
                    .firstName("John")
                    .lastName("Doe")
                    .email("teacher@example.com")
                    .user(user)
                    .classTeacherList(new ArrayList<>())
                    .role(Role.ROLE_TEACHER)
                    .build();

            classEntity = ClassEntity.builder()
                    .id(classId)
                    .className("Class 10A")
                    .classTeacherList(new ArrayList<>())
                    .build();

            requestDTO = ClassTeacherRequestDTO.builder()
                    .teacherId(teacherId)
                    .classId(classId)
                    .build();

            classTeacher = ClassTeacher.builder()
                    .id(classTeacherId)
                    .teacher(teacher)
                    .classEntity(classEntity)
                    .isPrimary(false)
                    .roleType(Role.ROLE_TEACHER)
                    .assignmentDate(LocalDate.now())
                    .build();

            classTeacherDTO = ClassTeacherDTO.builder()
                    .id(classTeacherId)
                    .teacherId(teacherId)
                    .classId(classId)
                    .isPrimary(false)
                    .roleType(String.valueOf(Role.ROLE_TEACHER))
                    .build();
        }


        @Test
        @Order(1)
        @DisplayName("Should assign teacher to class successfully as non-primary")
        void assignTeacherToClass_Success_NonPrimary() {
            when(classRepository.existsById(classId)).thenReturn(true);
            when(teacherRepository.existsById(teacherId)).thenReturn(true);
            when(allRepositoryMethods.getTeacherById(teacherId)).thenReturn(teacher);
            when(allRepositoryMethods.getClassById(classId)).thenReturn(classEntity);
            when(classTeacherRepository.existsByClassEntity_IdAndIsPrimary(classId, true))
                    .thenReturn(true); // Class already has primary teacher
            when(classTeacherRepository.save(any(ClassTeacher.class))).thenReturn(classTeacher);
            when(classTeacherMapper.toDTO(any(ClassTeacher.class))).thenReturn(classTeacherDTO);

            ClassTeacherDTO result = classTeacherService.assignTeacherToClass(requestDTO);

            assertNotNull(result);
            assertFalse(result.getIsPrimary());
            assertEquals(Role.ROLE_TEACHER, Role.valueOf(result.getRoleType()));
            verify(classTeacherRepository, times(1)).save(any(ClassTeacher.class));
        }

        @Test
        @Order(2)
        @DisplayName("Should assign teacher as primary when class has no primary teacher")
        void assignTeacherToClass_Success_Primary() {
            when(classRepository.existsById(classId)).thenReturn(true);
            when(teacherRepository.existsById(teacherId)).thenReturn(true);
            when(allRepositoryMethods.getTeacherById(teacherId)).thenReturn(teacher);
            when(allRepositoryMethods.getClassById(classId)).thenReturn(classEntity);
            when(classTeacherRepository.existsByClassEntity_IdAndIsPrimary(classId, true))
                    .thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(classTeacherRepository.save(any(ClassTeacher.class))).thenReturn(classTeacher);
            when(classTeacherMapper.toDTO(any(ClassTeacher.class))).thenReturn(classTeacherDTO);

            ClassTeacherDTO result = classTeacherService.assignTeacherToClass(requestDTO);

            assertNotNull(result);
            verify(userRepository, times(1)).save(any(User.class));
            verify(classTeacherRepository, times(1)).save(any(ClassTeacher.class));
        }

        @Test
        @Order(3)
        @DisplayName("Should throw exception when class does not exist")
        void assignTeacherToClass_ClassNotFound() {
            when(classRepository.existsById(classId)).thenReturn(false);

            assertThrows(BadRequestException.class, () -> {
                classTeacherService.assignTeacherToClass(requestDTO);
            });

            verify(classTeacherRepository, never()).save(any(ClassTeacher.class));
        }

        @Test
        @Order(4)
        @DisplayName("Should throw exception when teacher does not exist")
        void assignTeacherToClass_TeacherNotFound() {
            when(classRepository.existsById(classId)).thenReturn(true);
            when(teacherRepository.existsById(teacherId)).thenReturn(false);

            assertThrows(BadRequestException.class, () -> {
                classTeacherService.assignTeacherToClass(requestDTO);
            });
            verify(classTeacherRepository, never()).save(any(ClassTeacher.class));
        }

        @Test
        @Order(5)
        @DisplayName("Should throw exception when teacher already assigned to class")
        void assignTeacherToClass_TeacherAlreadyAssigned() {
            classEntity.getClassTeacherList().add(classTeacher);

            when(classRepository.existsById(classId)).thenReturn(true);
            when(teacherRepository.existsById(teacherId)).thenReturn(true);
            when(allRepositoryMethods.getTeacherById(teacherId)).thenReturn(teacher);
            when(allRepositoryMethods.getClassById(classId)).thenReturn(classEntity);

            assertThrows(BadRequestException.class, () -> {
                classTeacherService.assignTeacherToClass(requestDTO);
            });

            verify(classTeacherRepository, never()).save(any(ClassTeacher.class));
        }

        @Test
        @Order(6)
        @DisplayName("Should get all class teachers with pagination")
        void getAllClassTeachers_Success() {
            Pageable pageable = PageRequest.of(0, 10,
                    Sort.by("assignmentDate").descending());

            List<ClassTeacher> classTeachers = Arrays.asList(classTeacher);
            Page<ClassTeacher> classTeacherPage = new PageImpl<>(classTeachers, pageable, 1);

            when(classTeacherRepository.findAll(any(Pageable.class))).thenReturn(classTeacherPage);
            when(classTeacherMapper.toDTO(any(ClassTeacher.class))).thenReturn(classTeacherDTO);

            Page<ClassTeacherDTO> result = classTeacherService.getAllClassTeachers(0, 10);

            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            verify(classTeacherRepository, times(1)).findAll(any(Pageable.class));
        }

        @Test
        @Order(7)
        @DisplayName("Should return empty page when no class teachers exist")
        void getAllClassTeachers_EmptyResult() {
            Pageable pageable = PageRequest.of(0, 10,
                    Sort.by("assignmentDate").descending());

            Page<ClassTeacher> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

            when(classTeacherRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);
            Page<ClassTeacherDTO> result = classTeacherService.getAllClassTeachers(0, 10);

            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(classTeacherRepository, times(1)).findAll(any(Pageable.class));

        }

        @Test
        @Order(8)
        @DisplayName("Should get class teacher by ID successfully")
        void getClassTeacherById_Success() {
            when(allRepositoryMethods.getClassTeacherById(classTeacherId)).thenReturn(classTeacher);
            when(classTeacherMapper.toDTO(classTeacher)).thenReturn(classTeacherDTO);

            ClassTeacherDTO result = classTeacherService.getClassTeacherById(classTeacherId);

            assertNotNull(result);
            assertEquals(classTeacherId, result.getId());
            verify(allRepositoryMethods, times(1)).getClassTeacherById(classTeacherId);
        }

        @Test
        @Order(9)
        @DisplayName("Should delete class teacher successfully")
        void deleteClassTeacher_Success() {
            when(allRepositoryMethods.getClassTeacherById(classTeacherId)).thenReturn(classTeacher);
            doNothing().when(classTeacherRepository).delete(any(ClassTeacher.class));

            assertDoesNotThrow(() -> classTeacherService.deleteClassTeacher(classTeacherId));
            verify(classTeacherRepository, times(1)).delete(classTeacher);
        }

        @Test
        @DisplayName("Should remove primary role when deleting primary class teacher")
        @Order(10)
        void deleteClassTeacher_PrimaryTeacher() {
            classTeacher.setIsPrimary(true);
            classTeacher.setRoleType(Role.ROLE_CLASS_TEACHER);
            teacher.addRoles(Role.ROLE_CLASS_TEACHER);

            when(allRepositoryMethods
                    .getClassTeacherById(classTeacherId)).thenReturn(classTeacher);
            when(classTeacherRepository.existsByTeacher_IdAndIsPrimary(classEntity.getId(), true))
                    .thenReturn(true);
            doNothing().when(classTeacherRepository).delete(any(ClassTeacher.class));

            assertDoesNotThrow(() -> classTeacherService.deleteClassTeacher(classTeacherId));

            assertFalse(classTeacher.getIsPrimary());
            verify(classTeacherRepository, times(1)).delete(classTeacher);
        }

        @Test
        @Order(11)
        @DisplayName("Should return empty list when no teachers assigned to class")
        void getTeachersByClassId_EmptyList() {
            classEntity.setClassTeacherList(new ArrayList<>());

            when(allRepositoryMethods.getClassById(classId)).thenReturn(classEntity);

            List<ClassTeacherDTO> result = classTeacherService.getTeachersByClassId(classId);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }
