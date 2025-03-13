package com.github.jackmilless.schoolmanagementsystem.Model;

// entity class for student_course in database
public class StudentCourse {
    private int studentId;
    private int courseId;
    private Character grade;
    private Double gradePercentage;
    private int courseHour;
    
    public StudentCourse() {}
    public StudentCourse(int studentId, int courseId, Character grade, Double gradePercentage, int courseHour) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.grade = grade;
        this.gradePercentage = gradePercentage;
        this.courseHour = courseHour;
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
    public Character getGrade() {
        return grade;
    }
    public void setGrade(Character grade) {
        this.grade = grade;
    }
    public Double getGradePercentage() {
        return gradePercentage;
    }
    public void setGradePercentage(Double gradePercentage) {
        this.gradePercentage = gradePercentage;
    }
    public int getCourseHour() {
        return courseHour;
    }
    public void setCourseHour(int courseHour) {
        this.courseHour = courseHour;
    }
}