package com.example.demo.ServiceImpl;

import com.example.demo.DTO.StudentDTO;
import com.example.demo.ENTITY.ClassEntity;
import com.example.demo.ENTITY.Student;
import com.example.demo.ENTITY.User;
import com.example.demo.Helper.Role;
import com.example.demo.Mapper.StudentMapper;
import com.example.demo.MyException.*;
import com.example.demo.Repository.StudentRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.RequestDTO.StudentRequestDTO;
import com.example.demo.Security.SecurityUtil;
import com.example.demo.Service.StudentService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final AllRepositoryMethods allRepositoryMethods;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;

    private void validateStudentAndClass(Long studentId, Long classId) {
        if (classId == null || studentId == null) {
            throw new BadRequestException("StudentId or classId cannot be null.");
        }
    }

    private boolean isStudentAlreadyInClass(ClassEntity classEntity, Long studentId) {
        return classEntity.getStudentList() != null &&
                classEntity.getStudentList().stream()
                        .anyMatch(student -> student.getId().equals(studentId));
    }

    private void addRelations(ClassEntity classEntity, Student student) {
        classEntity.addClassStudent(student);
        student.setClassEntity(classEntity);
    }

    private void removeRelations(ClassEntity classEntity, Student student) {
        if (classEntity.getStudentList() != null) {
            classEntity.removeClassStudent(student);
            student.setClassEntity(null);
        }
    }

    private void checkExistenceForCreate(String email, String phone) {
        if (email != null && studentRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already exists: " + email);
        }
        if (phone != null && studentRepository.existsByPhone(phone)) {
            throw new BadRequestException("Phone already exists: " + phone);
        }
    }

    private void checkExistenceForUpdate(String email, String phone, Long excludeStudentId) {
        if (email != null && studentRepository.existsByEmailAndIdNot(email, excludeStudentId)) {
            throw new BadRequestException("Email already exists: " + email);
        }
        if (phone != null && studentRepository.existsByPhoneAndIdNot(phone, excludeStudentId)) {
            throw new BadRequestException("Phone already exists: " + phone);
        }
    }

    private void assignRollNumber(Student student, ClassEntity classEntity) {
        int totalStudents = studentRepository.countByClassEntity_Id(classEntity.getId());
        student.setRollNo(totalStudents + 1);
    }

    @Override
    @Transactional
    public StudentDTO createStudent(StudentRequestDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Student request DTO cannot be null");
        }

        checkExistenceForCreate(dto.getEmail(), dto.getPhone());
        User user = userRepository.findByEmailOrUsernameAndIsActive(dto.getEmail(), dto.getEmail(), true)
                .orElseThrow(() -> new BadRequestException
                        ("No active user account found with this email. User must signup first."));


        securityUtil.isAlreadyAssignWithDifferentRoles(user, Role.ROLE_STUDENT);

        ClassEntity classEntity = null;
        if (dto.getClassId() != null) {
            classEntity = allRepositoryMethods.getClassById(dto.getClassId());
        }

        Student student = new Student();
        student = studentMapper.toEntity(dto, student);

        if (classEntity != null) {
            student.setClassEntity(classEntity);
            classEntity.addClassStudent(student);
        }

        if (classEntity != null) {
            assignRollNumber(student, classEntity);
        }

        student.setUser(user);
        student.addRoles(Role.ROLE_STUDENT);
        Student saved = studentRepository.save(student);
        userRepository.save(student.getUser());
        log.info("Student created successfully with id: {}", saved.getId());

        return studentMapper.toDto(saved);
    }

    @Override
    @Transactional
    public StudentDTO updateStudentById(Long studentId, @Valid StudentRequestDTO dto) throws AccessDeniedException {
        if (dto == null) {
            throw new BadRequestException("Student request DTO cannot be null");
        }

        boolean isAdmin = SecurityUtil.isAdmin();
        boolean isOwner = SecurityUtil.isOwner(studentId);

        Student student = allRepositoryMethods.getStudentById(studentId);

        if (!isAdmin && !isOwner) {
            throw new com.example.demo.MyException.AccessDeniedException("You're not allowed for this operation.");
        }

        if(isAdmin || isOwner) {

            checkExistenceForUpdate(dto.getEmail(), dto.getPhone(), studentId);

            student.setEmail(dto.getEmail());
            student.setPhone(dto.getPhone());

            student = studentMapper.UpdateToEntity(dto, student);
        }

        if(isOwner && !isAdmin) {
            if (dto.getClassId() != null) {
                throw new AccessDeniedException("Students cannot change their class.");
            }
        }

        if(isAdmin) {
            ClassEntity classEntity = allRepositoryMethods.getClassById(dto.getClassId());
            if(!classEntity.getId().equals(student.getClassEntity().getId())) {
                removeRelations(student.getClassEntity(), student);
                addRelations(classEntity, student);
            }
        }

        Student updated = studentRepository.save(student);
        log.info("Student updated successfully with id: {}", updated.getId());

        return studentMapper.toDto(updated);
    }

    @Override
    @Transactional
    public StudentDTO patchUpdateStudentById(Long studentId, StudentRequestDTO dto) throws AccessDeniedException {
        if (dto == null) {
            throw new BadRequestException("Student request DTO cannot be null");
        }

        boolean isAdmin = SecurityUtil.isAdmin();
        boolean isOwner = SecurityUtil.isOwner(studentId);

        Student student = allRepositoryMethods.getStudentById(studentId);

        if (!isAdmin && !isOwner) {
            throw new com.example.demo.MyException.AccessDeniedException("You're not allowed for this operation.");
        }

        if(isAdmin || isOwner) {

            String email = (dto.getEmail() != null ? dto.getEmail() : student.getEmail());
            String phone = (dto.getPhone() != null ? dto.getPhone() : student.getPhone());

            checkExistenceForUpdate(email, phone, studentId);

            student.setEmail(email);
            student.setPhone(phone);

            student = studentMapper.toEntity(dto, student);

        }

        if(isOwner && !isAdmin) {
            if (dto.getClassId() != null) {
                throw new AccessDeniedException("Students cannot change their class.");
            }
        }

        if(isAdmin) {
            ClassEntity classEntity = allRepositoryMethods.getClassById(dto.getClassId());
            if(!classEntity.getId().equals(student.getClassEntity().getId())) {
                removeRelations(student.getClassEntity(), student);
                addRelations(classEntity, student);
            }
        }

        Student updated = studentRepository.save(student);
        log.info("Student updated successfully with id: {}", updated.getId());

        return studentMapper.toDto(updated);
    }

    @Override
    public List<StudentDTO> getAllStudent() {
        List<Student> students = studentRepository.findAll();

        if (students.isEmpty()) {
            return List.of();
        }
        return studentMapper.toDtoList(students);
    }

    @Override
    @Transactional
    public void deleteStudentById(Long studentId) {
        Student student = allRepositoryMethods.getStudentById(studentId);
        studentRepository.delete(student);
        log.info("Student deleted successfully with id: {}", studentId);
    }

    @Override
    public StudentDTO getStudentById(Long studentId) {
        Student student = allRepositoryMethods.getStudentById(studentId);
        return studentMapper.toDto(student);
    }

    @Override
    @Transactional
    public StudentDTO assignStudentToClass(Long studentId, StudentRequestDTO dto) {
        ClassEntity classEntity = allRepositoryMethods.getClassById(dto.getClassId());
        Student student = allRepositoryMethods.getStudentById(studentId);

        if (studentRepository.existsByIdAndClassEntity_Id(student.getId(), classEntity.getId())) {
            throw new BadRequestException("Student is already assigned to this class");
        }

        addRelations(classEntity, student);
        assignRollNumber(student, classEntity);

        Student assigned = studentRepository.save(student);
        log.info("Student {} assigned to class {} with roll number {}",
                studentId, classEntity.getId(), student.getRollNo());

        return studentMapper.toDto(assigned);
    }

    @Override
    public List<StudentDTO> getAllClassStudents() {
        List<Student> students = studentRepository.findByClassEntityIsNotNull();
        if (students.isEmpty()) {
            return List.of();
        }
        return studentMapper.toDtoList(students);
    }

    @Override
    @Transactional
    public void deleteClassStudent(Long studentId) {
        Student student = allRepositoryMethods.getStudentById(studentId);

        if (student.getClassEntity() == null) {
            throw new BadRequestException("Student is not assigned to any class.");
        }

        ClassEntity classEntity = student.getClassEntity();
        removeRelations(classEntity, student);
        studentRepository.save(student);
        log.info("Student {} removed from class {}", studentId, classEntity.getId());
    }

    @Override
    public StudentDTO getClassStudentById(Long id) {
        Student student = allRepositoryMethods.getStudentById(id);

        if (student.getClassEntity() == null) {
            throw new DataNotFoundException("Student is not assigned to any class.");
        }

        return studentMapper.toDto(student);
    }

    @Override
    @Transactional
    public void removeStudentFromClass(Long classId, Long studentId) {
        validateStudentAndClass(studentId, classId);

        Student student = allRepositoryMethods.getStudentById(studentId);
        ClassEntity classEntity = allRepositoryMethods.getClassById(classId);

        if (student.getClassEntity() == null ||
                !student.getClassEntity().getId().equals(classId)) {
            throw new BadRequestException("Student does not belong to this class.");
        }

        removeRelations(classEntity, student);
        studentRepository.save(student);
        log.info("Student {} removed from class {}", studentId, classId);
    }

    @Override
    public List<StudentDTO> getStudentsByClassId(Long classId) {
        ClassEntity classEntity = allRepositoryMethods.getClassById(classId);

        List<Student> students = classEntity.getStudentList();

        if (students == null || students.isEmpty()) {
            return List.of();
        }

        return studentMapper.toDtoList(students);
    }

    @Override
    public List<StudentDTO> getClassesByStudentId(Long studentId) {
        Student student = allRepositoryMethods.getStudentById(studentId);

        if (student.getClassEntity() == null) {
            return List.of();
        }

        return List.of(studentMapper.toDto(student));
    }

    @Override
    public List<StudentDTO> getAllStudents(Long classId) {
        ClassEntity classEntity = allRepositoryMethods.getClassById(classId);
        List<Student> studentList = studentRepository.findByClassEntity_Id(classId);

        if(studentList.isEmpty()) {
            return List.of();
        }

        return studentMapper.toDtoList(studentList);
    }

}