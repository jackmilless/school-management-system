package com.github.jackmilless.schoolmanagementsystem.Service;

import com.github.jackmilless.schoolmanagementsystem.DAO.SchoolSystemDAO;
import com.github.jackmilless.schoolmanagementsystem.Model.*;

import java.sql.SQLException;
import java.util.List;

/*
 * SchoolSystemService:
 * provides business logic for application
 * mostly hands off methods directly to schoolSystemDAO
 * sends SQLExceptions up to the Controller 
 */
public class SchoolSystemService {
    private SchoolSystemDAO schoolSystemDAO;

    public SchoolSystemService(SchoolSystemDAO schoolSystemDAO) {
        this.schoolSystemDAO = schoolSystemDAO;
    }

    /*
     * add methods: call insert method to insert record, throwing SQLException on failure
     * then use returned id to call get method to return persisted record
     */

    public Student addStudent(Student student) throws SQLException {
        int studentId = schoolSystemDAO.insertStudent(student);
        return schoolSystemDAO.getStudentByStudentId(studentId);
    }

    public Teacher addTeacher(Teacher teacher) throws SQLException {
        int teacherId = schoolSystemDAO.insertTeacher(teacher);
        return schoolSystemDAO.getTeacherByTeacherId(teacherId);
    }

    public Classroom addClassroom(Classroom classroom) throws SQLException {
        int classroomId = schoolSystemDAO.insertClassroom(classroom);
        return schoolSystemDAO.getClassroomByClassroomId(classroomId);
    }

    public Course addCourse(Course course) throws SQLException {
        int courseId = schoolSystemDAO.insertCourse(course);
        return schoolSystemDAO.getCourseByCourseId(courseId);
    }

    public StudentCourse addStudentCourse(StudentCourse studentCourse) throws SQLException {
        schoolSystemDAO.insertStudentCourse(studentCourse);
        return studentCourse;
    }

    public AttendanceRecord addAttendanceRecord(AttendanceRecord attendanceRecord) throws SQLException {
        int attendanceId = schoolSystemDAO.insertAttendanceRecord(attendanceRecord);
        return schoolSystemDAO.getAttendanceRecordByAttendanceId(attendanceId);
    }

    /*
     * get methods: call get methods to return requested records
     */

    public List<Student> getStudentByFullName(String firstName, String lastName) {
        return schoolSystemDAO.getStudentByFullName(firstName, lastName);
    }

    public List<Student> getStudentByEmail(String emailAddress) {
        return schoolSystemDAO.getStudentByEmail(emailAddress);
    }

    public List<Student> getStudentsByGradeLevel(int gradeLevel, int limit, int offset, String ascendingParam, boolean ascending) {
        return schoolSystemDAO.getStudentsByGradeLevel(gradeLevel, limit, offset, ascendingParam, ascending);
    }

    public List<Student> getStudentsByGpa(double minGpa, double maxGpa, int limit, int offset, String ascendingParam, boolean ascending) {
        return schoolSystemDAO.getStudentsByGpa(minGpa, maxGpa, limit, offset, ascendingParam, ascending);
    }

    public List<Student> getAllStudents(int limit, int offset, String ascendingParam, boolean ascending) {
        return schoolSystemDAO.getAllStudents(limit, offset, ascendingParam, ascending);
    }

    public List<Teacher> getTeacherByFullName(String firstName, String lastName) {
        return schoolSystemDAO.getTeacherByFullName(firstName, lastName);
    }

    public List<Teacher> getTeacherByEmail(String emailAddress) {
        return schoolSystemDAO.getTeacherByEmail(emailAddress);
    }

    public List<Teacher> getAllTeachers(int limit, int offset, String ascendingParam, boolean ascending) {
        return schoolSystemDAO.getAllTeachers(limit, offset, ascendingParam, ascending);
    }

    public List<Classroom> getClassroomByNumber(String classroomNumber) {
        return schoolSystemDAO.getClassroomByNumber(classroomNumber);
    }

    public List<Classroom> getClassroomsByCapacity(int minCapacity, int maxCapacity, int limit, int offset,
            String ascendingParam, boolean ascending) {
        return schoolSystemDAO.getClassroomsByCapacity(minCapacity, maxCapacity, limit, offset, ascendingParam, ascending);
    }

    public List<Classroom> getAllClassrooms(int limit, int offset, String ascendingParam, boolean ascending) {
        return schoolSystemDAO.getAllClassrooms(limit, offset, ascendingParam, ascending);
    }

    public List<Course> getCourseByName(String courseName) {
        return schoolSystemDAO.getCourseByName(courseName);
    }

    public List<Course> getCourseByCode(String courseCode) {
        return schoolSystemDAO.getCourseByCode(courseCode);
    }

    public List<Course> getCoursesByHours(String courseHours, int limit, int offset, String ascendingParam,
            boolean ascending) {
        return schoolSystemDAO.getCoursesByHours(courseHours, limit, offset, ascendingParam, ascending);
    }

    public List<Course> getCoursesBySubject(String subjectName, int limit, int offset, String ascendingParam,
            boolean ascending) {
        return schoolSystemDAO.getCoursesBySubject(subjectName, limit, offset, ascendingParam, ascending);
    }

    public List<Course> getAllCourses(int limit, int offset, String ascendingParam, boolean ascending) {
        return schoolSystemDAO.getAllCourses(limit, offset, ascendingParam, ascending);
    }

    public Student getStudentByStudentId(int studentId) {
        return schoolSystemDAO.getStudentByStudentId(studentId);
    }

    public Teacher getTeacherByTeacherId(int teacherId) {
        return schoolSystemDAO.getTeacherByTeacherId(teacherId);
    }

    public Classroom getClassroomByClassroomId(int classroomId) {
        return schoolSystemDAO.getClassroomByClassroomId(classroomId);
    }

    public Course getCourseByCourseId(int courseId) {
        return schoolSystemDAO.getCourseByCourseId(courseId);
    }

    public List<Course> getCoursesByTeacherId(int teacherId) {
        return schoolSystemDAO.getCoursesByTeacherId(teacherId);
    }

    public List<Course> getCoursesByClassroomId(int classroomId) {
        return schoolSystemDAO.getCoursesByClassroomId(classroomId);
    }

    public List<StudentCourseCourse> getCoursesByStudentId(int studentId, int limit, int offset, String ascendingParam, boolean ascending) {
        return schoolSystemDAO.getCoursesByStudentId(studentId, limit, offset, ascendingParam, ascending);
    }

    public List<StudentCourseStudent> getStudentsByCourseId(int courseId, int limit, int offset, String ascendingParam, boolean ascending) {
        return schoolSystemDAO.getStudentsByCourseId(courseId, limit, offset, ascendingParam, ascending);
    }

    public List<StudentCourseStudent> getStudentsByCourseIdAndGrade(int courseId, char mingrade, char maxgrade, int limit, int offset, String ascendingParam, boolean ascending) {
        return schoolSystemDAO.getStudentsByCourseIdAndGrade(courseId, mingrade, maxgrade, limit, offset, ascendingParam, ascending);
    }

    public StudentCourse getStudentCourse(int studentId, int courseId) {
        return schoolSystemDAO.getStudentCourse(studentId, courseId);
    }

    public List<AttendanceRecord> getAttendanceRecords(int studentId, int courseId, int limit, int offset, String ascendingParam, boolean ascending) {
        return schoolSystemDAO.getAttendanceRecords(studentId, courseId, limit, offset, ascendingParam, ascending);
    }

    public List<AttendanceRecord> getAttendanceRecordsByDate(int studentId, int courseId, String minDate, String maxDate, int limit, int offset, String ascendingParam, boolean ascending) {
        return schoolSystemDAO.getAttendanceRecordsByDate(studentId, courseId, minDate, maxDate, limit, offset, ascendingParam, ascending);
    }

    public AttendanceRecord getAttendanceRecordByAttendanceId(int attendanceId) {
        return schoolSystemDAO.getAttendanceRecordByAttendanceId(attendanceId);
    }

    /*
     * update methods: update record on database, throwing SQLException on failure
     * then return updated record by calling get method
     */

    public Student updateStudentByStudentId(Student student) throws SQLException {
        schoolSystemDAO.updateStudent(student);
        return schoolSystemDAO.getStudentByStudentId(student.getStudentId());
    }

    public Teacher updateTeacherByTeacherId(Teacher teacher) throws SQLException {
        schoolSystemDAO.updateTeacher(teacher);
        return schoolSystemDAO.getTeacherByTeacherId(teacher.getTeacherId());
    }

    public Classroom updateClassroomByClassroomId(Classroom classroom) throws SQLException {
        schoolSystemDAO.updateClassroom(classroom);
        return schoolSystemDAO.getClassroomByClassroomId(classroom.getClassroomId());
    }

    public Course updateCourseByCourseId(Course course) throws SQLException {
        schoolSystemDAO.updateCourse(course);
        return schoolSystemDAO.getCourseByCourseId(course.getCourseId());
    }

    public AttendanceRecord updateAttendanceRecord(AttendanceRecord attendanceRecord) throws SQLException {
        schoolSystemDAO.updateAttendanceRecord(attendanceRecord);
        return schoolSystemDAO.getAttendanceRecordByAttendanceId(attendanceRecord.getAttendanceId());
    }

    public StudentCourse updateStudentCourse(StudentCourse studentCourse) throws SQLException {
        schoolSystemDAO.updateStudentCourse(studentCourse);
        return schoolSystemDAO.getStudentCourse(studentCourse.getStudentId(), studentCourse.getCourseId());
    }

    /*
     * delete record from database by calling delete method
     * first get record to be deleted, then return it if record was found
     */

    public Student deleteStudentByStudentId(int studentId) {
        Student student = schoolSystemDAO.getStudentByStudentId(studentId);
        if(schoolSystemDAO.deleteStudentByStudentId(studentId)) {
            return student;
        } else {
            return null;
        }
    }

    public Teacher deleteTeacherByTeacherId(int teacherId) {
        Teacher teacher = schoolSystemDAO.getTeacherByTeacherId(teacherId);
        if(schoolSystemDAO.deleteTeacherByTeacherId(teacherId)) {
            return teacher;
        } else {
            return null;
        }
    }

    public Classroom deleteClassroomByClassroomId(int classroomId) {
        Classroom classroom = schoolSystemDAO.getClassroomByClassroomId(classroomId);
        if(schoolSystemDAO.deleteClassroomByClassroomId(classroomId)) {
            return classroom;
        } else {
            return null;
        }
    }

    /*
     * throw SQLException on error (course found but not allowed to be deleted)
     */
    public Course deleteCourseByCourseId(int courseId) throws SQLException {
        Course course = schoolSystemDAO.getCourseByCourseId(courseId);
        if(schoolSystemDAO.deleteCourseByCourseId(courseId)) {
            return course;
        } else {
            return null;
        }
    }
}
