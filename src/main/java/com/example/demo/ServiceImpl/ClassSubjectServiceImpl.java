package com.example.demo.ServiceImpl;

import com.example.demo.DTO.ClassSubjectDTO;
import com.example.demo.ENTITY.*;
import com.example.demo.Mapper.ClassSubjectMapper;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.MyException.DataNotFoundException;
import com.example.demo.Repository.ClassSubjectRepository;
import com.example.demo.Repository.ClassRepository;
import com.example.demo.Repository.SubjectRepository;
import com.example.demo.Repository.TeacherRepository;
import com.example.demo.RequestDTO.ClassSubjectRequestDTO;
import com.example.demo.RequestDTO.TeacherSubjectRequestDTO;
import com.example.demo.Service.ClassSubjectService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ClassSubjectServiceImpl implements ClassSubjectService {

    private final ClassSubjectRepository classSubjectRepository;
    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final ClassSubjectMapper classSubjectMapper;
    private final AllRepositoryMethods allRepositoryMethods;
    private final TeacherRepository teacherRepository;

    private void validateClassAndSubjectExist(Long classId, Long subjectId) {
        if (!classRepository.existsById(classId)) {
            throw new BadRequestException("Class id: " + classId + " does not exist.");
        }
        if (!subjectRepository.existsById(subjectId)) {
            throw new BadRequestException("Subject id: " + subjectId + " does not exist.");
        }
    }

    private boolean isSubjectAlreadyAssignedToClass(ClassEntity classEntity, Long subjectId) {
        return classEntity.getClassSubjects().stream()
                .anyMatch(classSubject -> classSubject.getSubject().getId().equals(subjectId));
    }

    private void addRelations(ClassEntity classEntity, Subject subject, Teacher teacher, ClassSubject classSubject) {
        classSubject.setSubject(subject);
        classSubject.setClassEntity(classEntity);
        classSubject.setTeacher(teacher);

        subject.addClassSubject(classSubject);
        classEntity.addClassSubjects(classSubject);
        teacher.addClassSubjects(classSubject);
    }

    private void removeRelations(ClassEntity classEntity, Subject subject, Teacher teacher, ClassSubject classSubject) {
        subject.removeClassSubject(classSubject);
        classEntity.removeClassSubjects(classSubject);
        teacher.removeClassSubjects(classSubject);

        classSubject.setSubject(null);
        classSubject.setClassEntity(null);
        classSubject.setTeacher(null);
    }

    @Override
    @Transactional
    public ClassSubjectDTO assignSubjectToClass(@Valid ClassSubjectRequestDTO requestDto) {

        validateClassAndSubjectExist(requestDto.getClassId(), requestDto.getSubjectId());

        Subject subject = allRepositoryMethods.getSubjectById(requestDto.getSubjectId());
        ClassEntity classEntity = allRepositoryMethods.getClassById(requestDto.getClassId());
        Teacher teacher = allRepositoryMethods.getTeacherById(requestDto.getTeacherId());

        if (isSubjectAlreadyAssignedToClass(classEntity, subject.getId())) {
            throw new BadRequestException("Subject Id: " + subject.getId() + " is already assigned to class: " + classEntity.getId());
        }

        ClassSubject classSubject = new ClassSubject();
        addRelations(classEntity, subject, teacher, classSubject);

        ClassSubject created = classSubjectRepository.save(classSubject);
        return classSubjectMapper.toDTO(created);
    }

    @Override
    public Page<ClassSubjectDTO> getAllClassSubjects(int pageNo, int pageSize) {
        if(pageSize > AttendanceServiceImpl.maxPageSize) pageSize = AttendanceServiceImpl.maxPageSize;

        Sort sort = Sort.by("assignedAt").descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<ClassSubject> classSubjectPage = classSubjectRepository.findAll(pageable);

        if (classSubjectPage.getContent().isEmpty()) {
            return Page.empty(pageable);
        }

        return classSubjectPage.map(classSubjectMapper::toDTO);
    }

    @Override
    public ClassSubjectDTO partialUpdateClassSubject(Long id, ClassSubjectRequestDTO requestDto) {

        ClassSubject classSubject = allRepositoryMethods.getClassSubjectById(id);

        Long subjectId = requestDto.getSubjectId() != null
                ? requestDto.getSubjectId()
                : classSubject.getSubject().getId();

        Long classId = requestDto.getClassId() != null
                ? requestDto.getClassId()
                : classSubject.getClassEntity().getId();

        Long teacherId = requestDto.getTeacherId() != null
                ? requestDto.getTeacherId()
                : classSubject.getTeacher().getId();

        validateClassAndSubjectExist(classId, subjectId);

        ClassEntity classEntity = allRepositoryMethods.getClassById(classId);
        Subject subject = allRepositoryMethods.getSubjectById(subjectId);

        boolean exist = classSubjectRepository
                .existsByClassEntity_IdAndSubject_IdAndTeacher_IdAndIdNot(
                        classId, subjectId, teacherId, id);

        if (exist) {
            throw new BadRequestException("Combination of subject, teacher, class already exists.");
        }

        Teacher teacher = teacherRepository
                .findByIdAndTeacherSubjects_Subject_Id(teacherId, subjectId)
                .orElseThrow(() ->
                        new BadRequestException("Teacher is not assigned with subject Id : " + subjectId));

        boolean changed =
                !classSubject.getSubject().getId().equals(subjectId) ||
                        !classSubject.getClassEntity().getId().equals(classId) ||
                        !classSubject.getTeacher().getId().equals(teacherId);

        if (changed) {
            removeRelations(classSubject.getClassEntity(), classSubject.getSubject(),
                    classSubject.getTeacher(), classSubject);

            addRelations(classEntity, subject, teacher, classSubject);
        }

        ClassSubject saved = classSubjectRepository.save(classSubject);
        return classSubjectMapper.toDTO(saved);
    }

    @Override
    public ClassSubjectDTO getClassSubjectById(Long id) {
        ClassSubject classSubject = allRepositoryMethods.getClassSubjectById(id);
        return classSubjectMapper.toDTO(classSubject);
    }

    @Override
    @Transactional
    public void deleteClassSubject(Long id) {
        ClassSubject classSubject = allRepositoryMethods.getClassSubjectById(id);

        if (classSubject.getClassEntity() != null) {
            classSubject.getClassEntity().removeClassSubjects(classSubject);
        }
        if (classSubject.getSubject() != null) {
            classSubject.getSubject().removeClassSubject(classSubject);
        }
        if (classSubject.getTeacher() != null) {
            classSubject.getTeacher().removeClassSubjects(classSubject);
        }

        classSubject.setClassEntity(null);
        classSubject.setSubject(null);
        classSubject.setTeacher(null);
        classSubjectRepository.delete(classSubject);
    }

    @Override
    @Transactional
    public void removeSubjectFromClassById(Long classSubjectId) {
        ClassSubject classSubject = allRepositoryMethods.getClassSubjectById(classSubjectId);

        if (classSubject.getClassEntity() != null) {
            classSubject.getClassEntity().removeClassSubjects(classSubject);
        }
        if (classSubject.getSubject() != null) {
            classSubject.getSubject().removeClassSubject(classSubject);
        }
        if (classSubject.getTeacher() != null) {
            classSubject.getTeacher().removeClassSubjects(classSubject);
        }

        classSubject.setClassEntity(null);
        classSubject.setSubject(null);
        classSubject.setTeacher(null);

        classSubjectRepository.delete(classSubject);
    }

    @Override
    @Transactional
    public void removeSubjectFromClass(Long classId, Long subjectId) {
        validateClassAndSubjectExist(classId, subjectId);

        ClassSubject classSubject = classSubjectRepository
                .findByClassEntityIdAndSubjectId(classId, subjectId)
                .orElseThrow(() -> new DataNotFoundException(
                        "Subject ID: " + subjectId + " is not assigned to Class ID: " + classId
                ));

        ClassEntity classEntity = classSubject.getClassEntity();
        Subject subject = classSubject.getSubject();
        Teacher teacher = classSubject.getTeacher();

        if (classEntity != null) {
            classEntity.removeClassSubjects(classSubject);
        }
        if (subject != null) {
            subject.removeClassSubject(classSubject);
        }
        if (teacher != null) {
            teacher.removeClassSubjects(classSubject);
        }

        classSubject.setClassEntity(null);
        classSubject.setSubject(null);
        classSubject.setTeacher(null);

        classSubjectRepository.delete(classSubject);
    }

    @Deprecated
    @Override
    @Transactional
    public ClassSubjectDTO removeClassSubjectToClass(Long classSubjectId, @Valid ClassSubjectRequestDTO requestDTO) {

        ClassSubject classSubject = allRepositoryMethods.getClassSubjectById(classSubjectId);

        if (!classSubject.getClassEntity().getId().equals(requestDTO.getClassId()) ||
                !classSubject.getSubject().getId().equals(requestDTO.getSubjectId())) {
            throw new BadRequestException(
                    "ClassSubject ID: " + classSubjectId + " does not match the provided Class ID and Subject ID"
            );
        }

        validateClassAndSubjectExist(requestDTO.getClassId(), requestDTO.getSubjectId());

        Subject subject = allRepositoryMethods.getSubjectById(requestDTO.getSubjectId());
        ClassEntity classEntity = allRepositoryMethods.getClassById(requestDTO.getClassId());
        Teacher teacher = allRepositoryMethods.getTeacherById(requestDTO.getTeacherId());

        boolean exist = classSubjectRepository
                .existsByClassEntity_IdAndSubject_IdAndTeacher_IdAndIdNot(
                        requestDTO.getClassId(), requestDTO.getSubjectId(), requestDTO.getTeacherId(), classSubjectId);

        if (!isSubjectAlreadyAssignedToClass(classEntity, subject.getId())) {
            throw new BadRequestException("Subject Id: " + subject.getId() + " does not exist in current class");
        }

        removeRelations(classEntity, subject, teacher, classSubject);
        classSubjectRepository.delete(classSubject);

        return classSubjectMapper.toDTO(classSubject);
    }

    @Override
    public List<ClassSubjectDTO> getClassSubjectsBySubjectId(Long subjectId) {
        if (!subjectRepository.existsById(subjectId)) {
            throw new BadRequestException("Subject id: " + subjectId + " does not exist.");
        }

        List<ClassSubject> classSubjects = classSubjectRepository.findBySubjectId(subjectId);
        if (classSubjects.isEmpty()) {
            return List.of();
        }
        return classSubjectMapper.classDTOList(classSubjects);
    }

    @Override
    public boolean isSubjectAssignedToClass(Long classId, Long subjectId) {
        validateClassAndSubjectExist(classId, subjectId);
        return classSubjectRepository.existsByClassEntityIdAndSubjectId(classId, subjectId);
    }

    @Override
    @Transactional
    public ClassSubjectDTO assignOrUpdateSubjectToClass(Long id, ClassSubjectRequestDTO requestDto) {
        validateClassAndSubjectExist(requestDto.getClassId(), requestDto.getSubjectId());

        ClassSubject classSubject = allRepositoryMethods.getClassSubjectById(id);
        Subject subject = allRepositoryMethods.getSubjectById(requestDto.getSubjectId());
        ClassEntity classEntity = allRepositoryMethods.getClassById(requestDto.getClassId());
        Teacher teacher = allRepositoryMethods.getTeacherById(requestDto.getTeacherId());

        boolean exist = classSubjectRepository.existsByClassEntity_IdAndSubject_IdAndTeacher_IdAndIdNot(
                classEntity.getId(), subject.getId(), teacher.getId(), id);
        if(exist) {
            throw new BadRequestException("Combination of subject, teacher, class already exists.");
        }

        teacherRepository.findByIdAndTeacherSubjects_Subject_Id(teacher.getId(), subject.getId())
                .orElseThrow(() -> new BadRequestException("Teacher is not assigned with subject Id : " + subject.getId()));


        if(!classSubjectRepository.existsBySubject_IdAndClassEntity_Id(subject.getId(), classEntity.getId())) {
            throw new BadRequestException("Current Subject has not assigned to this class yet.");

        }
        removeRelations(classSubject.getClassEntity(), classSubject.getSubject(), classSubject.getTeacher(), classSubject);
        addRelations(classEntity, subject, teacher, classSubject);

        ClassSubject created = classSubjectRepository.save(classSubject);
        return classSubjectMapper.toDTO(created);
    }

    @Override
    @Transactional
    public List<ClassSubjectDTO> bulkAssignSubjectsToClass(Long classId, List<TeacherSubjectRequestDTO> requestDTOS) {

        ClassEntity classEntity = allRepositoryMethods.getClassById(classId);
        List<ClassSubjectDTO> result = new ArrayList<>();

        for (TeacherSubjectRequestDTO item : requestDTOS) {

            Subject subject = allRepositoryMethods.getSubjectById(item.getSubjectId());

            Teacher teacher = teacherRepository
                    .findByIdAndTeacherSubjects_Subject_Id(item.getTeacherId(), item.getSubjectId())
                    .orElseThrow(() -> new BadRequestException("Teacher not assigned to subject"));

            boolean exist = classSubjectRepository
                    .existsByClassEntity_IdAndSubject_IdAndTeacher_Id(
                            classId, subject.getId(), teacher.getId());

            if (exist) continue;

            ClassSubject cs = new ClassSubject();
            addRelations(classEntity, subject, teacher, cs);

            result.add(classSubjectMapper.toDTO(
                    classSubjectRepository.save(cs)));
        }

        return result;
    }

    @Override
    @Transactional
    public void bulkRemoveSubjectsFromClass(Long classId,
                                            List<TeacherSubjectRequestDTO> requestDTOS) {
        ClassEntity classEntity = allRepositoryMethods.getClassById(classId);

        for (TeacherSubjectRequestDTO item : requestDTOS) {

            Long subjectId = item.getSubjectId();
            Long teacherId = item.getTeacherId();

            ClassSubject classSubject = classSubjectRepository
                    .findByClassEntity_IdAndSubject_IdAndTeacher_Id(classId, subjectId, teacherId)
                    .orElseThrow(() -> new BadRequestException("Mapping not found for class " + classId +
                            ", subject " + subjectId + ", teacher " + teacherId));

            removeRelations(classSubject.getClassEntity(), classSubject.getSubject(), classSubject.getTeacher(),
                    classSubject);

            classSubjectRepository.delete(classSubject);
        }
    }

    @Override
    public Map<String, Object> getClassSubjectsByClassIdPaged(Long classId, int page, int size) {
        if (!classRepository.existsById(classId)) {
            throw new BadRequestException("Class id: " + classId + " does not exist.");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<ClassSubject> classSubjectPage = classSubjectRepository.findByClassEntityId(classId, pageable);
        List<ClassSubjectDTO> classSubjects = classSubjectMapper.classDTOList(classSubjectPage.getContent());

        Map<String, Object> response = new HashMap<>();
        response.put("data", classSubjects);
        response.put("currentPage", classSubjectPage.getNumber());
        response.put("totalItems", classSubjectPage.getTotalElements());
        response.put("totalPages", classSubjectPage.getTotalPages());
        response.put("pageSize", classSubjectPage.getSize());

        return response;
    }
}