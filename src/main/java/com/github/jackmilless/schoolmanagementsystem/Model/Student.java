package com.github.jackmilless.schoolmanagementsystem.Model;

// entity class for student in database
public class Student {
    private int studentId;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private int gradeLevel;
    private Double gpa;
    
    public Student() {}
    public Student(int studentId, String firstName, String lastName, String emailAddress, int gradeLevel, Double gpa) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.gradeLevel = gradeLevel;
        this.gpa = gpa;
    }
    public int getStudentId() {
        return studentId;
    }
    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getEmailAddress() {
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    public int getGradeLevel() {
        return gradeLevel;
    }
    public void setGradeLevel(int gradeLevel) {
        this.gradeLevel = gradeLevel;
    }
    public Double getGpa() {
        return gpa;
    }
    public void setGpa(Double gpa) {
        this.gpa = gpa;
    }
}
