package com.github.jackmilless.schoolmanagementsystem.Model;

// wrapper class containing a StudentCourse with a Student
// used to get student information along with grade when associated course is given
public class StudentCourseStudent {
    private final StudentCourse studentCourse;
    private final Student student;

    public StudentCourseStudent(StudentCourse studentCourse, Student student) {
        this.studentCourse = studentCourse;
        this.student = student;
    }
    public StudentCourse getStudentCourse() {
        return studentCourse;
    }
    public Student getStudent() {
        return student;
    }
}
