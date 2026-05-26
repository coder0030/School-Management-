package com.example.demo.ServiceImpl;

import com.example.demo.DTO.TimetableDTO;
import com.example.demo.ENTITY.*;
import com.example.demo.Mapper.TimetableMapper;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.Repository.*;
import com.example.demo.RequestDTO.TimetableRequestDTO;
import com.example.demo.Service.TimetableService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimetableServiceImpl implements TimetableService {

    private final TimetableRepository timeTableRepository;
    private final TimetableMapper timetableMapper;
    private final AllRepositoryMethods allRepositoryMethods;
    private final TeacherRepository teacherRepository;
    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;

    private void checkValidation(Long subjectId, Long teacherId, Long classId) {
        if(subjectId == null) throw new BadRequestException("subjectId can't be null");
        if(teacherId == null) throw new BadRequestException("teacherId can't be null");
        if(classId == null) throw new BadRequestException("classId can't be null");

        if(!subjectRepository.existsById(subjectId))
            throw new BadRequestException("subjectId doesn't exist");

        if(!teacherRepository.existsById(teacherId))
            throw new BadRequestException("teacherId doesn't exist");

        if(!classRepository.existsById(classId))
            throw new BadRequestException("classId doesn't exist");
    }

    private void checkTimeValidation(LocalTime startTime, LocalTime endTime) {
        if(startTime == null || endTime == null)
            throw new BadRequestException("Start/End time can't be null");

        LocalTime earliest = LocalTime.of(8,0);
        LocalTime latest   = LocalTime.of(14,0);

        if(startTime.isBefore(earliest) || startTime.isAfter(latest))
            throw new BadRequestException("Start time must be between 8AM–2PM");

        if(endTime.isBefore(earliest) || endTime.isAfter(latest))
            throw new BadRequestException("End time must be between 8AM–2PM");

        if(endTime.isBefore(startTime))
            throw new BadRequestException("End time must be after start time");
    }

    private void checkValidRelations(Subject subject, ClassEntity classEntity, Teacher teacher) {

        boolean isTeacherAssignedToClass =
                teacher.getClassTeacherList().stream()
                        .anyMatch(c -> c.getClassEntity().getId().equals(classEntity.getId()));

        if (!isTeacherAssignedToClass) {
            throw new BadRequestException(
                    "Teacher is not assigned to class: " + classEntity.getId());
        }

        boolean isSubjectAssignedToClass =
                classEntity.getClassSubjects().stream()
                        .map(ClassSubject::getSubject)
                        .anyMatch(s -> s.getId().equals(subject.getId()));

        if (!isSubjectAssignedToClass) {
            throw new BadRequestException(
                    "Subject is not assigned to this class: " + subject.getId());
        }
    }

    private void addRelations(Timetable timetable, Teacher teacher, Subject subject, ClassEntity classEntity) {
        timetable.setTeacher(teacher);
        timetable.setSubject(subject);
        timetable.setClassEntity(classEntity);

        teacher.addTimetables(timetable);
        subject.addTimetables(timetable);
        classEntity.addTimetables(timetable);
    }

    private void removeRelations(Timetable timetable, Teacher teacher, Subject subject, ClassEntity classEntity) {
        if (teacher != null) teacher.removeTimetables(timetable);
        if (subject != null) subject.removeTimetables(timetable);
        if (classEntity != null) classEntity.removeTimetables(timetable);

        timetable.setTeacher(null);
        timetable.setSubject(null);
        timetable.setClassEntity(null);
    }

    private void validateTeacherTimeConflict(List<Timetable> lectures, LocalTime startTime, LocalTime endTime) {
        for (Timetable t : lectures) {
            boolean overlap = t.getStartTime().isBefore(endTime) && t.getEndTime().isAfter(startTime);
            if(overlap)
                throw new BadRequestException("Teacher already assigned to another class in this time slot");
        }
    }

    @Override
    public boolean isTimeSlotAvailable(Long classId, Long teacherId, DayOfWeek dayOfWeek,
                                       Integer periodNumber, LocalTime startTime,
                                       LocalTime endTime, Long excludeId) {

        if(dayOfWeek == null || periodNumber == null)
            throw new BadRequestException("Day and period required");

        checkTimeValidation(startTime,endTime);
        Long exclude = (excludeId == null) ? -1L : excludeId;

        boolean classBusy = timeTableRepository
                .existsByClassEntity_IdAndDayOfWeekAndPeriodNumberAndIdNot(
                        classId, dayOfWeek, periodNumber, exclude);

        if(classBusy)
            throw new BadRequestException("Class already has lecture in this period");

        List<Timetable> teacherLectures =
                timeTableRepository.findByTeacher_IdAndDayOfWeekAndIdNot(
                        teacherId, dayOfWeek, exclude);

        validateTeacherTimeConflict(teacherLectures,startTime,endTime);

        return true;
    }

    @Override
    public TimetableDTO createTimetable(TimetableRequestDTO requestDTO) {

        checkValidation(requestDTO.getSubjectId(), requestDTO.getTeacherId(), requestDTO.getClassId());

        Subject subject = allRepositoryMethods.getSubjectById(requestDTO.getSubjectId());
        Teacher teacher = allRepositoryMethods.getTeacherById(requestDTO.getTeacherId());
        ClassEntity classEntity = allRepositoryMethods.getClassById(requestDTO.getClassId());

        checkValidRelations(subject, classEntity, teacher);

        isTimeSlotAvailable(classEntity.getId(), teacher.getId(), requestDTO.getDayOfWeek(),
                requestDTO.getPeriodNumber(), requestDTO.getStartTime(), requestDTO.getEndTime(), null);

        Timetable timetable = timetableMapper.toEntity(new Timetable(), requestDTO);
        addRelations(timetable, teacher, subject, classEntity);

        Timetable saved = timeTableRepository.save(timetable);
        return timetableMapper.toDTO(saved);
    }

    @Override
    public TimetableDTO getTimetableById(Long id) {
        Timetable timetable = allRepositoryMethods.getTimetableById(id);
        return timetableMapper.toDTO(timetable);
    }

    @Override
    public List<TimetableDTO> getAllTimetables() {
        return timetableMapper.toDTOList(timeTableRepository.findAll());
    }

    @Override
    @Transactional
    public TimetableDTO updateTimetable(Long id, TimetableRequestDTO requestDTO) {

        Timetable timetable = allRepositoryMethods.getTimetableById(id);

        checkValidation(requestDTO.getSubjectId(), requestDTO.getTeacherId(), requestDTO.getClassId());

        Subject subject = allRepositoryMethods.getSubjectById(requestDTO.getSubjectId());
        Teacher teacher = allRepositoryMethods.getTeacherById(requestDTO.getTeacherId());
        ClassEntity classEntity = allRepositoryMethods.getClassById(requestDTO.getClassId());

        checkValidRelations(subject, classEntity, teacher);

        isTimeSlotAvailable(requestDTO.getClassId(), requestDTO.getTeacherId(),
                requestDTO.getDayOfWeek(), requestDTO.getPeriodNumber(),
                requestDTO.getStartTime(), requestDTO.getEndTime(), id);

        removeRelations(timetable, timetable.getTeacher(), timetable.getSubject(), timetable.getClassEntity());
        addRelations(timetable, teacher, subject, classEntity);

        timetable = timetableMapper.toEntity(timetable, requestDTO);
        Timetable saved = timeTableRepository.save(timetable);
        return timetableMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public TimetableDTO partialUpdateTimetable(Long id, TimetableRequestDTO requestDTO) {

        Timetable timetable = allRepositoryMethods.getTimetableById(id);

        Long subjectId = timetable.getSubject().getId();
        Long teacherId = timetable.getTeacher().getId();
        Long classId = timetable.getClassEntity().getId();
        DayOfWeek dayOfWeek = timetable.getDayOfWeek();
        int periodNumber = timetable.getPeriodNumber();
        LocalTime startTime = timetable.getStartTime();
        LocalTime endTime = timetable.getEndTime();

        if(requestDTO.getClassId() != null) classId = requestDTO.getClassId();
        if(requestDTO.getSubjectId() != null) subjectId = requestDTO.getSubjectId();
        if(requestDTO.getTeacherId() != null) teacherId = requestDTO.getTeacherId();
        if(requestDTO.getDayOfWeek() != null) dayOfWeek = requestDTO.getDayOfWeek();
        if(requestDTO.getPeriodNumber() != null) periodNumber = requestDTO.getPeriodNumber();
        if(requestDTO.getStartTime() != null) startTime = requestDTO.getStartTime();
        if(requestDTO.getEndTime() != null) endTime = requestDTO.getEndTime();

        checkValidation(subjectId, teacherId, classId);

        if(!teacherRepository.existsByIdAndClassTeacherList_ClassEntity_IdAndClassTeacherList_ClassEntity_ClassSubjects_Subject_Id(teacherId, classId, subjectId))
            throw new BadRequestException("Teacher class subject combination not exists.");

        isTimeSlotAvailable(classId, teacherId, dayOfWeek, periodNumber, startTime, endTime, id);

        removeRelations(timetable, timetable.getTeacher(), timetable.getSubject(), timetable.getClassEntity());

        Subject subject = allRepositoryMethods.getSubjectById(subjectId);
        Teacher teacher = allRepositoryMethods.getTeacherById(teacherId);
        ClassEntity classEntity = allRepositoryMethods.getClassById(classId);

        addRelations(timetable, teacher, subject, classEntity);

        timetable = timetableMapper.partialUpdateEntity(timetable, requestDTO);
        Timetable saved = timeTableRepository.save(timetable);
        return timetableMapper.toDTO(saved);
    }

    @Override
    public List<TimetableDTO> getTimetableByClass(Long classId) {
        return timetableMapper.toDTOList(timeTableRepository.findByClassEntity_Id(classId));
    }

    @Override
    public List<TimetableDTO> getTimetableByTeacher(Long teacherId) {
        return timetableMapper.toDTOList(timeTableRepository.findByTeacher_Id(teacherId));
    }

    @Override
    public List<TimetableDTO> getTimetableByClassAndDay(Long classId, DayOfWeek dayOfWeek) {
        return timetableMapper.toDTOList(
                timeTableRepository.findByClassEntity_IdAndDayOfWeek(classId, dayOfWeek));
    }

    @Override
    public List<TimetableDTO> getTimetableByTeacherAndDay(Long teacherId, DayOfWeek dayOfWeek) {
        return timetableMapper.toDTOList(
                timeTableRepository.findByTeacher_IdAndDayOfWeek(teacherId, dayOfWeek));
    }

    @Override
    public void deleteTimetable(Long id) {
        Timetable timetable = allRepositoryMethods.getTimetableById(id);
        timeTableRepository.delete(timetable);
    }
}