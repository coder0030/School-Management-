package com.example.demo.ServiceImpl;

import com.example.demo.DTO.BulkFeePaymentResponseDTO;
import com.example.demo.DTO.FeePaymentDTO;
import com.example.demo.ENTITY.*;
import com.example.demo.Helper.FeeType;
import com.example.demo.Helper.PaymentMode;
import com.example.demo.Helper.Status;
import com.example.demo.Helper.StudentFeeStatus;
import com.example.demo.Mapper.FeePaymentMapper;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.Repository.ClassRepository;
import com.example.demo.Repository.FeePaymentRepository;
import com.example.demo.Repository.StudentFeeRepository;
import com.example.demo.Repository.StudentRepository;
import com.example.demo.RequestDTO.FeePaymentRequestDTO;
import com.example.demo.Service.FeePaymentService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class FeePaymentServiceImpl implements FeePaymentService {
    private final AllRepositoryMethods allRepositoryMethods;
    private final FeePaymentRepository feePaymentRepository;
    private final FeePaymentMapper feePaymentMapper;
    private final StudentFeeRepository studentFeeRepository;
    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;

    private void checkStudentFeeId(Long studentFeeId) {
        if (studentFeeId == null) {
            throw new BadRequestException("StudentFee id can't be null");
        }

        if (!studentFeeRepository.existsById(studentFeeId)) {
            throw new BadRequestException("StudentFee id not found");
        }
    }

    private void validateAmount(double amount, double remainingAmount) {
        if(amount <= 0) {
            throw new BadRequestException("Amount must be greater than zero.");

        } if(amount > remainingAmount) {
            throw new BadRequestException("Amount must be less than equal to remaining Amount.");
        }
    }

    private FeePayment getStudentFeeByIdAndStatus(@NotNull(message = "StudentFee id is required") Long studentFeeId, Status status) {
      FeePayment feePayment = feePaymentRepository.findById(studentFeeId).orElse(null);
      if(feePayment == null) {
          throw new BadRequestException("Student i");
      }

      if(!feePayment.getStudent().getStatus().equals(Status.ACTIVE)) {
           throw new BadRequestException("Student : " + feePayment.getStudent().getStatus());
        }

        return feePayment;
    }


    private double calculateRemainingAmount(StudentFee studentFee, double amountToPay) {

        double remaining = studentFee.getRemainingAmount();

        if (amountToPay <= 0) {
            throw new BadRequestException("Amount must be greater than zero");
        }

        if (amountToPay > remaining) {
            throw new BadRequestException(
                    "Payment exceeds remaining amount. Remaining: " + remaining);
        }

        double newRemaining = remaining - amountToPay;

        return Math.max(newRemaining, 0);
    }

    private StudentFeeStatus getStudentFeeStatus(double remaining, double total) {
        if (remaining <= 0) return StudentFeeStatus.PAID;
        if (remaining < total) return StudentFeeStatus.PARTIALLY_PAID;
        return StudentFeeStatus.PENDING;
    }

    private String generateUniqueReceiptNumber(Student student) {
        String receiptNumber = String.format("RCP-%d-%s-%d",
                student.getId(),
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                System.currentTimeMillis() % 10000
        );

        if (feePaymentRepository.existsByReceiptNumber(receiptNumber)) {
            receiptNumber = receiptNumber + "-" + UUID.randomUUID().toString().substring(0, 4);
        }

        return receiptNumber;
    }

    private List<StudentFee> getStudentFeeByClassAndRollNo(Long classId, Integer rollNumber) {
        if(classId == null || rollNumber == null) {
            throw new BadRequestException("class id or Roll_no can't be null");
        }

        if(!classRepository.existsById(classId)) {
            throw new BadRequestException("class id not exists");

        }

        Student student = studentRepository.findByClassEntity_IdAndRollNo(classId, rollNumber);
        if(student == null) {
            throw new BadRequestException("Roll_no not exists");
        }

        return student.getStudentFeesList();
    }


    private void addRelations(FeePayment feePayment, StudentFee studentFee, Student student, ClassEntity classEntity) {
        classEntity.addFeePayments(feePayment);
        student.addFeePayments(feePayment);
        studentFee.addFeePayments(feePayment);

        feePayment.setStudent(student);
        feePayment.setClassEntity(classEntity);
        feePayment.setStudentFee(studentFee);
    }

    private void removeRelations(FeePayment feePayment, StudentFee studentFee, Student student, ClassEntity classEntity) {
        if(student != null) {
            student.removeFeePayments(feePayment);
        }
        if(classEntity != null) {
            classEntity.removeFeePayments(feePayment);
        }
        if(studentFee != null) {
            studentFee.removeFeePayments(feePayment);
        }

        feePayment.setStudent(student);
        feePayment.setClassEntity(classEntity);
        feePayment.setStudentFee(studentFee);
    }

    private FeePayment makePayment(StudentFee studentFee, double amount) {

        double newRemaining = calculateRemainingAmount(studentFee, amount);
        studentFee.setRemainingAmount(newRemaining);

        StudentFeeStatus feeStatus =
                getStudentFeeStatus(newRemaining, studentFee.getTotalAmount());

        studentFee.setStatus(feeStatus);

        FeePayment feePayment = new FeePayment();
        addRelations(feePayment, studentFee,
                studentFee.getStudent(), studentFee.getClassEntity());

        feePayment.setAmountPaid(amount);
        feePayment.setPaymentDate(LocalDate.now());
        feePayment.setStatus(feeStatus.name());
        feePayment.setReceiptNumber(
                generateUniqueReceiptNumber(studentFee.getStudent()));
        feePayment.setRemarks("Payment successful");

        return feePayment;
    }

    @Override
    @Transactional
    public FeePaymentDTO payFee(FeePaymentRequestDTO requestDTO) {

        checkStudentFeeId(requestDTO.getStudentFeeId());

        StudentFee studentFee =
                allRepositoryMethods.getStudentFeeById(requestDTO.getStudentFeeId());

        if (requestDTO.getPaymentMode() != PaymentMode.CASH &&
                requestDTO.getTransactionId() == null) {
            throw new BadRequestException("TransactionId required for online payment");
        }

        FeePayment feePayment = makePayment(studentFee, requestDTO.getAmountPaid());

        feePayment.setPaymentMode(requestDTO.getPaymentMode());
        feePayment.setTransactionId(requestDTO.getTransactionId());
        feePayment.setFeeType(
                requestDTO.getFeeType() == null ?
                        FeeType.TUITION_FEE :
                        FeeType.valueOf(requestDTO.getFeeType())
        );

        FeePayment saved = feePaymentRepository.save(feePayment);
        return feePaymentMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public FeePaymentDTO makePaymentByStudentId(FeePaymentRequestDTO requestDTO) {

        Student student = allRepositoryMethods.getStudentById(requestDTO.getStudentId());

        List<StudentFee> fees = studentFeeRepository.findByStudent_Id(student.getId());

        FeeType feeType = requestDTO.getFeeType() == null ?
                FeeType.TUITION_FEE :
                FeeType.valueOf(requestDTO.getFeeType());

        StudentFee studentFee = fees.stream()
                .filter(sf -> sf.getFeeStructure().getFeeType() == feeType)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Fee not found for student"));

        FeePayment payment = makePayment(studentFee, requestDTO.getAmountPaid());

        payment.setPaymentMode(requestDTO.getPaymentMode());
        payment.setTransactionId(requestDTO.getTransactionId());
        payment.setFeeType(feeType);

        return feePaymentMapper.toDTO(feePaymentRepository.save(payment));
    }

    @Override
    public FeePaymentDTO makePaymentByClassAndRollNumber(FeePaymentRequestDTO requestDTO) {
        List<StudentFee> studentFees = getStudentFeeByClassAndRollNo(requestDTO.getClassId(), requestDTO.getRollNumber());

        FeeType feeType = requestDTO.getFeeType() == null ?
                FeeType.TUITION_FEE :
                FeeType.valueOf(requestDTO.getFeeType());

        StudentFee studentFee = studentFees.stream()
                .filter(sf -> sf.getFeeStructure().getFeeType() == feeType)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Fee not found for student"));

        FeePayment payment = makePayment(studentFee, requestDTO.getAmountPaid());

        payment.setPaymentMode(requestDTO.getPaymentMode());
        payment.setTransactionId(requestDTO.getTransactionId());
        payment.setFeeType(feeType);

        return feePaymentMapper.toDTO(feePaymentRepository.save(payment));
    }

    @Override
    public FeePaymentDTO makePaymentByMobileNumber(FeePaymentRequestDTO requestDTO) {
        Student student = studentRepository.findByPhone(requestDTO.getMobileNumber());
        if(student == null) {
            throw new BadRequestException("Student not found.");
        }

        List<StudentFee> studentFees = student.getStudentFeesList();
        FeeType feeType = requestDTO.getFeeType() == null ?
                FeeType.TUITION_FEE :
                FeeType.valueOf(requestDTO.getFeeType());

        StudentFee studentFee = studentFees.stream()
                .filter(sf -> sf.getFeeStructure().getFeeType() == feeType)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Fee not found for student"));

        FeePayment payment = makePayment(studentFee, requestDTO.getAmountPaid());

        payment.setPaymentMode(requestDTO.getPaymentMode());
        payment.setTransactionId(requestDTO.getTransactionId());
        payment.setFeeType(feeType);

        return feePaymentMapper.toDTO(feePaymentRepository.save(payment));
    }

    @Override
    public BulkFeePaymentResponseDTO makeBulkPaymentForStudentFee(Long studentFeeId, List<FeePaymentRequestDTO> requestDTOs) {
        List<FeePaymentDTO> feePayments = new ArrayList<>();
        List<FeePaymentRequestDTO> skippedStudent = new ArrayList<>();

        for(FeePaymentRequestDTO dto : requestDTOs) {
            try {
                feePayments.add(payFee(dto));

            } catch(Exception e) {
                skippedStudent.add(dto);
            }
        }

        BulkFeePaymentResponseDTO responseDTO = new BulkFeePaymentResponseDTO();
        responseDTO.setSkippedStudents(skippedStudent);
        responseDTO.setSuccessfulStudents(feePayments);
        String remarks = (feePayments.size() > 0 ? "Students saved successfully." : "Error found.");
        responseDTO.setRemarks(remarks);

        return responseDTO;
    }

    @Override
    public List<FeePaymentDTO> getPaymentsByStudentFeeId(Long studentFeeId) {
        List<FeePayment> feePayments = feePaymentRepository.findByStudentFee_Id(studentFeeId);
        if(feePayments.isEmpty()) {
            return List.of();
        }

        return feePaymentMapper.toDTOList(feePayments);
    }

    @Override
    public FeePaymentDTO getPaymentByReceiptNumber(String receiptNumber) {
        FeePayment feePayment = feePaymentRepository.findByReceiptNumber(receiptNumber).orElseThrow(
                () -> new BadRequestException("No payment found."));

        return feePaymentMapper.toDTO(feePayment);
    }
}