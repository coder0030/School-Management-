package com.example.demo.ServiceImpl;

import com.example.demo.DTO.ClassDTO;
import com.example.demo.ENTITY.ClassEntity;
import com.example.demo.Mapper.ClassMapper;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.MyException.DataNotFoundException;
import com.example.demo.MyException.InternalServerIssue;
import com.example.demo.Repository.ClassRepository;
import com.example.demo.RequestDTO.ClassRequestDTO;
import com.example.demo.Service.ClassService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClassServiceImpl implements ClassService {

    private final ClassRepository classRepository;
    private final ClassMapper classMapper;
    private final AllRepositoryMethods allRepositoryMethods;
    private final ModelMapper modelMapper;

   private void checkDuplicates(String className, String section, String roomNo, Long id) {
       if (classRepository.existsByClassNameAndSectionAndIdNot(className.toUpperCase(), section.toLowerCase(), id)) {
           throw new BadRequestException("ClassName : " + className + " and Section : " +
                   section + " already exists.");
       } if(classRepository.existsByRoomNoAndIdNot(roomNo, id)) {
           throw new BadRequestException("This room no. already part of the different class.");
       }
   }

    public ClassDTO createClass(ClassRequestDTO classRequestDTO) {

        checkDuplicates(classRequestDTO.getClassName(), classRequestDTO.getSection(), classRequestDTO.getRoomNo(), null);
        ClassEntity classEntity = new ClassEntity();
        classEntity = classMapper.requestToEntity(classEntity,classRequestDTO);
        ClassEntity created = classRepository.save(classEntity);
        return classMapper.toDTO(created);
    }

    @Override
    public Page<ClassDTO> getAllClasses(int pageNo, int pageSize) {
        if(pageSize > AttendanceServiceImpl.maxPageSize) pageSize = AttendanceServiceImpl.maxPageSize;

        Sort sort = Sort.by("className").ascending()
                .and(Sort.by("id").ascending());

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<ClassEntity> classEntityPage = classRepository.findAll(pageable);

        if(classEntityPage.getContent().isEmpty()) {
            return Page.empty(pageable);
        }
        return classEntityPage.map(classMapper::toDTO);
    }

    public void deleteClassById(Long id) {
        ClassEntity classEntity = classRepository.findById(id).orElseThrow(() ->
                new BadRequestException("Id : " + id + " not found")
        );

        classRepository.delete(classEntity);

    }

    public ClassDTO getClassById(Long id) {
        ClassEntity classEntity = allRepositoryMethods.getClassById(id);

        return classMapper.toDTO(classEntity);
    }

    public ClassDTO updateClassById(Long id, ClassRequestDTO classRequestDTO) {
        ClassEntity classEntity = allRepositoryMethods.getClassById(id);

        String className = (classRequestDTO.getClassName() != null ? classRequestDTO.getClassName() : null);
        String section = (classRequestDTO.getSection() != null ? classRequestDTO.getSection() : null);
        String roomNo = (classRequestDTO.getRoomNo() != null ? classRequestDTO.getRoomNo() : null);

        checkDuplicates(className, section, roomNo, id);

        classEntity = classMapper.requestToEntity(classEntity,classRequestDTO);
        ClassEntity updated = classRepository.save(classEntity);
        return classMapper.toDTO(updated);
    }

    @Transactional
    public ClassDTO partialUpdateClassById(Long id, ClassRequestDTO classRequestDTO) {
        ClassEntity classEntity = allRepositoryMethods.getClassById(id);

        String className = (classRequestDTO.getClassName() != null ? classRequestDTO.getClassName() : null);
        String section = (classRequestDTO.getSection() != null ? classRequestDTO.getSection() : null);
        String roomNo = (classRequestDTO.getRoomNo() != null ? classRequestDTO.getRoomNo() : null);
        checkDuplicates(className, section, roomNo, id);

        classEntity.setClassName(className);
        classEntity.setSection(section);
        classEntity.setRoomNo(roomNo);

        classEntity = classMapper.partialToEntity(classRequestDTO, classEntity);
        return classMapper.toDTO(classRepository.save(classEntity));
    }

    public List<ClassDTO> getAllClasses() {
        List<ClassEntity> allClasses = classRepository.findAll();
        if(allClasses.isEmpty()) {
            throw new IllegalArgumentException("Data not found.");
        }
        return classMapper.classDTOList(allClasses);
    }
}
