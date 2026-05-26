package com.example.demo.ServiceImpl;

import com.example.demo.DTO.SubjectDTO;
import com.example.demo.ENTITY.Subject;
import com.example.demo.Mapper.SubjectMapper;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.MyException.DataNotFoundException;
import com.example.demo.MyException.DuplicateResourceException;
import com.example.demo.MyException.IncompleteDataException;
import com.example.demo.Repository.SubjectRepository;
import com.example.demo.RequestDTO.SubjectRequestDTO;
import com.example.demo.Service.SubjectService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;
    private final AllRepositoryMethods allRepositoryMethods;


    private void checkExistence(String subjectCode, String subjectName) {
        if(subjectRepository.existsBySubjectCode(subjectCode)) {
            throw new BadRequestException("Subject code already exists.");
        }
        if (subjectRepository.existsBySubjectName(subjectName)) {
            throw new BadRequestException("Subject name already exists.");

        }
    }

    private void checkExistenceForUpdate(String subjectCode, String subjectName, Long excludeId) {

        List<Subject> subjects = subjectRepository
                .findAllBySubjectCodeOrSubjectName(subjectCode, subjectName);

        for (Subject subject : subjects) {
            if (!subject.getId().equals(excludeId)) {
                throw new DuplicateResourceException(
                        "Subject with code '" + subjectCode +
                                "' or name '" + subjectName + "' already exists"
                );
            }
        }
    }

    @Transactional
    public SubjectDTO createSubject(SubjectRequestDTO subjectRequestDTO) {
        checkExistence(subjectRequestDTO.getSubjectCode(), subjectRequestDTO.getSubjectName());

        Subject subject = new Subject();
        subject = subjectMapper.requestToEntity(subjectRequestDTO, subject);

        Subject saved = subjectRepository.save(subject);
        return subjectMapper.toDTO(saved);
    }


    public List<SubjectDTO> getAllSubjects() {
        List<Subject> subjects = subjectRepository.findAll();
        if(subjects.isEmpty()) {
            return List.of();
        }

        return subjectMapper.toDTOList(subjects);
    }

    public SubjectDTO getSubjectById(Long id) {
        Subject subject = allRepositoryMethods.getSubjectById(id);
        return subjectMapper.toDTO(subject);
    }

    @Transactional
    public String deleteSubjectById(Long id) {
        Subject subject = allRepositoryMethods.getSubjectById(id);
        subjectRepository.delete(subject);
        return "Deleted Successfully.";
    }

    @Transactional
    public SubjectDTO updateSubjectById(Long id, SubjectRequestDTO subjectRequestDTO) {
        Subject subject = allRepositoryMethods.getSubjectById(id);

        if(subjectRequestDTO.getSubjectCode() == null || subjectRequestDTO.getSubjectName() == null) {
            throw new BadRequestException("Request Body is not completed, fill full details.");
        }
        checkExistence(subjectRequestDTO.getSubjectCode(), subjectRequestDTO.getSubjectName());
        subject = subjectMapper.requestToEntity(subjectRequestDTO, subject);
        Subject updated = subjectRepository.save(subject);
        return subjectMapper.toDTO(updated);
    }

    @Transactional
    public SubjectDTO partialUpdateSubjectById(Long id, SubjectRequestDTO subjectRequestDTO) {
        Subject subject = allRepositoryMethods.getSubjectById(id);

        String newSubjectCode = subjectRequestDTO.getSubjectCode() != null ?
                subjectRequestDTO.getSubjectCode() : subject.getSubjectCode();

        String newSubjectName = subjectRequestDTO.getSubjectName() != null ?
                subjectRequestDTO.getSubjectName() : subject.getSubjectName();

        if(subjectRequestDTO.getSubjectCode() != null || subjectRequestDTO.getSubjectName() != null) {
            checkExistenceForUpdate(newSubjectCode, newSubjectName, id);
        }

        subject = subjectMapper.toEntity(subjectRequestDTO, subject);
        Subject updated = subjectRepository.save(subject);
        return subjectMapper.toDTO(updated);
    }

}
