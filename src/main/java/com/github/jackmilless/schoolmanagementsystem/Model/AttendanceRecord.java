package com.github.jackmilless.schoolmanagementsystem.Model;

// entity class for attendance_record in database
public class AttendanceRecord {
    private int attendanceId;
    private int studentId;
    private int courseId;
    private String attendanceDate;
    private boolean present;
    
    public AttendanceRecord() {}
    public AttendanceRecord(int attendanceId, int studentId, int courseId, String attendanceDate, boolean present) {
        this.attendanceId = attendanceId;
        this.studentId = studentId;
        this.courseId = courseId;
        this.attendanceDate = attendanceDate;
        this.present = present;
    }
    public int getAttendanceId() {
        return attendanceId;
    }
    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }
    public int getStudentId() {
        return studentId;
    }
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }
    public int getCourseId() {
        return courseId;
    }
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
    public String getAttendanceDate() {
        return attendanceDate;
    }
    public void setAttendanceDate(String attendanceDate) {
        this.attendanceDate = attendanceDate;
    }
    public boolean isPresent() {
        return present;
    }
    public void setPresent(boolean present) {
        this.present = present;
    }
}
