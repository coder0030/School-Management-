package com.example.demo.ServiceImpl;

import com.example.demo.DTO.TeacherDTO;
import com.example.demo.ENTITY.*;
import com.example.demo.Helper.Role;
import com.example.demo.Mapper.TeacherMapper;
import com.example.demo.MyException.*;
import com.example.demo.Repository.*;
import com.example.demo.RequestDTO.TeacherRequestDTO;
import com.example.demo.Security.SecurityUtil;
import com.example.demo.Service.TeacherService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherMapper teacherMapper;
    private final AllRepositoryMethods allRepositoryMethods;
    private final ClassTeacherRepository classTeacherRepository;
    private final TeacherSubjectRepository teacherSubjectRepository;
    private final ClassSubjectRepository classSubjectRepository;
    private final UserRepository userRepository;

    private void addRelations(ClassEntity classEntity, Teacher teacher, Subject subject, TeacherSubject teacherSubject) {
        teacher.addTeacherSubject(teacherSubject);
        subject.addTeacherSubject(teacherSubject);
        classEntity.addTeacherSubject(teacherSubject);

        teacherSubject.setTeacher(teacher);
        teacherSubject.setSubject(subject);
        teacherSubject.setClassEntity(classEntity);
    }

    private void removeRelations(ClassEntity classEntity, Teacher teacher, Subject subject, TeacherSubject teacherSubject) {
        if(teacher != null) teacher.removeTeacherSubject(teacherSubject);
        if(subject != null) subject.removeTeacherSubject(teacherSubject);
        if(classEntity != null) classEntity.removeTeacherSubject(teacherSubject);

        teacherSubject.setTeacher(null);
        teacherSubject.setSubject(null);
        teacherSubject.setClassEntity(null);
    }

    private void checkExistence(String email, String phone) {
        if(email != null && teacherRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already exists.");
        }

        if(phone != null && teacherRepository.existsByPhone(phone)) {
            throw new BadRequestException("Phone already exists.");
        }

        if((email != null || phone != null) && allRepositoryMethods.existsTeacherByEmailOrPhone(email, phone)) {
            throw new BadRequestException("Teacher with Email : " + email + " or phone : " +
                    phone + " already exists.");
        }
    }

    private void validateTeacherSubjectClass(Long teacherId, Long subjectId, Long classId) {
        boolean teacherInClass = classTeacherRepository.existsByTeacher_IdAndClassEntity_Id(teacherId, classId);

        if (!teacherInClass) {
            throw new BadRequestException("Teacher is not assigned to this class");
        }

        boolean subjectInClass = classSubjectRepository.existsBySubject_IdAndClassEntity_Id(subjectId, classId);

        if (!subjectInClass) {
            throw new BadRequestException("Subject is not assigned to this class");
        }

        boolean alreadyExists = teacherSubjectRepository.existsByTeacher_IdAndSubject_IdAndClassEntity_Id(teacherId, subjectId, classId);

        if (alreadyExists) {
            throw new BadRequestException("Teacher already teaches this subject in this class");
        }
    }

    @Override
    @Transactional
    public TeacherDTO createTeacher(TeacherRequestDTO dto) {
        checkExistence(dto.getEmail(), dto.getPhone());
        User user = userRepository.findByEmailOrUsernameAndIsActive(dto.getEmail(), dto.getEmail(), true)
                .orElseThrow(() -> new BadRequestException
                        ("No active user account found with this email. User must signup first."));

        Teacher teacher = new Teacher();
        teacher = teacherMapper.updateEntityFromRequest(teacher, dto);
        teacher.setUser(user);

        teacher.addRoles(Role.ROLE_TEACHER);
        teacherRepository.save(teacher);
        return teacherMapper.toDto(teacher);
    }

    @Override
    @Transactional
    public TeacherDTO updateTeacher(Long teacherId, @Valid TeacherRequestDTO requestDTO) {
        boolean isAdmin = SecurityUtil.isAdmin();
        boolean isOwner = SecurityUtil.isOwner(teacherId);
        Teacher teacher = allRepositoryMethods.getTeacherById(teacherId);

        if (!isAdmin && !isOwner) {
            throw new com.example.demo.MyException.AccessDeniedException("You're not allowed for this operation.");
        }

        if(isAdmin || isOwner) {
            String email = teacher.getEmail(), phone = teacher.getPhone();


            if (requestDTO.getPhone() != null || !requestDTO.getPhone().equals(phone)) {
                phone = requestDTO.getPhone();

            }
            if (requestDTO.getEmail() != null || !requestDTO.getEmail().equals(email)) {
                email = requestDTO.getEmail();
            }

            if (teacherRepository.existsByEmailOrPhoneAndIdNot(email, phone, teacherId)) {
                throw new BadRequestException("Teacher already exists with email : " + email + " or " +
                        "phone : " + phone);
            }

            teacher.setEmail(email);
            teacher.setPhone(phone);

            teacher = teacherMapper.UpdateToEntity(requestDTO, teacher);

        }

        if(isOwner && !isAdmin) {
            if(requestDTO.getRole() != null) {
                throw new AccessDeniedException("Teachers can't add roles.");

            }
        }

        teacher = teacherMapper.updateEntityFromRequest(teacher, requestDTO);
        Teacher update = teacherRepository.save(teacher);
        return teacherMapper.toDto(update);
    }

    @Override
    @Transactional
    public void deleteTeacher(Long teacherId) {
        Teacher teacher = allRepositoryMethods.getTeacherById(teacherId);
        teacherRepository.delete(teacher);
    }

    @Override
    public TeacherDTO getTeacherById(Long teacherId) {
        Teacher teacher = allRepositoryMethods.getTeacherById(teacherId);
        return teacherMapper.toDto(teacher);
    }

    @Override
    public TeacherDTO partialUpdate(Long teacherId, TeacherRequestDTO requestDTO) {
        boolean isAdmin = SecurityUtil.isAdmin();
        boolean isOwner = SecurityUtil.isOwner(teacherId);
        Teacher teacher = allRepositoryMethods.getTeacherById(teacherId);

        if (!isAdmin && !isOwner) {
            throw new com.example.demo.MyException.AccessDeniedException("You're not allowed for this operation.");
        }

        if(isAdmin || isOwner) {

            String email = teacher.getEmail();
            String phone = teacher.getPhone();

            if (requestDTO.getEmail() != null &&
                    teacherRepository.existsByEmailAndIdNot(email, teacherId)) {

                throw new BadRequestException("Teacher already exists with email: " + email);

            } if (requestDTO.getPhone() != null &&
                    teacherRepository.existsByPhoneAndIdNot(phone, teacherId)) {

                throw new BadRequestException("Teacher already exists with phone: " + phone);
            }

                teacher.setEmail(email);
                teacher.setPhone(phone);
        }

        if(isOwner && !isAdmin) {
            if(requestDTO.getRole() != null) {
                throw new AccessDeniedException("Teachers can't update roles.");
            }
            requestDTO.setRole(null);
        }

        teacher = teacherMapper.updateEntityFromRequest(teacher, requestDTO);
        Teacher update = teacherRepository.save(teacher);
        return teacherMapper.toDto(update);
    }

    @Override
    public List<TeacherDTO> getAllTeacher() {
        List<Teacher> teachers = teacherRepository.findAll();
        if(teachers.isEmpty()) {
            return List.of();
        }
        return teacherMapper.toDtoList(teachers);
    }

    @Override
    public List<TeacherDTO> getTeacherByClass(Long classId) {
        allRepositoryMethods.getClassById(classId);

        return classTeacherRepository.findAllByClassEntity_Id(classId)
                .stream()
                .map(ClassTeacher::getTeacher)
                .map(teacherMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void assignTeacherSubjectToClass(Long teacherId, Long subjectId, Long classId) {
        validateTeacherSubjectClass(teacherId, subjectId, classId);

        Teacher teacher = allRepositoryMethods.getTeacherById(teacherId);
        Subject subject = allRepositoryMethods.getSubjectById(subjectId);
        ClassEntity classEntity = allRepositoryMethods.getClassById(classId);

        TeacherSubject teacherSubject = new TeacherSubject();
        removeRelations(classEntity, teacher, subject, teacherSubject);

        teacherSubjectRepository.save(teacherSubject);
    }

    @Override
    @Transactional
    public void removeTeacherSubjectToClass(Long teacherId, Long subjectId, Long classId) {
        TeacherSubject teacherSubject = teacherSubjectRepository
                .findByTeacher_IdAndSubject_IdAndClassEntity_Id(teacherId, subjectId, classId)
                .orElseThrow(() -> new BadRequestException("Teacher has not assigned this subject for class id: " + classId));

        Teacher teacher = teacherSubject.getTeacher();
        Subject subject = teacherSubject.getSubject();
        ClassEntity classEntity = teacherSubject.getClassEntity();

        removeRelations(classEntity, teacher, subject, teacherSubject);
        teacherSubjectRepository.delete(teacherSubject);
    }

    @Override
    public TeacherDTO addRoleToTeacher(Long teacherId, Role role) {
        Teacher teacher = allRepositoryMethods.getTeacherById(teacherId);
        if(teacher.getUser().getRoles().contains(role)) {
            throw new BadRequestException("Teacher already assigned with role : " + role);
        }

        teacher.addRoles(role);
        return teacherMapper.toDto(teacherRepository.save(teacher));
    }

    @Override
    public TeacherDTO removeRoleFromTeacher(Long teacherId, Role role) {
        Teacher teacher = allRepositoryMethods.getTeacherById(teacherId);
        if(!teacher.getUser().getRoles().contains(role)) {
            throw new BadRequestException("Teacher has not been assigned with role : " + role);
        }
        teacher.removeRoles(role);
        return teacherMapper.toDto(teacherRepository.save(teacher));
    }
}