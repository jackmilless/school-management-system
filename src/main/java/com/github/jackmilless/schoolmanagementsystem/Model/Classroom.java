package com.github.jackmilless.schoolmanagementsystem.Model;

// entity class for classroom in database
public class Classroom {
    private int classroomId;
    private String classroomNumber;
    private int capacity;
    
    public Classroom() {}
    public Classroom(int classroomId, String classroomNumber, int capacity) {
        this.classroomId = classroomId;
        this.classroomNumber = classroomNumber;
        this.capacity = capacity;
    }
    public int getClassroomId() {
        return classroomId;
    }
    public void setClassroomId(int classroomId) {
        this.classroomId = classroomId;
    }
    public String getClassroomNumber() {
        return classroomNumber;
    }
    public void setClassroomNumber(String classroomNumber) {
        this.classroomNumber = classroomNumber;
    }
    public int getCapacity() {
        return capacity;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
