package com.example.demo.Security;

import com.example.demo.ENTITY.Librarian;
import com.example.demo.ENTITY.Student;
import com.example.demo.ENTITY.User;
import com.example.demo.Helper.Role;
import com.example.demo.MyException.AccessDeniedException;
import com.example.demo.MyException.BadRequestException;
import com.example.demo.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Component("securityUtil")
@RequiredArgsConstructor
public class SecurityUtil {
    private final AdminRepository adminRepository;
    private final TeacherRepository teacherRepository;
    private final AccountantRepository accountantRepository;
    private final ParentRepository parentRepository;
    private final LibrarianRepository librarianRepository;
    private final StudentRepository studentRepository;

    public static User getCurrentUserDetails() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            throw new AccessDeniedException("No authenticated user found");
        }

       return (User) auth.getPrincipal();
    }

    public static Long getCurrentUserId() {
        return getCurrentUserDetails().getId();
    }

    public static boolean isAdmin() {
        return getCurrentUserDetails().getAuthorities()
                .stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_ADMIN") ||
                                a.getAuthority().equals("ROLE_SUPERADMIN")
                );
    }

    public boolean isCurrAdmin(final Long adminId) {
        Long currUserId = getCurrentUserDetails().getId();

        return adminRepository.findByUser_Id(currUserId)
                .map(admin -> admin.getId().equals(adminId))
                .orElse(false);

    }

    public boolean isCurrStudent(final Long studentId) {
        Long currUserId = getCurrentUserDetails().getId();

        return studentRepository.findByUser_Id(currUserId)
                .map(admin -> admin.getId().equals(studentId))
                .orElse(false);

    }

    public boolean isCurrLibrarian(final Long librarianId) {
        Long currUserId = getCurrentUserDetails().getId();

        return librarianRepository.findByUser_Id(currUserId)
                .map(t -> t.getId().equals(librarianId))
                .orElse(false);

    }

    public boolean isCurrAccountant(final Long accountantId) {
        Long currUserId = getCurrentUserDetails().getId();

        return accountantRepository.findByUser_Id(currUserId)
                .map(t -> t.getId().equals(accountantId))
                .orElse(false);

    }

    public boolean isCurrParent(final Long parentId) {
        Long currUserId = getCurrentUserDetails().getId();

        return parentRepository.findByUser_Id(currUserId)
                .map(t -> t.getId().equals(parentId))
                .orElse(false);

    }

    public boolean isCurrSuperAdmin(final Long superAdminId) {
        Long currUserId = getCurrentUserDetails().getId();

        return adminRepository.findByUser_Id(currUserId)
                .map(t -> t.getId().equals(superAdminId))
                .orElse(false);

    }

    public boolean isCurrTeacher(final Long teacherId) {
        Long currUserId = getCurrentUserDetails().getId();

        return teacherRepository.findByUser_Id(currUserId)
                .map(t -> t.getId().equals(teacherId))
                .orElse(false);
    }

    public boolean isCurrStudentByPhone(final String phone) {
        Long currUserId = getCurrentUserDetails().getId();

        return studentRepository.findByUser_Id(currUserId)
                .map(s -> s.getPhone().equals(phone))
                .orElse(false);
    }

    public boolean isCurrParentOfStudentFee(final Long feeId) {
        Long currUserId = getCurrentUserDetails().getId();

        return studentRepository.findByUser_Id(currUserId)
                .map(s -> s.getStudentFeesList().stream()
                        .anyMatch(fee -> fee.getId().equals(feeId)))
                .orElse(false);
    }

    public boolean isCurrStudentByReceipt(final String receiptId) {

        Long currUserId = getCurrentUserDetails().getId();

        return studentRepository.findByUser_Id(currUserId)
                .map(student -> student.getFeePaymentList().stream()
                        .anyMatch(fs -> fs.getReceiptNumber().equals(receiptId)))
                .orElse(false);
    }

    public boolean isCurrStudentByClassAndRoll(Long classId, int studentRollNo) {

        Long currUserId = getCurrentUserDetails().getId();

        Student student = studentRepository
                .findByClassEntity_IdAndRollNo(classId, studentRollNo);

        return student != null && student.getUser().getId().equals(currUserId);
    }

    public static boolean isOwner(Long userId) {
        return getCurrentUserId().equals(userId);
    }

    public boolean isAdminOrOwner(Long userId) {
        return isAdmin() || isOwner(userId);
    }

    private static final Set<Role> NON_STUDENT_ROLES = EnumSet.of(
            Role.ROLE_ADMIN,
            Role.ROLE_SUPERADMIN,
            Role.ROLE_TEACHER,
            Role.ROLE_LIBRARIAN,
            Role.ROLE_ACCOUNTANT,
            Role.ROLE_CLASS_TEACHER,
            Role.ROLE_PARENT
    );

    public void isAlreadyAssignWithDifferentRoles(User user, Role currRequestRole) {

        if (currRequestRole != Role.ROLE_STUDENT) return;

        boolean hasConflict = user.getRoles().stream()
                .anyMatch(NON_STUDENT_ROLES::contains);

        if (hasConflict) {
            throw new BadRequestException(
                    "User cannot be assigned STUDENT role. Current roles: " + user.getRoles()
            );
        }

    }
}