package com.example.demo.ServiceImpl;

import com.example.demo.DTO.StudentFeeResponseDTO;
import com.example.demo.ENTITY.ClassEntity;
import com.example.demo.ENTITY.FeeStructure;
import com.example.demo.ENTITY.Student;
import com.example.demo.ENTITY.StudentFee;
import com.example.demo.Helper.Status;
import com.example.demo.Helper.StudentFeeStatus;
import com.example.demo.Mapper.StudentFeeMapper;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.Repository.ClassRepository;
import com.example.demo.Repository.FeeStructureRepository;
import com.example.demo.Repository.StudentFeeRepository;
import com.example.demo.Repository.StudentRepository;
import com.example.demo.RequestDTO.StudentFeeRequestDTO;
import com.example.demo.Service.StudentFeeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StudentFeeServiceImpl implements StudentFeeService {
    private final StudentFeeRepository studentFeeRepository;
    private final StudentFeeMapper studentFeeMapper;
    private final AllRepositoryMethods allRepositoryMethods;
    private final FeeStructureRepository feeStructureRepository;
    private final ClassRepository classRepository;
    private final StudentRepository studentRepository;


    private void checkValidations(Long feeStructureId, Long studentId, Long classId) {

        if (feeStructureId == null)
            throw new BadRequestException("FeeStructure id can't be null");

        if (studentId == null)
            throw new BadRequestException("Student id can't be null");

        if (classId == null)
            throw new BadRequestException("Class id can't be null");

        if (!feeStructureRepository.existsById(feeStructureId))
            throw new BadRequestException("FeeStructure not exists.");

        if (!studentRepository.existsById(studentId))
            throw new BadRequestException("Student not exists.");

        if (!classRepository.existsById(classId))
            throw new BadRequestException("Class not exists.");
    }

    private void checkRelations(Student student, FeeStructure feeStructure, ClassEntity classEntity) {

        if (!feeStructure.getClassEntity().getId().equals(classEntity.getId())) {
            throw new BadRequestException("FeeStructure does not belong to this class.");
        }

        if(!classRepository.existsByFeeStructureList_Id(feeStructure.getId())) {
            throw new BadRequestException("FeeStructure not belongs to this class.");

        }

        if(studentFeeRepository.existsByStudent_IdAndClassEntity_IdAndFeeStructure_Id(student.getId(),
                classEntity.getId(), feeStructure.getId())) {
            throw new BadRequestException("student has already opt this feeStructure before.");
        }
    }

    private void addRelations(StudentFee studentFee, Student student, ClassEntity classEntity) {
        student.addStudentFee(studentFee);
        classEntity.addStudentFee(studentFee);

        studentFee.setStudent(student);
        studentFee.setClassEntity(classEntity);
    }

    private StudentFeeStatus getStatus(StudentFee studentFee) {

        double total = studentFee.getTotalAmount();
        double paid = studentFee.getPaidAmount();
        LocalDate dueDate = studentFee.getDueDate();

        if (Double.compare(paid, total) >= 0) {
            return StudentFeeStatus.PAID;
        }

        if (Double.compare(paid, 0) == 0 && LocalDate.now().isAfter(dueDate)) {
            return StudentFeeStatus.OVERDUE;
        }

        if (paid > 0 && paid < total) {
            return StudentFeeStatus.PARTIALLY_PAID;
        }

        return StudentFeeStatus.PENDING;
    }

    @Transactional
    @Override
    public StudentFeeResponseDTO createStudentFee(StudentFeeRequestDTO requestDto) {

        checkValidations(requestDto.getFeeStructureId(), requestDto.getStudentId(), requestDto.getClassEntityId());

        FeeStructure feeStructure = allRepositoryMethods.getFeesStructureById(requestDto.getFeeStructureId());
        Student student = allRepositoryMethods.getStudentById(requestDto.getStudentId());
        ClassEntity classEntity = allRepositoryMethods.getClassById(requestDto.getClassEntityId());

        checkRelations(student, feeStructure, classEntity);

        StudentFee studentFee = new StudentFee();

        addRelations(studentFee, student, classEntity);
        studentFee.setFeeStructure(feeStructure);
        studentFee.setTotalAmount(feeStructure.getAmount());
        studentFee.setPaidAmount(0.0);
        studentFee.setRemainingAmount(feeStructure.getAmount());
        studentFee.setDueDate(requestDto.getDueDate());
        studentFee.setStatus(StudentFeeStatus.PENDING);

        StudentFee saved = studentFeeRepository.save(studentFee);
        return studentFeeMapper.toDTO(saved);
    }

    @Override
    public StudentFeeResponseDTO getStudentFeeById(Long id) {
        StudentFee studentFee = allRepositoryMethods.getStudentFeeById(id);
        return studentFeeMapper.toDTO(studentFee);
    }

    @Override
    public List<StudentFeeResponseDTO> getAllStudentFees() {
        List<StudentFee> studentFees = studentFeeRepository.findAll();
        if(studentFees.isEmpty()) {
            return List.of();
        }
        return studentFeeMapper.toDTOList(studentFees);
    }

    @Transactional
    @Override
    public StudentFeeResponseDTO updateStudentFee(Long id, StudentFeeRequestDTO requestDto) {
        checkValidations(requestDto.getFeeStructureId(), requestDto.getStudentId(), requestDto.getClassEntityId());
        StudentFee studentFee = allRepositoryMethods.getStudentFeeById(id);
        Long classId = studentFee.getClassEntity().getId();
        Long studentId = studentFee.getStudent().getId();

        if (requestDto.getStudentId() != null && !requestDto.getStudentId().equals(studentId)) {
            classId = requestDto.getClassEntityId();

        }
        if (requestDto.getClassEntityId() != null && !requestDto.getStudentId().equals(classId)) {
            studentId = requestDto.getStudentId();
        }

        Student student = allRepositoryMethods.getStudentById(studentId);
        ClassEntity classEntity = allRepositoryMethods.getClassById(classId);
        Set<Long> feeTypeIds = student.getFeeTypesId();

        if(requestDto.getFeeStructureId() != null) {
            if(feeTypeIds.contains(requestDto.getFeeStructureId())) {
                throw new BadRequestException("FeeStructure id" +
                        requestDto.getFeeStructureId() + " Already includes in fees.");
            }
        }

        FeeStructure feeStructure = allRepositoryMethods.getFeesStructureById(requestDto.getFeeStructureId());
        checkRelations(student, feeStructure, classEntity);
        addRelations(studentFee, student, classEntity);
        studentFee.setFeeStructure(feeStructure);
        studentFee.setTotalAmount(feeStructure.getAmount());
        studentFee.setPaidAmount(0.0);
        studentFee.setRemainingAmount(feeStructure.getAmount());
        studentFee.setDueDate(requestDto.getDueDate());
        studentFee.setStatus(StudentFeeStatus.PENDING);

        StudentFee saved = studentFeeRepository.save(studentFee);
        return studentFeeMapper.toDTO(saved);
    }

    @Override
    public List<StudentFeeResponseDTO> getFeesByStudentId(Long studentId) {
        Student student = allRepositoryMethods.getStudentById(studentId);
        List<StudentFee> studentFees = student.getStudentFeesList();

        if(studentFees.isEmpty()) {
            return List.of();
        }
        return studentFeeMapper.toDTOList(studentFees);
    }

    @Override
    public List<StudentFeeResponseDTO> getFeesByClassId(Long classId) {
        List<StudentFee> studentFees = studentFeeRepository.findByClassEntity_Id(classId);
        if(studentFees.isEmpty()) {
            return List.of();
        }
        return studentFeeMapper.toDTOList(studentFees);
    }

    @Override
    public List<StudentFeeResponseDTO> getFeeByStudentClassAndRollNo(Long classId, int rollNo) {
        if (!classRepository.existsById(classId)) {
            throw new BadRequestException("Class Id not exist: ");

        }

        Student student = studentRepository.findByClassEntity_IdAndRollNo(classId, rollNo);

        if(student == null) {
            throw new BadRequestException("Student not exist with Roll_no : " + rollNo);

        } else if(!student.getStatus().equals(Status.ACTIVE)) {
            throw new BadRequestException("Student " + student.getStatus());
        }

        List<StudentFee> studentFees = student.getStudentFeesList();
        if(studentFees.isEmpty()) {
            return List.of();
        }
        return studentFeeMapper.toDTOList(studentFees);
    }

    @Override
    public List<StudentFeeResponseDTO> getFeeByStudentMobile(String phone, StudentFeeRequestDTO requestDTO) {
        Student student = studentRepository.findByPhone(phone);

        if(student == null) {
            throw new BadRequestException("Student not exist with Phone : " + phone);

        } else if(!student.getStatus().equals(Status.ACTIVE)) {
            throw new BadRequestException("Student " + student.getStatus());
        }

        List<StudentFee> studentFees = student.getStudentFeesList();
        if(studentFees.isEmpty()) {
            return List.of();
        }
        return studentFeeMapper.toDTOList(studentFees);
    }
}
