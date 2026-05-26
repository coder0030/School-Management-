package com.example.demo.ServiceImpl;

import com.example.demo.DTO.FeeStructureDTO;
import com.example.demo.ENTITY.ClassEntity;
import com.example.demo.ENTITY.FeeStructure;
import com.example.demo.Helper.FeeType;
import com.example.demo.Mapper.FeesStructureMapper;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.MyException.DataNotFoundException;
import com.example.demo.MyException.DuplicateResourceException;
import com.example.demo.Repository.ClassRepository;
import com.example.demo.Repository.FeeStructureRepository;
import com.example.demo.RequestDTO.FeeStructureRequestDTO;
import com.example.demo.Service.FeeStructureService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
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
public class FeeStructureServiceImpl implements FeeStructureService {

    private final FeeStructureRepository feeStructureRepository;
    private final AllRepositoryMethods allRepositoryMethods;
    private final FeesStructureMapper feesStructureMapper;
    private final ClassRepository classRepository;
    private final ModelMapper modelMapper;

    private void validateClassId(Long classId) {
        if (classId == null) {
            throw new BadRequestException("classId can't be null.");
        }

        if (!classRepository.existsById(classId)) {
            throw new DataNotFoundException("Class not found with id: " + classId);
        }
    }

    private void checkForDuplicateFee(Long classId, FeeType feeType) {
        if (feeType == null) {
            throw new BadRequestException("Fee type can't be null.");
        }

        if (feeStructureRepository.existsByClassEntity_IdAndFeeType(classId, feeType)) {
            throw new DuplicateResourceException(
                    "Fee type already exists for class id: " + classId);
        }
    }

    private void validateAmount(Double amount) {
        if (amount == null) {
            throw new BadRequestException("Amount can't be null.");
        }

        if (amount < 0 || amount > 50000) {
            throw new BadRequestException("Amount must be between 0 and 50000.");
        }
    }

    public void checkFeesStructByTypeAndClassId(FeeType feeType, Long classId, Long currentId) {

        boolean exists;

        if (currentId == null) {
            exists = feeStructureRepository
                    .existsByFeeTypeAndClassEntity_Id(feeType, classId);
        } else {

            exists = feeStructureRepository
                    .existsByFeeTypeAndClassEntity_IdAndIdNot(feeType, classId, currentId);
        }

        if (exists) {
            throw new DuplicateResourceException(
                    "FeesStructure with FeeType: " + feeType +
                            " already exists for class ID: " + classId);
        }
    }

    @Transactional
    public FeeStructureDTO create(@Valid FeeStructureRequestDTO requestDTO) {

        validateClassId(requestDTO.getClassId());
        validateAmount(requestDTO.getAmount());
        checkForDuplicateFee(requestDTO.getClassId(), requestDTO.getFeeType());

        ClassEntity classEntity = allRepositoryMethods.getClassById(requestDTO.getClassId());
        checkFeesStructByTypeAndClassId(requestDTO.getFeeType(), requestDTO.getClassId(), null);

        FeeStructure feeStructure = new FeeStructure();
        feeStructure = feesStructureMapper.toUpdate(requestDTO, feeStructure);

        classEntity.addFeesStructure(feeStructure);

        FeeStructure saved = feeStructureRepository.save(feeStructure);
        return feesStructureMapper.toDTO(saved);
    }


    @Transactional
    public void deleteById(Long id) {
        FeeStructure feeStructure = allRepositoryMethods.getFeesStructureById(id);
        feeStructure.getClassEntity().removeFeesStructure(feeStructure);
        feeStructureRepository.delete(feeStructure);
    }

    @Transactional
    public void deleteAll() {
        List<FeeStructure> list = feeStructureRepository.findAll();

        if (list.isEmpty()) {
            throw new DataNotFoundException("No FeeStructure exists.");
        }

        list.forEach(fs -> fs.getClassEntity().removeFeesStructure(fs));
        feeStructureRepository.deleteAll();
    }

    public FeeStructureDTO getById(Long id) {
        FeeStructure feeStructure = allRepositoryMethods.getFeesStructureById(id);
        return feesStructureMapper.toDTO(feeStructure);
    }

    public Page<FeeStructureDTO> getAll(int pageNo, int pageSize) {
        if(pageSize > AttendanceServiceImpl.maxPageSize) pageSize = AttendanceServiceImpl.maxPageSize;

        Sort sort = Sort.by("classEntity_Id").descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<FeeStructure> feeStructurePage = feeStructureRepository.findAll(pageable);

        if(feeStructurePage.getContent().isEmpty()) {
            return Page.empty(pageable);
        }
        return feeStructurePage.map(fs ->
                modelMapper.map(fs, FeeStructureDTO.class));
    }

    @Transactional
    public FeeStructureDTO updateById(Long id, @Valid FeeStructureRequestDTO requestDTO) {

        FeeStructure feeStructure = allRepositoryMethods.getFeesStructureById(id);

        if (requestDTO.getAmount() != null) {
            validateAmount(requestDTO.getAmount());
        }

        if (requestDTO.getClassId() != null) {
            validateClassId(requestDTO.getClassId());
        }

        Long finalClassId = requestDTO.getClassId() != null ? requestDTO.getClassId() : feeStructure.getClassEntity().getId();
        FeeType finalFeeType = requestDTO.getFeeType() != null ? requestDTO.getFeeType() : feeStructure.getFeeType();

        checkFeesStructByTypeAndClassId(finalFeeType, finalClassId, id);

        if (requestDTO.getClassId() != null &&
                !feeStructure.getClassEntity().getId().equals(requestDTO.getClassId())) {

            ClassEntity oldClass = feeStructure.getClassEntity();
            ClassEntity newClass = allRepositoryMethods.getClassById(requestDTO.getClassId());

            oldClass.removeFeesStructure(feeStructure);
            newClass.addFeesStructure(feeStructure);
        }

        feesStructureMapper.toUpdate(requestDTO, feeStructure);
        FeeStructure updated = feeStructureRepository.save(feeStructure);
        return feesStructureMapper.toDTO(updated);
    }

    @Transactional
    public FeeStructureDTO partialUpdateById(Long id, FeeStructureRequestDTO requestDTO) {

        FeeStructure feeStructure = allRepositoryMethods.getFeesStructureById(id);

        if (requestDTO.getAmount() != null) {
            validateAmount(requestDTO.getAmount());
        }

        if (requestDTO.getClassId() != null) {
            validateClassId(requestDTO.getClassId());
        }

        if (requestDTO.getFeeType() != null || requestDTO.getClassId() != null) {
            Long classId = requestDTO.getClassId() != null ? requestDTO.getClassId() : feeStructure.getClassEntity().getId();
            FeeType feeType = requestDTO.getFeeType() != null ? requestDTO.getFeeType() : feeStructure.getFeeType();

            checkFeesStructByTypeAndClassId(feeType, classId, id);
        }

        if (requestDTO.getClassId() != null) {
            ClassEntity oldClass = feeStructure.getClassEntity();
            ClassEntity newClass = allRepositoryMethods.getClassById(requestDTO.getClassId());

            oldClass.removeFeesStructure(feeStructure);
            newClass.addFeesStructure(feeStructure);
        }

        feeStructure = feesStructureMapper.toPartialUpdate(requestDTO, feeStructure);

        FeeStructure updated = feeStructureRepository.save(feeStructure);
        return feesStructureMapper.toDTO(updated);
    }
}