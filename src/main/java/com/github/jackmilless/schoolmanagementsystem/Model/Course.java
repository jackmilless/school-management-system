package com.github.jackmilless.schoolmanagementsystem.Model;

// entity class for course in database
public class Course {
    private int courseId;
    private int teacherId;
    private int classroomId;
    private String courseName;
    private String courseCode;
    private String courseHours;
    public String subjectName;
    
    public Course() {}
    public Course(int courseId, int teacherId, int classroomId, String courseName, String courseCode, String courseHours, String subjectName) {
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.classroomId = classroomId;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.courseHours = courseHours;
        this.subjectName = subjectName;
    }
    public int getCourseId() {
        return courseId;
    }
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
    public int getTeacherId() {
        return teacherId;
    }
    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }
    public int getClassroomId() {
        return classroomId;
    }
    public void setClassroomId(int classroomId) {
        this.classroomId = classroomId;
    }
    public String getCourseName() {
        return courseName;
    }
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    public String getCourseCode() {
        return courseCode;
    }
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
    public String getCourseHours() {
        return courseHours;
    }
    public void setCourseHours(String courseHours) {
        this.courseHours = courseHours;
    }
    public String getSubjectName() {
        return subjectName;
    }
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
