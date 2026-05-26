package com.example.demo.ServiceImpl;

import com.example.demo.DTO.ParentResponseDTO;
import com.example.demo.DTO.StudentDTO;
import com.example.demo.ENTITY.*;
import com.example.demo.Helper.Role;
import com.example.demo.Helper.Status;
import com.example.demo.Mapper.ParentMapper;
import com.example.demo.Mapper.StudentMapper;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.Repository.ParentRepository;
import com.example.demo.Repository.StudentRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.RequestDTO.ParentRequestDTO;
import com.example.demo.Service.ParentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParentServiceImpl implements ParentService {

    private final ParentRepository parentRepository;
    private final UserRepository userRepository;
    private final ParentMapper parentMapper;
    private final StudentRepository studentRepository;
    private final AllRepositoryMethods allRepositoryMethods;
    private final StudentMapper studentMapper;

    private void addRelations(Parent parent, Student student) {
        parent.addStudents(student);
        student.setParent(parent);
    }

    private void removeRelations(Parent parent, Student student) {
        if (parent.getStudentList() != null) {
            parent.removeStudents(student);
            student.setParent(null);
        }
    }

    private void checkExistenceForCreate(String email, String phone) {
        if (email != null && parentRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already exists: " + email);
        }
        if (phone != null && parentRepository.existsByPhone(phone)) {
            throw new BadRequestException("Phone already exists: " + phone);
        }
    }

    private void checkExistenceForUpdate(String email, String phone, Long excludeStudentId) {
        if (email != null && parentRepository.existsByEmailAndIdNot(email, excludeStudentId)) {
            throw new BadRequestException("Email already exists: " + email);
        }
        if (phone != null && parentRepository.existsByPhoneAndIdNot(phone, excludeStudentId)) {
            throw new BadRequestException("Phone already exists: " + phone);
        }
    }

    private boolean studentExistInParent(Student student, Parent parent) {
        return parent.getStudentList().stream()
                .anyMatch(st -> st.getId().equals(student.getId()));

    }

    @Override
    @Transactional
    public ParentResponseDTO createParent(ParentRequestDTO requestDTO) {
        checkExistenceForCreate(requestDTO.getEmail(), requestDTO.getPhone());

        List<Student> studentList = new ArrayList<>();
        requestDTO.getStudentIds().forEach(id -> {
            Student student = studentRepository.findByIdAndStatus(String.valueOf(id), Status.ACTIVE).orElse(null);
            if (student == null) {
                throw new BadRequestException("Student id: " + id + " does not exist.");
            }
            studentList.add(student);
        });

        User user = userRepository.findByEmailOrUsernameAndIsActive(requestDTO.getEmail(), requestDTO.getEmail(), true)
                .orElseThrow(() -> new BadRequestException
                        ("No active user account found with this email. User must signup first."));

        Parent parent = new Parent();
        parent = parentMapper.toEntity(requestDTO, parent);

       for(Student student : studentList) {
           addRelations(parent, student);
       }

        parent.setUser(user);
        parent.addRoles(Role.ROLE_PARENT);
        userRepository.save(parent.getUser());
        Parent saved = parentRepository.save(parent);
        return parentMapper.toDto(saved);
    }

    @Override
    public ParentResponseDTO getParentById(Long id) {
        Parent parent = allRepositoryMethods.getParentById(id);
        return parentMapper.toDto(parent);

    }

    @Override
    public ParentResponseDTO getParentByCode(String parentCode) {
        return null;
    }

    @Override
    public ParentResponseDTO getParentByEmail(String email) {
        Parent parent = allRepositoryMethods.getParentByEmail(email);
        return parentMapper.toDto(parent);
    }

    @Override
    public Page<ParentResponseDTO> getAllParents(int pageNo, int pageSize) {
        if(pageSize > AttendanceServiceImpl.maxPageSize) pageSize = AttendanceServiceImpl.maxPageSize;

        Sort sort = Sort.by("author").descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);


        Page<Parent> parentPage = parentRepository.findAll(pageable);

        if (parentPage.getContent().isEmpty()) {
            return Page.empty(pageable);
        }

        return parentPage.map(parentMapper::toDto);
    }

    @Override
    public Page<ParentResponseDTO> getParentsWithMultipleChildren(int pageNo, int pageSize) {
        if(pageSize > AttendanceServiceImpl.maxPageSize) pageSize = AttendanceServiceImpl.maxPageSize;

        Sort sort = Sort.by("author").descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Parent> parentPage = parentRepository.findParentsWithMultipleChildren(pageable);

        return parentPage.map(parentMapper::toDto);
    }

    @Override
    public ParentResponseDTO updateParent(Long id, ParentRequestDTO requestDTO) {
        Parent parent = allRepositoryMethods.getParentById(id);
        List<Long> studentId = new ArrayList<>();

        String email = (requestDTO.getEmail() != null ? requestDTO.getEmail() : parent.getEmail());
        String phone = (requestDTO.getPhone() != null ? requestDTO.getPhone() : parent.getPhone());

        checkExistenceForUpdate(email, phone, id);
        parent.setEmail(email);
        parent.setPhone(phone);

        if(requestDTO.getStudentIds() != null) {
            for(Long ids : requestDTO.getStudentIds()) {

                Student student = allRepositoryMethods.getStudentById(ids);
                if(studentExistInParent(student, parent)) {
                    studentId.add(ids);
                    continue;
                }
                addRelations(parent, student);
                studentId.add(ids);
                parent.getStudentList().add(student);
            }
        }

        parent = parentMapper.updateToEntity(requestDTO, parent);
        ParentResponseDTO responseDTOS = parentMapper.toDto(parent);
        responseDTOS.setStudentIds(studentId);
        userRepository.save(parent.getUser());
        parentRepository.save(parent);
        return responseDTOS;
    }

    @Override
    public ParentResponseDTO partialUpdateParent(Long id, ParentRequestDTO requestDTO) {
        Parent parent = allRepositoryMethods.getParentById(id);
        List<Long> studentId = new ArrayList<>();

        String email = (requestDTO.getEmail() != null ? requestDTO.getEmail() : parent.getEmail());
        String phone = (requestDTO.getPhone() != null ? requestDTO.getPhone() : parent.getPhone());

        checkExistenceForUpdate(email, phone, id);

        parent.setEmail(email);
        parent.setPhone(phone);

        if(requestDTO.getStudentIds() != null) {
            for(Long ids : requestDTO.getStudentIds()) {

                Student student = allRepositoryMethods.getStudentById(ids);
                if(studentExistInParent(student, parent)) {
                    studentId.add(ids);
                    continue;
                }
                addRelations(parent, student);
                studentId.add(ids);
                parent.getStudentList().add(student);
            }
        }

        parent = parentMapper.toEntity(requestDTO, parent);
        ParentResponseDTO responseDTOS = parentMapper.toDto(parent);
        responseDTOS.setStudentIds(studentId);
        userRepository.save(parent.getUser());
        parentRepository.save(parent);
        return responseDTOS;
    }

    @Override
    public void deleteParent(Long id) {
        Parent parent = allRepositoryMethods.getParentById(id);
        User user = parent.getUser();
        user.setDeleted(true);
        parentRepository.delete(parent);
    }

    @Override
    public ParentResponseDTO addStudentToParent(Long parentId, Long studentId) {
        Parent parent = allRepositoryMethods.getParentById(parentId);
        Student student = allRepositoryMethods.getStudentById(studentId);

        if(studentExistInParent(student, parent)) {
            throw new BadRequestException("Student already added with parent Id : " + parentId);
        }

        addRelations(parent, student);
        return parentMapper.toDto(parentRepository.save(parent));
    }

    @Override
    public ParentResponseDTO removeStudentFromParent(Long parentId, Long studentId) {
        Parent parent = allRepositoryMethods.getParentById(parentId);
        Student student = allRepositoryMethods.getStudentById(studentId);

        if(!studentExistInParent(student, parent)) {
            throw new BadRequestException("Student not exists with parent Id : " + parentId);
        }

        removeRelations(parent, student);
        return parentMapper.toDto(parentRepository.save(parent));
    }

    @Override
    public List<StudentDTO> getAllChildrenOfParent(Long parentId) {
        Parent parent = allRepositoryMethods.getParentById(parentId);
        if(parent.getStudentList().isEmpty()) {
            return List.of();
        }

        return parent.getStudentList().stream()
                .map(studentMapper::toDto).collect(Collectors.toList());
    }
}
