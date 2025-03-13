package com.github.jackmilless.schoolmanagementsystem.DAO;

import com.github.jackmilless.schoolmanagementsystem.Util.ConnectionUtil;
import com.github.jackmilless.schoolmanagementsystem.Model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import java.util.List;
import java.util.ArrayList;

/*
 * SchoolSystemDAO:
 * provides methods that interact directly with the database 
 * generates and executes SQL statements to the database
 * returns obtained records or success information to the SchoolSystemService instance
 * generates and sends SQLExceptions to SchoolSystemService when database error messages are encountered
 */
public class SchoolSystemDAO {
    
    /*
     * helper function that validates orderBy with a whitelist to protect against SQL injection
     */ 
    public boolean validateOrderBy(String orderBy) {
        for(char c : orderBy.toCharArray()) {
            if(!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '_' || c == '.')) {
                return false;
            }
        }
        return true;
    }

    /*
     * INSERT INTO methods:
     * insert records into appropriate database tables
     * return id of inserted record on success
     * throw SQLException on failure
     */

    public int insertStudent(Student student) throws SQLException {
        Connection conn = ConnectionUtil.getConnection();
        String sql = "INSERT INTO school.student(first_name, last_name, email_address, grade_level, gpa) VALUES(?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, student.getFirstName());
        ps.setString(2, student.getLastName());
        ps.setString(3, student.getEmailAddress());
        ps.setInt(4, student.getGradeLevel());
        if(student.getGpa() != null) {
            ps.setDouble(5, student.getGpa());
        } else {
            ps.setNull(5, Types.NULL);
        }

        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        rs.next();
        return rs.getInt("student_id");
    }

    public int insertTeacher(Teacher teacher) throws SQLException {
        Connection conn = ConnectionUtil.getConnection();
        String sql = "INSERT INTO school.teacher(first_name, last_name, email_address, phone_number, salary, hire_date) VALUES(?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, teacher.getFirstName());
        ps.setString(2, teacher.getLastName());
        ps.setString(3, teacher.getEmailAddress());
        ps.setString(4, teacher.getPhoneNumber());
        ps.setDouble(5, teacher.getSalary());
        ps.setString(6, teacher.getHireDate());

        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        rs.next();
        return rs.getInt("teacher_id");
    }

    public int insertClassroom(Classroom classroom) throws SQLException {
        Connection conn = ConnectionUtil.getConnection();
        String sql = "INSERT INTO school.classroom(classroom_number, capacity) VALUES(?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, classroom.getClassroomNumber());
        ps.setInt(2, classroom.getCapacity());

        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();

        rs.next();
        return rs.getInt("classroom_id");
    }

    public int insertCourse(Course course) throws SQLException {
        Connection conn = ConnectionUtil.getConnection();
        String sql = "INSERT INTO school.course(teacher_id, classroom_id, course_name, course_code, course_hours, subject_name) VALUES(?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        ps.setInt(1, course.getTeacherId());
        ps.setInt(2, course.getClassroomId());
        ps.setString(3, course.getCourseName());
        ps.setString(4, course.getCourseCode());
        ps.setString(5, course.getCourseHours());
        ps.setString(6, course.getSubjectName());

        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();

        rs.next();
        return rs.getInt("course_id");
    }

    public void insertStudentCourse(StudentCourse studentCourse) throws SQLException {
        Connection conn = ConnectionUtil.getConnection();
        String sql = "INSERT INTO school.student_course(student_id, course_id, grade, grade_percentage, course_hour) VALUES(?, ?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setInt(1, studentCourse.getStudentId());
        ps.setInt(2, studentCourse.getCourseId());
        String grade = null;
        if(studentCourse.getGrade() != null) {
            grade = String.valueOf(studentCourse.getGrade());
        }
        ps.setString(3, grade);
        if(studentCourse.getGradePercentage() != null) {
            ps.setDouble(4, studentCourse.getGradePercentage());
        } else {
            ps.setNull(4, Types.NULL);
        }
        ps.setInt(5, studentCourse.getCourseHour());
        ps.executeUpdate();
    }

    public int insertAttendanceRecord(AttendanceRecord attendanceRecord) throws SQLException {
        Connection conn = ConnectionUtil.getConnection();
        String sql = "INSERT INTO school.attendance_record(student_id, course_id, attendance_date, present) VALUES(?, ?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        ps.setInt(1, attendanceRecord.getStudentId());
        ps.setInt(2, attendanceRecord.getCourseId());
        ps.setString(3, attendanceRecord.getAttendanceDate());
        ps.setBoolean(4, attendanceRecord.isPresent());

        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();

        rs.next();
        return rs.getInt("attendance_id");
    }

    /*
     * SELECT methods:
     * get and return requested records from database
     */

    public List<Student> getAllStudents(int limit, int offset, String orderBy, boolean ascending) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            if(!validateOrderBy(orderBy)) {
                return null;
            }
            String asc = ascending ? "ASC" : "DESC";

            String sql = "SELECT * FROM school.student ORDER BY " + orderBy + " " + asc + " LIMIT ? OFFSET ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, limit);
            ps.setInt(2, offset);

            ResultSet rs = ps.executeQuery();
            List<Student> students = new ArrayList<>();
            
            while(rs.next()) {
                Double gpa = null;
                if(rs.getString("gpa") != null) {
                    gpa = rs.getDouble("gpa");
                }
                students.add(new Student(
                    rs.getInt("student_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email_address"),
                    rs.getInt("grade_level"),
                    gpa
                ));
            }

            return students;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Student getStudentByStudentId(int studentId) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            String sql = "SELECT * FROM school.student WHERE student_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);

            ResultSet rs = ps.executeQuery();
            Student student = null;
            
            if(rs.next()) {
                Double gpa = null;
                if(rs.getString("gpa") != null) {
                    gpa = rs.getDouble("gpa");
                }
                student = new Student(
                    rs.getInt("student_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email_address"),
                    rs.getInt("grade_level"),
                    gpa
                );
            }

            return student;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Student> getStudentByFullName(String firstName, String lastName) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            String sql = "SELECT * FROM school.student WHERE first_name=? AND last_name=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, firstName);
            ps.setString(2, lastName);

            ResultSet rs = ps.executeQuery();
            List<Student> students = new ArrayList<>();
            
            while(rs.next()) {
                Double gpa = null;
                if(rs.getString("gpa") != null) {
                    gpa = rs.getDouble("gpa");
                }
                students.add(new Student(
                    rs.getInt("student_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email_address"),
                    rs.getInt("grade_level"),
                    gpa
                ));
            }

            return students;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Student> getStudentByEmail(String emailAddress) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            String sql = "SELECT * FROM school.student WHERE email_address=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, emailAddress);

            ResultSet rs = ps.executeQuery();
            List<Student> students = new ArrayList<>();
            
            while(rs.next()) {
                Double gpa = null;
                if(rs.getString("gpa") != null) {
                    gpa = rs.getDouble("gpa");
                }
                students.add(new Student(
                    rs.getInt("student_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email_address"),
                    rs.getInt("grade_level"),
                    gpa
                ));
            }

            return students;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Student> getStudentsByGradeLevel(int gradeLevel, int limit, int offset, String orderBy, boolean ascending) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            if(!validateOrderBy(orderBy)) {
                return null;
            }
            String asc = ascending ? "ASC" : "DESC";

            String sql = "SELECT * FROM school.student WHERE grade_level=? ORDER BY " + orderBy + " " + asc + " LIMIT ? OFFSET ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, gradeLevel);
            ps.setInt(2, limit);
            ps.setInt(3, offset);

            ResultSet rs = ps.executeQuery();
            List<Student> students = new ArrayList<>();
            
            while(rs.next()) {
                Double gpa = null;
                if(rs.getString("gpa") != null) {
                    gpa = rs.getDouble("gpa");
                }
                students.add(new Student(
                    rs.getInt("student_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email_address"),
                    rs.getInt("grade_level"),
                    gpa
                ));
            }

            return students;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Student> getStudentsByGpa(double minGpa, double maxGpa, int limit, int offset, String orderBy, boolean ascending) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            if(!validateOrderBy(orderBy)) {
                return null;
            }
            String asc = ascending ? "ASC" : "DESC";

            String sql = "SELECT * FROM school.student WHERE gpa BETWEEN ? AND ? ORDER BY " + orderBy + " " + asc + " LIMIT ? OFFSET ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, minGpa);
            ps.setDouble(2, maxGpa);
            ps.setInt(3, limit);
            ps.setInt(4, offset);

            ResultSet rs = ps.executeQuery();
            List<Student> students = new ArrayList<>();
            
            while(rs.next()) {
                Double gpa = null;
                if(rs.getString("gpa") != null) {
                    gpa = rs.getDouble("gpa");
                }
                students.add(new Student(
                    rs.getInt("student_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email_address"),
                    rs.getInt("grade_level"),
                    gpa
                ));
            }

            return students;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Teacher> getAllTeachers(int limit, int offset, String orderBy, boolean ascending) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            if(!validateOrderBy(orderBy)) {
                return null;
            }
            String asc = ascending ? "ASC" : "DESC";

            String sql = "SELECT * FROM school.teacher ORDER BY " + orderBy + " " + asc + " LIMIT ? OFFSET ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            ResultSet rs = ps.executeQuery();
            List<Teacher> teachers = new ArrayList<>();
            
            while(rs.next()) {
                teachers.add(new Teacher(
                    rs.getInt("teacher_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email_address"),
                    rs.getString("phone_number"),
                    rs.getDouble("salary"),
                    rs.getString("hire_date")
                ));
            }

            return teachers;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Teacher getTeacherByTeacherId(int teacherId) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            String sql = "SELECT * FROM school.teacher WHERE teacher_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, teacherId);

            ResultSet rs = ps.executeQuery();
            Teacher teacher = null;
            
            if(rs.next()) {
                teacher = new Teacher(
                    rs.getInt("teacher_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email_address"),
                    rs.getString("phone_number"),
                    rs.getDouble("salary"),
                    rs.getString("hire_date")
                );
            }

            return teacher;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Teacher> getTeacherByFullName(String firstName, String lastName) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            String sql = "SELECT * FROM school.teacher WHERE first_name=? AND last_name=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, firstName);
            ps.setString(2, lastName);

            ResultSet rs = ps.executeQuery();
            List<Teacher> teachers = new ArrayList<>();
            
            while(rs.next()) {
                teachers.add(new Teacher(
                    rs.getInt("teacher_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email_address"),
                    rs.getString("phone_number"),
                    rs.getDouble("salary"),
                    rs.getString("hire_date")
                ));
            }

            return teachers;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Teacher> getTeacherByEmail(String emailAddress) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            String sql = "SELECT * FROM school.teacher WHERE email_address=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, emailAddress);

            ResultSet rs = ps.executeQuery();
            List<Teacher> teachers = new ArrayList<>();
            
            while(rs.next()) {
                teachers.add(new Teacher(
                    rs.getInt("teacher_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email_address"),
                    rs.getString("phone_number"),
                    rs.getDouble("salary"),
                    rs.getString("hire_date")
                ));
            }

            return teachers;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Classroom> getAllClassrooms(int limit, int offset, String orderBy, boolean ascending) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            if(!validateOrderBy(orderBy)) {
                return null;
            }
            String asc = ascending ? "ASC" : "DESC";

            String sql = "SELECT * FROM school.classroom ORDER BY " + orderBy + " " + asc + " LIMIT ? OFFSET ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            ResultSet rs = ps.executeQuery();
            List<Classroom> classrooms = new ArrayList<>();
            
            while(rs.next()) {
                classrooms.add(new Classroom(
                    rs.getInt("classroom_id"),
                    rs.getString("classroom_number"),
                    rs.getInt("capacity")
                ));
            }

            return classrooms;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Classroom getClassroomByClassroomId(int classroomId) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            String sql = "SELECT * FROM school.classroom WHERE classroom_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, classroomId);

            ResultSet rs = ps.executeQuery();
            Classroom classroom = null;
            
            if(rs.next()) {
                classroom = new Classroom(
                    rs.getInt("classroom_id"),
                    rs.getString("classroom_number"),
                    rs.getInt("capacity")
                );
            }

            return classroom;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Classroom> getClassroomByNumber(String classroomNumber) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            String sql = "SELECT * FROM school.classroom WHERE classroom_number=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, classroomNumber);

            ResultSet rs = ps.executeQuery();
            List<Classroom> classrooms = new ArrayList<>();
            
            while(rs.next()) {
                classrooms.add(new Classroom(
                    rs.getInt("classroom_id"),
                    rs.getString("classroom_number"),
                    rs.getInt("capacity")
                ));
            }

            return classrooms;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Classroom> getClassroomsByCapacity(int minCapacity, int maxCapacity, int limit, int offset, String orderBy, boolean ascending) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            if(!validateOrderBy(orderBy)) {
                return null;
            }
            String asc = ascending ? "ASC" : "DESC";

            String sql = "SELECT * FROM school.classroom WHERE capacity BETWEEN ? AND ? ORDER BY " + orderBy + " " + asc + " LIMIT ? OFFSET ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, minCapacity);
            ps.setInt(2, maxCapacity);
            ps.setInt(3, limit);
            ps.setInt(4, offset);

            ResultSet rs = ps.executeQuery();
            List<Classroom> classrooms = new ArrayList<>();
            
            while(rs.next()) {
                classrooms.add(new Classroom(
                    rs.getInt("classroom_id"),
                    rs.getString("classroom_number"),
                    rs.getInt("capacity")
                ));
            }

            return classrooms;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Course> getAllCourses(int limit, int offset, String orderBy, boolean ascending) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            if(!validateOrderBy(orderBy)) {
                return null;
            }
            String asc = ascending ? "ASC" : "DESC";

            String sql = "SELECT * FROM school.course ORDER BY " + orderBy + " " + asc + " LIMIT ? OFFSET ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            ResultSet rs = ps.executeQuery();
            List<Course> courses = new ArrayList<>();
            
            while(rs.next()) {
                courses.add(new Course(
                    rs.getInt("course_id"),
                    rs.getInt("teacher_id"),
                    rs.getInt("classroom_id"),
                    rs.getString("course_name"),
                    rs.getString("course_code"),
                    rs.getString("course_hours"),
                    rs.getString("subject_name")
                ));
            }

            return courses;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Course getCourseByCourseId(int courseId) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            String sql = "SELECT * FROM school.course WHERE course_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, courseId);

            ResultSet rs = ps.executeQuery();
            Course course = null;
            
            if(rs.next()) {
                course = new Course(
                    rs.getInt("course_id"),
                    rs.getInt("teacher_id"),
                    rs.getInt("classroom_id"),
                    rs.getString("course_name"),
                    rs.getString("course_code"),
                    rs.getString("course_hours"),
                    rs.getString("subject_name")
                );
            }

            return course;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Course> getCourseByName(String courseName) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            String sql = "SELECT * FROM school.course WHERE course_name=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, courseName);

            ResultSet rs = ps.executeQuery();
            List<Course> courses = new ArrayList<>();
            
            while(rs.next()) {
                courses.add(new Course(
                    rs.getInt("course_id"),
                    rs.getInt("teacher_id"),
                    rs.getInt("classroom_id"),
                    rs.getString("course_name"),
                    rs.getString("course_code"),
                    rs.getString("course_hours"),
                    rs.getString("subject_name")
                ));
            }

            return courses;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Course> getCourseByCode(String courseCode) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            String sql = "SELECT * FROM school.course WHERE course_code=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, courseCode);

            ResultSet rs = ps.executeQuery();
            List<Course> courses = new ArrayList<>();
            
            while(rs.next()) {
                courses.add(new Course(
                    rs.getInt("course_id"),
                    rs.getInt("teacher_id"),
                    rs.getInt("classroom_id"),
                    rs.getString("course_name"),
                    rs.getString("course_code"),
                    rs.getString("course_hours"),
                    rs.getString("subject_name")
                ));
            }

            return courses;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Course> getCoursesByHours(String courseHours, int limit, int offset, String orderBy, boolean ascending) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            if(!validateOrderBy(orderBy)) {
                return null;
            }
            String asc = ascending ? "ASC" : "DESC";

            String sql = "SELECT * FROM school.course WHERE course_hours LIKE ? ORDER BY " + orderBy + " " + asc + " LIMIT ? OFFSET ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            String hoursEncoding = "";
            for(int i = 0; i < courseHours.length(); i++) {
                hoursEncoding += courseHours.charAt(i) == '1' ? '1' : '_';
            }
            ps.setString(1, hoursEncoding);
            ps.setInt(2, limit);
            ps.setInt(3, offset);

            ResultSet rs = ps.executeQuery();
            List<Course> courses = new ArrayList<>();
            
            while(rs.next()) {
                courses.add(new Course(
                    rs.getInt("course_id"),
                    rs.getInt("teacher_id"),
                    rs.getInt("classroom_id"),
                    rs.getString("course_name"),
                    rs.getString("course_code"),
                    rs.getString("course_hours"),
                    rs.getString("subject_name")
                ));
            }

            return courses;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Course> getCoursesBySubject(String subjectName, int limit, int offset, String orderBy, boolean ascending) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            if(!validateOrderBy(orderBy)) {
                return null;
            }
            String asc = ascending ? "ASC" : "DESC";

            String sql = "SELECT * FROM school.course WHERE subject_name=? ORDER BY " + orderBy + " " + asc + " LIMIT ? OFFSET ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, subjectName);
            ps.setInt(2, limit);
            ps.setInt(3, offset);

            ResultSet rs = ps.executeQuery();
            List<Course> courses = new ArrayList<>();
            
            while(rs.next()) {
                courses.add(new Course(
                    rs.getInt("course_id"),
                    rs.getInt("teacher_id"),
                    rs.getInt("classroom_id"),
                    rs.getString("course_name"),
                    rs.getString("course_code"),
                    rs.getString("course_hours"),
                    rs.getString("subject_name")
                ));
            }

            return courses;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Course> getCoursesByTeacherId(int teacherId) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            String sql = "SELECT * FROM school.course WHERE teacher_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, teacherId);

            ResultSet rs = ps.executeQuery();
            List<Course> courses = new ArrayList<>();
            
            while(rs.next()) {
                courses.add(new Course(
                    rs.getInt("course_id"),
                    rs.getInt("teacher_id"),
                    rs.getInt("classroom_id"),
                    rs.getString("course_name"),
                    rs.getString("course_code"),
                    rs.getString("course_hours"),
                    rs.getString("subject_name")
                ));
            }

            return courses;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Course> getCoursesByClassroomId(int classroomId) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            String sql = "SELECT * FROM school.course WHERE classroom_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, classroomId);

            ResultSet rs = ps.executeQuery();
            List<Course> courses = new ArrayList<>();
            
            while(rs.next()) {
                courses.add(new Course(
                    rs.getInt("course_id"),
                    rs.getInt("teacher_id"),
                    rs.getInt("classroom_id"),
                    rs.getString("course_name"),
                    rs.getString("course_code"),
                    rs.getString("course_hours"),
                    rs.getString("subject_name")
                ));
            }

            return courses;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<StudentCourseCourse> getCoursesByStudentId(int studentId, int limit, int offset, String orderBy, boolean ascending) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            if(!validateOrderBy(orderBy)) {
                return null;
            }
            String asc = ascending ? "ASC" : "DESC";

            String sql = "SELECT * FROM school.student_course sc " + 
                         "JOIN school.course c ON sc.course_id = c.course_id " +
                         "WHERE sc.student_id=? ORDER BY " + orderBy + " " + asc + " LIMIT ? OFFSET ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, studentId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);

            ResultSet rs = ps.executeQuery();
            List<StudentCourseCourse> sccs = new ArrayList<>();
            Course course;
            StudentCourse studentCourse;
            
            while(rs.next()) {
                course = new Course(
                    rs.getInt("course_id"),
                    rs.getInt("teacher_id"),
                    rs.getInt("classroom_id"),
                    rs.getString("course_name"),
                    rs.getString("course_code"),
                    rs.getString("course_hours"),
                    rs.getString("subject_name")
                );
                Character grade = null;
                if(rs.getString("grade") != null) {
                    grade = rs.getString("grade").charAt(0);
                }
                Double gradePercentage = null;
                if(rs.getString("grade_percentage") != null) {
                    gradePercentage = rs.getDouble("grade_percentage");
                }
                studentCourse = new StudentCourse(
                    rs.getInt("student_id"),
                    rs.getInt("course_id"),
                    grade,
                    gradePercentage,
                    rs.getInt("course_hour")
                );
                sccs.add(new StudentCourseCourse(studentCourse, course));
            }

            return sccs;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<StudentCourseStudent> getStudentsByCourseId(int courseId, int limit, int offset, String orderBy, boolean ascending) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            if(!validateOrderBy(orderBy)) {
                return null;
            }
            String asc = ascending ? "ASC" : "DESC";

            String sql = "SELECT * FROM school.student_course sc " +
                         "JOIN school.student s ON sc.student_id = s.student_id " +
                         "WHERE sc.course_id=? ORDER BY " + orderBy + " " + asc + " LIMIT ? OFFSET ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, courseId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);

            ResultSet rs = ps.executeQuery();
            List<StudentCourseStudent> scss = new ArrayList<>();
            Student student;
            StudentCourse studentCourse;
            
            while(rs.next()) {
                Double gpa = null;
                if(rs.getString("gpa") != null) {
                    gpa = rs.getDouble("gpa");
                }
                student = new Student(
                    rs.getInt("student_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email_address"),
                    rs.getInt("grade_level"),
                    gpa
                );
                Character grade = null;
                if(rs.getString("grade") != null) {
                    grade = rs.getString("grade").charAt(0);
                }
                Double gradePercentage = null;
                if(rs.getString("grade_percentage") != null) {
                    gradePercentage = rs.getDouble("grade_percentage");
                }
                studentCourse = new StudentCourse(
                    rs.getInt("student_id"),
                    rs.getInt("course_id"),
                    grade,
                    gradePercentage,
                    rs.getInt("course_hour")
                );
                scss.add(new StudentCourseStudent(studentCourse, student));
            }

            return scss;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<StudentCourseStudent> getStudentsByCourseIdAndGrade(int courseId, char mingrade, char maxgrade, int limit, int offset, String orderBy, boolean ascending) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            if(!validateOrderBy(orderBy)) {
                return null;
            }
            String asc = ascending ? "ASC" : "DESC";
            String sql = "SELECT * FROM school.student_course sc " +
                         "JOIN school.student s ON sc.student_id = s.student_id " +
                         "WHERE sc.course_id=? AND sc.grade BETWEEN ? AND ? ORDER BY " + orderBy + " " + asc + " LIMIT ? OFFSET ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, courseId);
            ps.setString(2, String.valueOf(maxgrade));
            ps.setString(3, String.valueOf(mingrade));
            ps.setInt(4, limit);
            ps.setInt(5, offset);

            ResultSet rs = ps.executeQuery();
            List<StudentCourseStudent> scss = new ArrayList<>();
            Student student;
            StudentCourse studentCourse;
            
            while(rs.next()) {
                Double gpa = null;
                if(rs.getString("gpa") != null) {
                    gpa = rs.getDouble("gpa");
                }
                student = new Student(
                    rs.getInt("student_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email_address"),
                    rs.getInt("grade_level"),
                    gpa
                );
                Character grade = null;
                if(rs.getString("grade") != null) {
                    grade = rs.getString("grade").charAt(0);
                }
                Double gradePercentage = null;
                if(rs.getString("grade_percentage") != null) {
                    gradePercentage = rs.getDouble("grade_percentage");
                }
                studentCourse = new StudentCourse(
                    rs.getInt("student_id"),
                    rs.getInt("course_id"),
                    grade,
                    gradePercentage,
                    rs.getInt("course_hour")
                );
                scss.add(new StudentCourseStudent(studentCourse, student));
            }

            return scss;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public StudentCourse getStudentCourse(int studentId, int courseId) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            String sql = "SELECT * FROM school.student_course WHERE student_id=? AND course_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);

            ResultSet rs = ps.executeQuery();
            StudentCourse studentCourse = null;

            if(rs.next()) {
                Character grade = null;
                if(rs.getString("grade") != null) {
                    grade = rs.getString("grade").charAt(0);
                }
                Double gradePercentage = null;
                if(rs.getString("grade_percentage") != null) {
                    gradePercentage = rs.getDouble("grade_percentage");
                }
                studentCourse = new StudentCourse(
                    rs.getInt("student_id"),
                    rs.getInt("course_id"),
                    grade,
                    gradePercentage,
                    rs.getInt("course_hour")
                );
            }

            return studentCourse;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public AttendanceRecord getAttendanceRecordByAttendanceId(int attendanceId) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            String sql = "SELECT * FROM school.attendance_record WHERE attendance_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, attendanceId);

            ResultSet rs = ps.executeQuery();
            AttendanceRecord attendanceRecord = null;
            
            if(rs.next()) {
                attendanceRecord = new AttendanceRecord(
                    rs.getInt("attendance_id"),
                    rs.getInt("student_id"),
                    rs.getInt("course_id"),
                    rs.getString("attendance_date"),
                    rs.getBoolean("present")
                );
            }

            return attendanceRecord;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<AttendanceRecord> getAttendanceRecords(int studentId, int courseId, int limit, int offset, String orderBy, boolean ascending) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            if(!validateOrderBy(orderBy)) {
                return null;
            }
            String asc = ascending ? "ASC" : "DESC";

            String sql = "SELECT * FROM school.attendance_record WHERE student_id=? AND course_id=? ORDER BY " + orderBy + " " + asc + " LIMIT ? OFFSET ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ps.setInt(3, limit);
            ps.setInt(4, offset);

            ResultSet rs = ps.executeQuery();
            List<AttendanceRecord> attendanceRecords = new ArrayList<>();
            
            while(rs.next()) {
               attendanceRecords.add(new AttendanceRecord(
                    rs.getInt("attendance_id"),
                    rs.getInt("student_id"),
                    rs.getInt("course_id"),
                    rs.getString("attendance_date"),
                    rs.getBoolean("present")
                ));
            }

            return attendanceRecords;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<AttendanceRecord> getAttendanceRecordsByDate(int studentId, int courseId, String minDate, String maxDate, int limit, int offset, String orderBy, boolean ascending) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            if(!validateOrderBy(orderBy)) {
                return null;
            }
            String asc = ascending ? "ASC" : "DESC";

            String sql = "SELECT * FROM school.attendance_record WHERE student_id=? AND course_id=? AND attendance_date BETWEEN ? AND ? ORDER BY " + orderBy + " " + asc + " LIMIT ? OFFSET ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ps.setString(3, minDate);
            ps.setString(4, maxDate);
            ps.setInt(5, limit);
            ps.setInt(6, offset);

            ResultSet rs = ps.executeQuery();
            List<AttendanceRecord> attendanceRecords = new ArrayList<>();
            
            while(rs.next()) {
               attendanceRecords.add(new AttendanceRecord(
                    rs.getInt("attendance_id"),
                    rs.getInt("student_id"),
                    rs.getInt("course_id"),
                    rs.getString("attendance_date"),
                    rs.getBoolean("present")
                ));
            }

            return attendanceRecords;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * UPDATE methods: 
     * update given record entirely in database
     * return nothing on success 
     * throw SQLException on failure
     */

    public void updateStudent(Student student) throws SQLException {
        Connection conn = ConnectionUtil.getConnection();
        String sql = "UPDATE school.student SET first_name=?, last_name=?, email_address=?, grade_level=?, gpa=? WHERE student_id=?";
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, student.getFirstName());
        ps.setString(2, student.getLastName());
        ps.setString(3, student.getEmailAddress());
        ps.setInt(4, student.getGradeLevel());
        if(student.getGpa() != null) {
            ps.setDouble(5, student.getGpa());
        } else {
            ps.setNull(5, Types.NULL);
        }
        ps.setInt(6, student.getStudentId());
        ps.executeUpdate();
    }

    public void updateTeacher(Teacher teacher) throws SQLException {
        Connection conn = ConnectionUtil.getConnection();
        String sql = "UPDATE school.teacher SET first_name=?, last_name=?, email_address=?, phone_number=?, salary=?, hire_date=? WHERE teacher_id=?";
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, teacher.getFirstName());
        ps.setString(2, teacher.getLastName());
        ps.setString(3, teacher.getEmailAddress());
        ps.setString(4, teacher.getPhoneNumber());
        ps.setDouble(5, teacher.getSalary());
        ps.setString(6, teacher.getHireDate());
        ps.setInt(7, teacher.getTeacherId());
        ps.executeUpdate();
    }

    public void updateClassroom(Classroom classroom) throws SQLException {
        Connection conn = ConnectionUtil.getConnection();
        String sql = "UPDATE school.classroom SET classroom_number=?, capacity=? WHERE classroom_id=?";
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setString(1, classroom.getClassroomNumber());
        ps.setInt(2, classroom.getCapacity());
        ps.setInt(3, classroom.getClassroomId());
        ps.executeUpdate();
    }

    public void updateCourse(Course course) throws SQLException {
        Connection conn = ConnectionUtil.getConnection();
        String sql = "UPDATE school.course SET teacher_id=?, classroom_id=?, course_name=?, course_code=?, course_hours=?, subject_name=? WHERE course_id=?";
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setInt(1, course.getTeacherId());
        ps.setInt(2, course.getClassroomId());
        ps.setString(3, course.getCourseName());
        ps.setString(4, course.getCourseCode());
        ps.setString(5, course.getCourseHours());
        ps.setString(6, course.getSubjectName());
        ps.setInt(7, course.getCourseId());
        ps.executeUpdate();
    }

    public void updateStudentCourse(StudentCourse studentCourse) throws SQLException {
        Connection conn = ConnectionUtil.getConnection();
        String sql = "UPDATE school.student_course SET grade=?, grade_percentage=?, course_hour=? WHERE student_id=? AND course_id=?";
        PreparedStatement ps = conn.prepareStatement(sql);

        String grade = null;
        if(studentCourse.getGrade() != null) {
            grade = String.valueOf(studentCourse.getGrade());
        }
        ps.setString(1, grade);
        if(studentCourse.getGradePercentage() != null) {
            ps.setDouble(2, studentCourse.getGradePercentage());
        } else {
            ps.setNull(2, Types.NULL);
        }
        ps.setInt(3, studentCourse.getCourseHour());
        ps.setInt(4, studentCourse.getStudentId());
        ps.setInt(5, studentCourse.getCourseId());
        ps.executeUpdate();
    }

    public void updateAttendanceRecord(AttendanceRecord attendanceRecord) throws SQLException {
        Connection conn = ConnectionUtil.getConnection();
        String sql = "UPDATE school.attendance_record SET present=? WHERE attendance_id=?";
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setBoolean(1, attendanceRecord.isPresent());
        ps.setInt(2, attendanceRecord.getAttendanceId());
        ps.executeUpdate();
    }

    /*
     * DELETE FROM methods:
     * delete record given by Id from database
     * return whether record was found in the database
     */

    public boolean deleteStudentByStudentId(int studentId) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            String sql = "DELETE FROM school.student WHERE student_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, studentId);

            int affectedRows = ps.executeUpdate();
            return affectedRows == 1;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteTeacherByTeacherId(int teacherId) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            String sql = "DELETE FROM school.teacher WHERE teacher_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, teacherId);

            int affectedRows = ps.executeUpdate();
            return affectedRows == 1;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteClassroomByClassroomId(int classroomId) {
        try {
            Connection conn = ConnectionUtil.getConnection();
            String sql = "DELETE FROM school.classroom WHERE classroom_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, classroomId);

            int affectedRows = ps.executeUpdate();
            return affectedRows == 1;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
     * throw SQLException if course not allowed to be deleted
     */
    public boolean deleteCourseByCourseId(int courseId) throws SQLException {
        Connection conn = ConnectionUtil.getConnection();
        String sql = "DELETE FROM school.course WHERE course_id=?";
        PreparedStatement ps = conn.prepareStatement(sql);

        ps.setInt(1, courseId);

        int affectedRows = ps.executeUpdate();
        return affectedRows == 1;
    }
}