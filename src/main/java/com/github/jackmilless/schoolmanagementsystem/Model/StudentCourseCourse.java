package com.github.jackmilless.schoolmanagementsystem.Model;

// wrapper class containing a StudentCourse with a Course
// used to get course information along with grade when associated student is given
public class StudentCourseCourse {
    private final StudentCourse studentCourse;
    private final Course course;

    public StudentCourseCourse(StudentCourse studentCourse, Course course) {
        this.studentCourse = studentCourse;
        this.course = course;
    }
    public StudentCourse getStudentCourse() {
        return studentCourse;
    }
    public Course getCourse() {
        return course;
    }
}