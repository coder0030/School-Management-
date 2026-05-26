package com.example.demo.ServiceImpl;

import com.example.demo.DTO.ClassTeacherDTO;
import com.example.demo.ENTITY.*;
import com.example.demo.Helper.Role;
import com.example.demo.Mapper.ClassTeacherMapper;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.MyException.DataNotFoundException;
import com.example.demo.Repository.ClassRepository;
import com.example.demo.Repository.ClassTeacherRepository;
import com.example.demo.Repository.TeacherRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.RequestDTO.ClassTeacherRequestDTO;
import com.example.demo.Service.ClassTeacherService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClassTeacherServiceImpl implements ClassTeacherService {

    private final ClassTeacherRepository classTeacherRepository;
    private final ClassTeacherMapper classTeacherMapper;
    private final ClassRepository classRepository;
    private final TeacherRepository teacherRepository;
    private final AllRepositoryMethods allRepositoryMethods;
    private final UserRepository userRepository;


    private void validTeacherAndClassExists(Long teacherId, Long classId) {
        if (!classRepository.existsById(classId)) {
            throw new BadRequestException("Class id: " + classId + " does not exist.");
        }
        if (!teacherRepository.existsById(teacherId)) {
            throw new BadRequestException("Teacher id: " + teacherId + " does not exist.");
        }
    }

    private boolean isClassAlreadyAssignedToTeacher(ClassEntity classEntity, Long teacherId) {
        List<ClassTeacher> classTeacherList = classEntity.getClassTeacherList();
        if (classTeacherList == null) {
            return false;
        }
        return classTeacherList.stream().anyMatch(ct ->
                ct.getTeacher() != null && ct.getTeacher().getId().equals(teacherId));
    }

    private void addRelations(ClassEntity classEntity, Teacher teacher, ClassTeacher classTeacher) {
        classTeacher.setTeacher(teacher);
        classTeacher.setClassEntity(classEntity);

        classEntity.addClassTeacher(classTeacher);
        teacher.addClassTeacher(classTeacher);
    }

    private void removeRelations(ClassEntity classEntity, Teacher teacher, ClassTeacher classTeacher) {
        if (classEntity.getClassTeacherList() != null && !classEntity.getClassTeacherList().isEmpty()) {
            classEntity.removeClassTeacher(classTeacher);
        }

        if (teacher.getClassTeacherList() != null && !teacher.getClassTeacherList().isEmpty()) {
            teacher.removeClassTeacher(classTeacher);
        }

        classTeacher.setTeacher(null);
        classTeacher.setClassEntity(null);
    }

    private boolean classHasPrimaryTeacher(Long classId) {
        return classTeacherRepository.existsByClassEntity_IdAndIsPrimary(classId, true);
    }

    private boolean teacherAlreadyClassTeacher(Teacher teacher) {
        return teacher.getUser().getRoles().contains(Role.ROLE_CLASS_TEACHER);
    }

    @Override
    @Transactional
    public ClassTeacherDTO assignTeacherToClass(ClassTeacherRequestDTO requestDto) {

        validTeacherAndClassExists(requestDto.getTeacherId(), requestDto.getClassId());

        Teacher teacher = allRepositoryMethods.getTeacherById(requestDto.getTeacherId());
        ClassEntity classEntity = allRepositoryMethods.getClassById(requestDto.getClassId());

        if (isClassAlreadyAssignedToTeacher(classEntity, teacher.getId())) {
            throw new BadRequestException("Teacher already assigned to this class");
        }

        boolean classHasPrimary = classHasPrimaryTeacher(classEntity.getId());
        boolean teacherIsAlreadyClassTeacher = teacherAlreadyClassTeacher(teacher);

        ClassTeacher classTeacher = new ClassTeacher();
        addRelations(classEntity, teacher, classTeacher);

        classTeacher.setRoleType(Role.ROLE_TEACHER);
        classTeacher.setIsPrimary(false);

        if (!classHasPrimary && !teacherIsAlreadyClassTeacher) {
            classTeacher.setIsPrimary(true);
            classTeacher.setRoleType(Role.ROLE_CLASS_TEACHER);

            teacher.addRoles(Role.ROLE_CLASS_TEACHER);
            userRepository.save(teacher.getUser());
        }

        ClassTeacher saved = classTeacherRepository.save(classTeacher);
        return classTeacherMapper.toDTO(saved);
    }

    @Override
    public Page<ClassTeacherDTO> getAllClassTeachers(int pageNo, int pageSize) {
        if(pageSize > AttendanceServiceImpl.maxPageSize) pageSize = AttendanceServiceImpl.maxPageSize;

        Sort sort = Sort.by("assignmentDate").descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<ClassTeacher> classTeacherPage = classTeacherRepository.findAll(pageable);

        if (classTeacherPage.getContent().isEmpty()) {
            return Page.empty(pageable);
        }

        return classTeacherPage.map(classTeacherMapper::toDTO);
    }

    @Override
    public ClassTeacherDTO getClassTeacherById(Long id) {
        ClassTeacher classTeacher = allRepositoryMethods.getClassTeacherById(id);

        return classTeacherMapper.toDTO(classTeacher);
    }

    @Override
    public void deleteClassTeacher(Long id) {
        ClassTeacher classTeacher = allRepositoryMethods.getClassTeacherById(id);
        ClassEntity classEntity = classTeacher.getClassEntity();
        Teacher teacher = classTeacher.getTeacher();
        if(classTeacherRepository.existsByTeacher_IdAndIsPrimary(classEntity.getId(), true)) {
            classTeacher.setIsPrimary(false);
            teacher.removeRoles(Role.ROLE_CLASS_TEACHER);
        }
        removeRelations(classEntity, teacher, classTeacher);
        classTeacherRepository.delete(classTeacher);
    }

    @Override
    public void removeTeacherFromClass(Long classId, Long teacherId) {
        validTeacherAndClassExists(teacherId, classId);
        ClassTeacher classTeacher = classTeacherRepository.findByClassEntity_IdAndTeacher_Id(classId, teacherId)
                .orElseThrow(() -> new DataNotFoundException(
                        "Teacher ID: " + teacherId + " is not assigned to Class ID: " + classId
                ));

        Teacher teacher = classTeacher.getTeacher();
        ClassEntity classEntity = classTeacher.getClassEntity();

        if(classTeacherRepository.existsByTeacher_IdAndIsPrimary(classEntity.getId(), true)) {
            classTeacher.setIsPrimary(false);
            teacher.removeRoles(Role.ROLE_CLASS_TEACHER);
        }

        removeRelations(classEntity, teacher, classTeacher);
        classTeacherRepository.delete(classTeacher);
    }

    @Override
    public List<ClassTeacherDTO> getTeachersByClassId(Long classId) {
        ClassEntity classEntity = allRepositoryMethods.getClassById(classId);
        List<ClassTeacher> classTeachers = classEntity.getClassTeacherList();
        if (classTeachers == null || classTeachers.isEmpty()) {
            return List.of();
        }
        return classTeacherMapper.toDTOList(classTeachers);
    }

    @Override
    public List<ClassTeacherDTO> getClassesByTeacherId(Long teacherId) {
        Teacher teacher = allRepositoryMethods.getTeacherById(teacherId);
        List<ClassTeacher> classTeachers = teacher.getClassTeacherList();
        if (classTeachers == null || classTeachers.isEmpty()) {
            return List.of();
        }
        return classTeacherMapper.toDTOList(classTeachers);
    }
}