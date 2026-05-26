package com.example.demo.ServiceImpl;

import com.example.demo.DTO.AttendanceBulkResponseDTO;
import com.example.demo.DTO.AttendanceDTO;
import com.example.demo.ENTITY.*;
import com.example.demo.Helper.AttendanceStatus;
import com.example.demo.Mapper.AttendanceMapper;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.MyException.DataNotFoundException;
import com.example.demo.Repository.*;
import com.example.demo.RequestDTO.AttendanceBulkRequestDTO;
import com.example.demo.RequestDTO.AttendanceRequestDTO;
import com.example.demo.Service.AttendanceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final ClassRepository classRepository;
    private final AttendanceRepository attendanceRepository;
    private final AllRepositoryMethods allRepositoryMethods;
    private final AttendanceMapper attendanceMapper;
    private final StudentRepository studentRepository;
    private final TimetableRepository timetableRepository;
    private final ModelMapper modelMapper;
    public final static int maxPageSize = 30;

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

    private void checkValidRelations(Subject subject, ClassEntity classEntity, Teacher teacher, Student student) {

        if(!student.getClassEntity().getId().equals(classEntity.getId())) {
            throw new BadRequestException(
                    "Student is not assigned to this class: " + classEntity.getId());
        }

        boolean isTeacherAssignedToClass =
                teacher.getClassTeacherList().stream()
                        .anyMatch(c -> c.getClassEntity()
                                .getId().equals(classEntity.getId()));

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

    private Timetable isTeacherScheduledNow(Long teacherId) {

        DayOfWeek currentDay = LocalDate.now().getDayOfWeek();
        LocalTime currentTime = LocalTime.now();

        Timetable timetable = timetableRepository
                .findByTeacher_IdAndDayOfWeekAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                        teacherId, currentDay, currentTime, currentTime);

        if (timetable == null) {
            throw new BadRequestException(
                    "Teacher is not scheduled for this period.");
        }

        return timetable;
    }

    private void addRelations(Attendance attendance, Student student, Timetable timetable) {
        student.addAttendance(attendance);
        timetable.addAttendances(attendance);

        attendance.setStudent(student);
        attendance.setTimetable(timetable);
    }

    private void removeRelations(Attendance attendance) {
        if (attendance.getStudent() != null) {
            attendance.getStudent().removeAttendance(attendance);
        }
        if (attendance.getTimetable() != null) {
            attendance.getTimetable().getAttendances().remove(attendance);
        }
        attendance.setStudent(null);
        attendance.setTimetable(null);
    }

    @Override
    @Transactional
    public AttendanceDTO markAttendance(Long studentId, AttendanceRequestDTO requestDTO) {
        Student student = allRepositoryMethods.getStudentById(studentId);
        checkValidation(requestDTO.getSubjectId(), requestDTO.getTeacherId(), requestDTO.getClassId());

        Subject subject = allRepositoryMethods.getSubjectById(requestDTO.getSubjectId());
        Teacher teacher = allRepositoryMethods.getTeacherById(requestDTO.getTeacherId());
        ClassEntity classEntity = allRepositoryMethods.getClassById(requestDTO.getClassId());

        checkValidRelations(subject, classEntity, teacher, student);
        Timetable timetable = isTeacherScheduledNow(teacher.getId());

        Attendance attendance = new Attendance();
        attendance.setStatus(requestDTO.getStatus() != null ? requestDTO.getStatus() : AttendanceStatus.PRESENT);
        addRelations(attendance, student, timetable);

        Attendance created = attendanceRepository.save(attendance);
        return attendanceMapper.toDTO(created);
    }

    @Override
    public Page<AttendanceDTO> getAllAttendanceRecords(int pageNo, int pageSize) {

        if(pageSize > maxPageSize) {
            pageSize = maxPageSize;
        }

        Sort sort = Sort.by("attendanceDate").descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Attendance> attendancePage = attendanceRepository.findAll(pageable);

        if(attendancePage.getContent().isEmpty()) {
            return Page.empty(pageable);
        }
        return attendancePage.map(attendance ->
                modelMapper.map(attendance, AttendanceDTO.class));
    }

    @Override
    public Page<AttendanceDTO> getAllAttendanceOfStudent(Long studentId, int pageNo, int pageSize) {
        int maxPageSize = 50;

        if (pageSize > maxPageSize) {
            pageSize = maxPageSize;
        }

        Sort sort = Sort.by("attendanceDate").descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Attendance> attendancePage =
                attendanceRepository.findAllByStudentId(studentId, pageable);

        if (attendancePage.getContent().isEmpty()) {
            return Page.empty(pageable);
        }

        return attendancePage.map(attendanceMapper::toDTO);
    }

    @Override
    public AttendanceDTO getStudentAttendanceByDate(Long studentId, LocalDate date) {
        Student student = allRepositoryMethods.getStudentById(studentId);

        Attendance attendance = attendanceRepository
                .findByStudent_IdAndAttendanceDate(studentId, date)
                .orElseThrow(() -> new BadRequestException(
                        "No attendance record found for student ID: " + studentId + " on date: " + date));

        return attendanceMapper.toDTO(attendance);
    }

    @Override
    @Transactional
    public void deleteStudentAttendanceByDate(Long studentId, LocalDate date) {
        Student student = allRepositoryMethods.getStudentById(studentId);

        Attendance attendance = attendanceRepository
                .findByStudent_IdAndAttendanceDate(studentId, date)
                .orElseThrow(() -> new BadRequestException(
                        "No attendance record found for student ID: " + studentId + " on date: " + date));

        removeRelations(attendance);
        attendanceRepository.delete(attendance);
    }

    @Override
    @Transactional
    public void deleteAllAttendanceOfStudent(Long studentId) {
        List<Attendance> attendanceList = attendanceRepository.findAllByStudent_Id(studentId);

        if(attendanceList.isEmpty()) {
            throw new DataNotFoundException("No attendance found.");
        }

        for (Attendance attendance : attendanceList) {
            removeRelations(attendance);
        }
        attendanceRepository.deleteAll(attendanceList);
    }

    @Override
    @Transactional
    public AttendanceDTO updateStudentAttendance(Long studentId, LocalDate date, AttendanceRequestDTO requestDTO) {
        Student student = allRepositoryMethods.getStudentById(studentId);

        Attendance attendance = attendanceRepository
                .findByStudent_IdAndAttendanceDate(studentId, date)
                .orElseThrow(() -> new BadRequestException(
                        "No attendance record found for student ID: " + studentId + " on date: " + date));

        if (requestDTO.getStatus() != null) {
            attendance.setStatus(requestDTO.getStatus());
        }

        Attendance updated = attendanceRepository.save(attendance);
        return attendanceMapper.toDTO(updated);
    }

    @Override
    public Page<AttendanceDTO> getAttendanceByClassAndDate(Long classId, LocalDate date,
            int pageNo, int pageSize) {

        if (!classRepository.existsById(classId)) {
            throw new BadRequestException("Class ID doesn't exist: " + classId);
        }

        int maxPageSize = 50;
        if (pageSize > maxPageSize) {
            pageSize = maxPageSize;
        }

        Sort sort = Sort.by("attendanceDate").descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Attendance> attendancePage =
                attendanceRepository.findByStudent_ClassEntity_IdAndAttendanceDate(classId, date, pageable);

        if (attendancePage.getContent().isEmpty()) {
            return Page.empty(pageable);
        }

        return attendancePage.map(attendanceMapper::toDTO);
    }

    @Override
    public List<AttendanceDTO> getAttendanceByTimetableAndDate(Long timetableId, LocalDate date) {

        List<Attendance> attendanceList = attendanceRepository
                .findByTimetable_IdAndAttendanceDate(timetableId, date);

        if(attendanceList.isEmpty()) {
             return List.of();
         }
        return attendanceMapper.toDTOList(attendanceList);

    }

    @Override
    public AttendanceBulkResponseDTO markBulkAttendance(AttendanceBulkRequestDTO requestDTO) {

        List<Attendance> attendanceList = new ArrayList<>();
        List<Long> skippedStudents = new ArrayList<>();

        checkValidation(requestDTO.getSubjectId(), requestDTO.getTeacherId(), requestDTO.getClassId());

        Subject subject = allRepositoryMethods.getSubjectById(requestDTO.getSubjectId());
        Teacher teacher = allRepositoryMethods.getTeacherById(requestDTO.getTeacherId());
        ClassEntity classEntity = allRepositoryMethods.getClassById(requestDTO.getClassId());
        Timetable timetable = isTeacherScheduledNow(teacher.getId());

        for (Long studentId : requestDTO.getStudentIds()) {

            try {
                Student student = allRepositoryMethods.getStudentById(studentId);

                checkValidRelations(subject, classEntity, teacher, student);

                boolean alreadyMarked =
                        attendanceRepository.existsByStudent_IdAndAttendanceDateAndTimetable_Id(
                                studentId, LocalDate.now(), timetable.getId());

                if (alreadyMarked) {
                    skippedStudents.add(studentId);
                    continue;
                }

                Attendance attendance = new Attendance();
                attendance.setStatus(AttendanceStatus.valueOf(requestDTO.getStatus()));
                addRelations(attendance, student, timetable);

                attendanceList.add(attendance);

            } catch (Exception ex) {
                skippedStudents.add(studentId);
            }
        }

        attendanceRepository.saveAll(attendanceList);

        return AttendanceBulkResponseDTO.builder()
                .message("Student attendance list saved.")
                .savedCount(attendanceList.size())
                .skippedStudentIds(skippedStudents)
                .build();
    }
}