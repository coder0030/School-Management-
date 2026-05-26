package com.example.demo.Helper;

public enum Role {
    ROLE_USER("User"),
    ROLE_ADMIN("Administrator"),
    ROLE_TEACHER("Teacher"),
    ROLE_STUDENT("Student"),
    ROLE_PARENT("Parents"),
    ROLE_SUPERADMIN("SuperAdmin"),
    ROLE_LIBRARIAN("Librarian"),
    ROLE_ACCOUNTANT("Accountant"),
    ROLE_CLASS_TEACHER("ClassTeacher");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Role getRoles(String role) {
        return switch (role.toUpperCase()) {
            case "USER" -> ROLE_USER;
            case "ADMIN" -> ROLE_ADMIN;
            case "SUPERADMIN" -> ROLE_SUPERADMIN;
            case "TEACHER" -> ROLE_TEACHER;
            case "CLASSTEACHER" -> ROLE_CLASS_TEACHER;
            case "PARENT" -> ROLE_PARENT;
            case "LIBRARIAN" -> ROLE_LIBRARIAN;
            case "ACCOUNTANT" -> ROLE_ACCOUNTANT;
            case "STUDENT" -> ROLE_STUDENT;
            default -> null;
        };
    }
}