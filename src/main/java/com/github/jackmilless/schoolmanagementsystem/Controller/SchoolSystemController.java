package com.github.jackmilless.schoolmanagementsystem.Controller;

import com.github.jackmilless.schoolmanagementsystem.Service.SchoolSystemService;
import com.github.jackmilless.schoolmanagementsystem.Model.*;
import com.github.jackmilless.schoolmanagementsystem.Util.ConnectionUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.vue.VueComponent;

import java.sql.SQLException;
import java.util.List;

/*
 * SchoolSystemController
 * uses Javalin to start/stop server and provide endpoints for http requests
 * and uses Vue plugin to initialize frontend pages stored in resources/vue
 * defines handlers that call SchoolSystemService methods to complete http requests
 * and send responses 
 */
public class SchoolSystemController {
    private SchoolSystemService schoolSystemService;
    private final ObjectMapper mapper = new ObjectMapper();

    public SchoolSystemController(SchoolSystemService schoolSystemService) {
        this.schoolSystemService = schoolSystemService; 
    }

    /*
     * startAPI
     * start server, create frontend and api endpoints
     * return Javalin server instance
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create(config -> {
            config.jetty.defaultHost = "localhost"; // only listen on host computer
            config.staticFiles.enableWebjars();
            config.vue.vueInstanceNameInJs = "app";
        }).start(7000);

        // frontend webpage endpoints, stored in resources/vue/views
        app.get("/", new VueComponent("home-page"));
        app.get("/teachers", new VueComponent("teachers"));
        app.get("/teachers/{teacher-id}", new VueComponent("teacher"));
        app.get("/classrooms", new VueComponent("classrooms"));
        app.get("/classrooms/{classroom-id}", new VueComponent("classroom"));
        app.get("/students", new VueComponent("students"));
        app.get("/students/{student-id}", new VueComponent("student"));
        app.get("/courses", new VueComponent("courses"));
        app.get("/courses/{course-id}", new VueComponent("course"));
        app.get("/courses/{course-id}/students", new VueComponent("course-students"));
        app.get("/students/{student-id}/courses", new VueComponent("student-courses"));
        app.get("/students/{student-id}/courses/{course-id}", new VueComponent("student-course"));
        app.get("/courses/{course-id}/students/{student-id}", new VueComponent("student-course"));
        app.get("/classrooms/{classroom-id}/courses", new VueComponent("classroom-courses"));
        app.get("/teachers/{teacher-id}/courses", new VueComponent("teacher-courses"));
        app.get("/students/{student-id}/courses/{course-id}/attendance-records", new VueComponent("attendance-records"));
        app.get("/students/{student-id}/courses/{course-id}/attendance-records/{attendance-id}", new VueComponent("attendance-record"));

        // add a record to the database
        app.post("/api/students", this::addStudent);
        app.post("/api/teachers", this::addTeacher);
        app.post("/api/classrooms", this::addClassroom);
        app.post("/api/courses", this::addCourse);
        app.post("/api/courses/{course-id}/students", this::addStudentCourse);
        app.post("/api/students/{student-id}/courses/{course-id}/attendance-records", this::addAttendanceRecord);

        // find all records of a given type from the database
        app.get("/api/students", this::findStudents);
        app.get("/api/teachers", this::findTeachers);
        app.get("/api/classrooms", this::findClassrooms);
        app.get("/api/courses", this::findCourses);

        // find record(s) of a given type by id 
        app.get("/api/students/{student-id}", this::findStudentByStudentId);
        app.get("/api/teachers/{teacher-id}", this::findTeacherByTeacherId);
        app.get("/api/classrooms/{classroom-id}", this::findClassroomByClassroomId);
        app.get("/api/courses/{course-id}", this::findCourseByCourseId);
        app.get("/api/teachers/{teacher-id}/courses", this::findCoursesByTeacherId);
        app.get("/api/classrooms/{classroom-id}/courses", this::findCoursesByClassroomId);
        app.get("/api/students/{student-id}/courses", this::findCoursesByStudentId);
        app.get("/api/courses/{course-id}/students", this::findStudentsByCourseId);
        app.get("/api/students/{student-id}/courses/{course-id}", this::findStudentCourse);
        app.get("/api/courses/{course-id}/students/{student-id}", this::findStudentCourse);
        app.get("/api/students/{student-id}/courses/{course-id}/attendance-records", this::findAttendanceRecords);
        app.get("/api/students/{student-id}/courses/{course-id}/attendance-records/{attendance-id}", this::findAttendanceRecord);

        // update a record of a given type
        app.put("/api/students/{student-id}", this::updateStudentByStudentId);
        app.put("/api/teachers/{teacher-id}", this::updateTeacherByTeacherId);
        app.put("/api/classrooms/{classroom-id}", this::updateClassroomByClassroomId);
        app.put("/api/courses/{course-id}", this::updateCourseByCourseId);
        app.put("/api/students/{student-id}/courses/{course-id}", this::updateGradeByStudentIdAndCourseId);
        app.put("/api/courses/{course-id}/students/{student-id}", this::updateGradeByStudentIdAndCourseId);
        app.put("/api/students/{student-id}/courses/{course-id}/attendance-records/{attendance-id}", this::updateAttendanceRecord);

        // delete record of a given type by id
        app.delete("/api/students/{student-id}", this::deleteStudentByStudentId);
        app.delete("/api/teachers/{teacher-id}", this::deleteTeacherByTeacherId);
        app.delete("/api/classrooms/{classroom-id}", this::deleteClassroomByClassroomId);
        app.delete("/api/courses/{course-id}", this::deleteCourseByCourseId);

        // close database connection and stop server
        app.get("/api/stop", ctx -> {
            ConnectionUtil.closeConnection();
            app.stop();
        });

        // handle exceptions, send 400 or 500 status code to frontend
        app.exception(SQLException.class, this::handleSQLException);
        app.exception(JsonProcessingException.class, this::handleJsonProcessingException);

        return app;
    }

    /*
     * POST handlers: persist record to database and return persisted record
     * Return 200 OK status on success, failure handled by Exception handlers
     */

    public void addStudent(Context ctx) throws JsonProcessingException, SQLException {
        Student student = mapper.readValue(ctx.body(), Student.class);
        Student enteredStudent = schoolSystemService.addStudent(student);
        ctx.json(mapper.writeValueAsString(enteredStudent));
    }

    public void addTeacher(Context ctx) throws JsonProcessingException, SQLException {
        Teacher teacher = mapper.readValue(ctx.body(), Teacher.class);
        Teacher enteredTeacher = schoolSystemService.addTeacher(teacher);
        ctx.json(mapper.writeValueAsString(enteredTeacher));
    }

    public void addClassroom(Context ctx) throws JsonProcessingException, SQLException {
        Classroom classroom = mapper.readValue(ctx.body(), Classroom.class);
        Classroom enteredClassroom = schoolSystemService.addClassroom(classroom);
        ctx.json(mapper.writeValueAsString(enteredClassroom));
    }

    public void addCourse(Context ctx) throws JsonProcessingException, SQLException {
        Course course = mapper.readValue(ctx.body(), Course.class);
        Course enteredCourse = schoolSystemService.addCourse(course);
        ctx.json(mapper.writeValueAsString(enteredCourse));
    }

    public void addStudentCourse(Context ctx) throws JsonProcessingException, SQLException {
        StudentCourse studentCourse = mapper.readValue(ctx.body(), StudentCourse.class);
        studentCourse.setCourseId(Integer.parseInt(ctx.pathParam("course-id")));
        StudentCourse enteredStudentCourse = schoolSystemService.addStudentCourse(studentCourse);
        ctx.json(mapper.writeValueAsString(enteredStudentCourse));
    }

    public void addAttendanceRecord(Context ctx) throws JsonProcessingException, SQLException {
        AttendanceRecord attendanceRecord = mapper.readValue(ctx.body(), AttendanceRecord.class);
        attendanceRecord.setStudentId(Integer.parseInt(ctx.pathParam("student-id")));
        attendanceRecord.setCourseId(Integer.parseInt(ctx.pathParam("course-id")));
        AttendanceRecord enteredAttendanceRecord = schoolSystemService.addAttendanceRecord(attendanceRecord);
        ctx.json(mapper.writeValueAsString(enteredAttendanceRecord));
    }

    /* 
     * GET handlers: obtain and return requested records from database 
     * parse query parameters and, if present, call alternative methods
     * to refine results of search.
     * Return 200 OK status if records found, or 204 no content if not 
     */

    public void findStudents(Context ctx) throws JsonProcessingException {
        String firstName = ctx.queryParam("firstname");
        String lastName = ctx.queryParam("lastname");
        String emailAddress = ctx.queryParam("emailaddress");
        Integer gradeLevel = ctx.queryParam("gradelevel") != null ? Integer.parseInt(ctx.queryParam("gradelevel")) : null;
        Double minGpa = ctx.queryParam("mingpa") != null ? Double.parseDouble(ctx.queryParam("mingpa")) : null;
        Double maxGpa = ctx.queryParam("maxgpa") != null ? Double.parseDouble(ctx.queryParam("maxgpa")) : null;

        List<Student> students;

        if(firstName != null && lastName != null) {
            students = schoolSystemService.getStudentByFullName(firstName, lastName);
        } else if(emailAddress != null) {
            students = schoolSystemService.getStudentByEmail(emailAddress);
        } else {
            int limit = ctx.queryParam("limit") != null ? Integer.parseInt(ctx.queryParam("limit")) : 10;
            int offset = ctx.queryParam("offset") != null ? Integer.parseInt(ctx.queryParam("offset")) : 0;
            String orderBy = ctx.queryParam("orderby") != null ? ctx.queryParam("orderby") : "last_name";
            boolean ascending = ctx.queryParam("ascending") != null ? Boolean.parseBoolean(ctx.queryParam("ascending")) : true;

            if(gradeLevel != null) {
                students = schoolSystemService.getStudentsByGradeLevel(gradeLevel, limit, offset, orderBy, ascending);
            } else if(minGpa != null && maxGpa != null) {
                students = schoolSystemService.getStudentsByGpa(minGpa, maxGpa, limit, offset, orderBy, ascending);
            } else {
                students = schoolSystemService.getAllStudents(limit, offset, orderBy, ascending);
            }
        }

        if(students == null) {
            ctx.status(204);
            ctx.json("");
        } else {
            ctx.json(mapper.writeValueAsString(students));
        }
    }

    public void findTeachers(Context ctx) throws JsonProcessingException {
        String firstName = ctx.queryParam("firstname");
        String lastName = ctx.queryParam("lastname");
        String emailAddress = ctx.queryParam("emailaddress");

        List<Teacher> teachers;

        if(firstName != null && lastName != null) {
            teachers = schoolSystemService.getTeacherByFullName(firstName, lastName);
        } else if(emailAddress != null) {
            teachers = schoolSystemService.getTeacherByEmail(emailAddress);
        } else {
            int limit = ctx.queryParam("limit") != null ? Integer.parseInt(ctx.queryParam("limit")) : 10;
            int offset = ctx.queryParam("offset") != null ? Integer.parseInt(ctx.queryParam("offset")) : 0;
            String orderBy = ctx.queryParam("orderby") != null ? ctx.queryParam("orderby") : "last_name";
            boolean ascending = ctx.queryParam("ascending") != null ? Boolean.parseBoolean(ctx.queryParam("ascending")) : true;
            teachers = schoolSystemService.getAllTeachers(limit, offset, orderBy, ascending);
        }

        if(teachers == null) {
            ctx.status(204);
            ctx.json("");
        } else {
            ctx.json(mapper.writeValueAsString(teachers));
        }
    }

    public void findClassrooms(Context ctx) throws JsonProcessingException {
        String classroomNumber = ctx.queryParam("classroomnumber");
        Integer minCapacity = ctx.queryParam("mincapacity") != null ? Integer.parseInt(ctx.queryParam("mincapacity")) : null;
        Integer maxCapacity = ctx.queryParam("maxcapacity") != null ? Integer.parseInt(ctx.queryParam("maxcapacity")) : null;

        List<Classroom> classrooms;

        if(classroomNumber != null) {
            classrooms = schoolSystemService.getClassroomByNumber(classroomNumber);
        } else {
            int limit = ctx.queryParam("limit") != null ? Integer.parseInt(ctx.queryParam("limit")) : 10;
            int offset = ctx.queryParam("offset") != null ? Integer.parseInt(ctx.queryParam("offset")) : 0;
            String orderBy = ctx.queryParam("orderby") != null ? ctx.queryParam("orderby") : "classroom_number";
            boolean ascending = ctx.queryParam("ascending") != null ? Boolean.parseBoolean(ctx.queryParam("ascending")) : true;

            if(minCapacity != null && maxCapacity != null) {
                classrooms = schoolSystemService.getClassroomsByCapacity(minCapacity, maxCapacity, limit, offset, orderBy, ascending);
            } else {
                classrooms = schoolSystemService.getAllClassrooms(limit, offset, orderBy, ascending);
            }
        }

        if(classrooms == null) {
            ctx.status(204);
            ctx.json("");
        } else {
            ctx.json(mapper.writeValueAsString(classrooms));
        }
    }

    public void findCourses(Context ctx) throws JsonProcessingException {
        String courseName = ctx.queryParam("coursename");
        String courseCode = ctx.queryParam("coursecode");
        String courseHours = ctx.queryParam("coursehours");
        String subjectName = ctx.queryParam("subjectname");

        List<Course> courses;

        if(courseName != null) {
            courses = schoolSystemService.getCourseByName(courseName);
        } else if(courseCode != null) {
            courses = schoolSystemService.getCourseByCode(courseCode);
        } else {
            int limit = ctx.queryParam("limit") != null ? Integer.parseInt(ctx.queryParam("limit")) : 10;
            int offset = ctx.queryParam("offset") != null ? Integer.parseInt(ctx.queryParam("offset")) : 0;
            String orderBy = ctx.queryParam("orderby") != null ? ctx.queryParam("orderby") : "course_name";
            boolean ascending = ctx.queryParam("ascending") != null ? Boolean.parseBoolean(ctx.queryParam("ascending")) : true;

            if(courseHours != null) {
                courses = schoolSystemService.getCoursesByHours(courseHours, limit, offset, orderBy, ascending);
            } else if(subjectName != null) {
                courses = schoolSystemService.getCoursesBySubject(subjectName, limit, offset, orderBy, ascending);
            } else {
                courses = schoolSystemService.getAllCourses(limit, offset, orderBy, ascending);
            }
        }

        if(courses == null) {
            ctx.status(204);
            ctx.json("");
        } else {
            ctx.json(mapper.writeValueAsString(courses));
        }
    }

    public void findStudentByStudentId(Context ctx) throws JsonProcessingException {
        int studentId = Integer.parseInt(ctx.pathParam("student-id"));
        Student student = schoolSystemService.getStudentByStudentId(studentId);

        if(student == null) {
            ctx.status(204);
            ctx.json("");
        } else {
            ctx.json(mapper.writeValueAsString(student));
        }
    }

    public void findTeacherByTeacherId(Context ctx) throws JsonProcessingException {
        int teacherId = Integer.parseInt(ctx.pathParam("teacher-id"));
        Teacher teacher = schoolSystemService.getTeacherByTeacherId(teacherId);
        
        if(teacher == null) {
            ctx.status(204);
            ctx.json("");
        } else {
            ctx.json(mapper.writeValueAsString(teacher));
        }
    }

    public void findClassroomByClassroomId(Context ctx) throws JsonProcessingException {
        int classroomId = Integer.parseInt(ctx.pathParam("classroom-id"));
        Classroom classroom = schoolSystemService.getClassroomByClassroomId(classroomId);
        
        if(classroom == null) {
            ctx.status(204);
            ctx.json("");
        } else {
            ctx.json(mapper.writeValueAsString(classroom));
        }
    }

    public void findCourseByCourseId(Context ctx) throws JsonProcessingException {
        int courseId = Integer.parseInt(ctx.pathParam("course-id"));
        Course course = schoolSystemService.getCourseByCourseId(courseId);
        
        if(course == null) {
            ctx.status(204);
            ctx.json("");
        } else {
            ctx.json(mapper.writeValueAsString(course));
        }
    }

    public void findCoursesByTeacherId(Context ctx) throws JsonProcessingException {
        int teacherId = Integer.parseInt(ctx.pathParam("teacher-id"));
        List<Course> courses = schoolSystemService.getCoursesByTeacherId(teacherId);
        
        if(courses == null) {
            ctx.status(204);
            ctx.json("");
        } else {
            ctx.json(mapper.writeValueAsString(courses));
        }
    }

    public void findCoursesByClassroomId(Context ctx) throws JsonProcessingException {
        int classroomId = Integer.parseInt(ctx.pathParam("classroom-id"));
        List<Course> courses = schoolSystemService.getCoursesByClassroomId(classroomId);
        
        if(courses == null) {
            ctx.status(204);
            ctx.json("");
        } else {
            ctx.json(mapper.writeValueAsString(courses));
        }
    }

    public void findCoursesByStudentId(Context ctx) throws JsonProcessingException {
        int studentId = Integer.parseInt(ctx.pathParam("student-id"));
        
        int limit = ctx.queryParam("limit") != null ? Integer.parseInt(ctx.queryParam("limit")) : 10;
        int offset = ctx.queryParam("offset") != null ? Integer.parseInt(ctx.queryParam("offset")) : 0;
        String orderBy = ctx.queryParam("orderby") != null ? ctx.queryParam("orderby") : "c.course_name";
        boolean ascending = ctx.queryParam("ascending") != null ? Boolean.parseBoolean(ctx.queryParam("ascending")) : true;

        List<StudentCourseCourse> courses = schoolSystemService.getCoursesByStudentId(studentId, limit, offset, orderBy, ascending);

        if(courses == null) {
            ctx.status(204);
            ctx.json("");
        } else {
            ctx.json(mapper.writeValueAsString(courses));
        }
    }

    public void findStudentsByCourseId(Context ctx) throws JsonProcessingException {
        int courseId = Integer.parseInt(ctx.pathParam("course-id"));
        Character mingrade = ctx.queryParam("mingrade") != null ? ctx.queryParam("mingrade").charAt(0) : null;
        Character maxgrade = ctx.queryParam("maxgrade") != null ? ctx.queryParam("maxgrade").charAt(0) : null;

        int limit = ctx.queryParam("limit") != null ? Integer.parseInt(ctx.queryParam("limit")) : 10;
        int offset = ctx.queryParam("offset") != null ? Integer.parseInt(ctx.queryParam("offset")) : 0;
        String orderBy = ctx.queryParam("orderby") != null ? ctx.queryParam("orderby") : "s.last_name";
        boolean ascending = ctx.queryParam("ascending") != null ? Boolean.parseBoolean(ctx.queryParam("ascending")) : true;

        List<StudentCourseStudent> students;
        
        if(mingrade != null && maxgrade != null) {
            students = schoolSystemService.getStudentsByCourseIdAndGrade(courseId, mingrade, maxgrade, limit, offset, orderBy, ascending);
        } else {
            students = schoolSystemService.getStudentsByCourseId(courseId, limit, offset, orderBy, ascending);
        }
        
        if(students == null) {
            ctx.status(204);
            ctx.json("");
        } else {
            ctx.json(mapper.writeValueAsString(students));
        }
    }

    public void findStudentCourse(Context ctx) throws JsonProcessingException {
        int studentId = Integer.parseInt(ctx.pathParam("student-id"));
        int courseId = Integer.parseInt(ctx.pathParam("course-id"));

        StudentCourse grade = schoolSystemService.getStudentCourse(studentId, courseId);

        if(grade == null) {
            ctx.status(204);
            ctx.json("");
        } else {
            ctx.json(mapper.writeValueAsString(grade));
        }
    }

    public void findAttendanceRecords(Context ctx) throws JsonProcessingException {
        int studentId = Integer.parseInt(ctx.pathParam("student-id"));
        int courseId = Integer.parseInt(ctx.pathParam("course-id"));
        String minDate = ctx.queryParam("mindate");
        String maxDate = ctx.queryParam("maxdate");

        int limit = ctx.queryParam("limit") != null ? Integer.parseInt(ctx.queryParam("limit")) : 10;
        int offset = ctx.queryParam("offset") != null ? Integer.parseInt(ctx.queryParam("offset")) : 0;
        String orderBy = ctx.queryParam("orderby") != null ? ctx.queryParam("orderby") : "attendance_date";
        boolean ascending = ctx.queryParam("ascending") != null ? Boolean.parseBoolean(ctx.queryParam("ascending")) : false;

        List<AttendanceRecord> attendanceRecords;
        
        if(minDate != null && maxDate != null) {
            attendanceRecords = schoolSystemService.getAttendanceRecordsByDate(studentId, courseId, minDate, maxDate, limit, offset, orderBy, ascending);
        } else {
            attendanceRecords = schoolSystemService.getAttendanceRecords(studentId, courseId, limit, offset, orderBy, ascending);
        }

        if(attendanceRecords == null) {
            ctx.status(204);
            ctx.json("");
        } else {
            ctx.json(mapper.writeValueAsString(attendanceRecords));
        }
    }

    public void findAttendanceRecord(Context ctx) throws JsonProcessingException {
        int attendanceId = Integer.parseInt(ctx.pathParam("attendance-id"));

        AttendanceRecord attendanceRecord = schoolSystemService.getAttendanceRecordByAttendanceId(attendanceId);

        if(attendanceRecord == null) {
            ctx.status(204);
            ctx.json("");
        } else {
            ctx.json(mapper.writeValueAsString(attendanceRecord));
        }
    }

    /*
     * PUT handlers: update record in database and return updated record
     * Return 200 OK status on success, failure handled by Exception handlers
     */

    public void updateStudentByStudentId(Context ctx) throws JsonProcessingException, SQLException {
        int studentId = Integer.parseInt(ctx.pathParam("student-id"));
        Student student = mapper.readValue(ctx.body(), Student.class);
        student.setStudentId(studentId);
        Student updatedStudent = schoolSystemService.updateStudentByStudentId(student);
        ctx.json(mapper.writeValueAsString(updatedStudent));
    }

    public void updateTeacherByTeacherId(Context ctx) throws JsonProcessingException, SQLException {
        int teacherId = Integer.parseInt(ctx.pathParam("teacher-id"));
        Teacher teacher = mapper.readValue(ctx.body(), Teacher.class);
        teacher.setTeacherId(teacherId);
        Teacher updatedTeacher = schoolSystemService.updateTeacherByTeacherId(teacher);
        ctx.json(mapper.writeValueAsString(updatedTeacher));
    }

    public void updateClassroomByClassroomId(Context ctx) throws JsonProcessingException, SQLException {
        int classroomId = Integer.parseInt(ctx.pathParam("classroom-id"));
        Classroom classroom = mapper.readValue(ctx.body(), Classroom.class);
        classroom.setClassroomId(classroomId);
        Classroom updatedClassroom = schoolSystemService.updateClassroomByClassroomId(classroom);
        ctx.json(mapper.writeValueAsString(updatedClassroom));
    }

    public void updateCourseByCourseId(Context ctx) throws JsonProcessingException, SQLException {
        int courseId = Integer.parseInt(ctx.pathParam("course-id"));
        Course course = mapper.readValue(ctx.body(), Course.class);
        course.setCourseId(courseId);
        Course updatedCourse = schoolSystemService.updateCourseByCourseId(course);
        ctx.json(mapper.writeValueAsString(updatedCourse));
    }

    public void updateGradeByStudentIdAndCourseId(Context ctx) throws JsonProcessingException, SQLException {
        int studentId = Integer.parseInt(ctx.pathParam("student-id"));
        int courseId = Integer.parseInt(ctx.pathParam("course-id"));
        StudentCourse studentCourse = mapper.readValue(ctx.body(), StudentCourse.class);
        studentCourse.setStudentId(studentId);
        studentCourse.setCourseId(courseId);
        StudentCourse updatedStudentCourse = schoolSystemService.updateStudentCourse(studentCourse);
        ctx.json(mapper.writeValueAsString(updatedStudentCourse));
    }

    public void updateAttendanceRecord(Context ctx) throws JsonProcessingException, SQLException {
        int attendanceId = Integer.parseInt(ctx.pathParam("attendance-id"));
        AttendanceRecord attendanceRecord = mapper.readValue(ctx.body(), AttendanceRecord.class);
        attendanceRecord.setAttendanceId(attendanceId);
        AttendanceRecord updatedAttendanceRecord = schoolSystemService.updateAttendanceRecord(attendanceRecord);
        ctx.json(mapper.writeValueAsString(updatedAttendanceRecord));
    }
    
    /*
     * DELETE handlers: delete record from database and return deleted record
     * Return 200 OK status on success if record found, 204 no content if record not found
     * On failure (i.e. record exists but not allowed to be deleted) failure handled by Exception handlers
     */

    public void deleteStudentByStudentId(Context ctx) throws JsonProcessingException {
        int studentId = Integer.parseInt(ctx.pathParam("student-id"));
        Student deletedStudent = schoolSystemService.deleteStudentByStudentId(studentId);

        if(deletedStudent == null) {
            ctx.status(204);
            ctx.json("");
        } else {
            ctx.json(mapper.writeValueAsString(deletedStudent));
        }
    }

    public void deleteTeacherByTeacherId(Context ctx) throws JsonProcessingException {
        int teacherId = Integer.parseInt(ctx.pathParam("teacher-id"));
        Teacher deletedTeacher = schoolSystemService.deleteTeacherByTeacherId(teacherId);

        if(deletedTeacher == null) {
            ctx.status(204);
            ctx.json("");
        } else {
            ctx.json(mapper.writeValueAsString(deletedTeacher));
        }
    }

    public void deleteClassroomByClassroomId(Context ctx) throws JsonProcessingException {
        int classroomId = Integer.parseInt(ctx.pathParam("classroom-id"));
        Classroom deletedClassroom = schoolSystemService.deleteClassroomByClassroomId(classroomId);

        if(deletedClassroom == null) {
            ctx.status(204);
            ctx.json("");
        } else {
            ctx.json(mapper.writeValueAsString(deletedClassroom));
        }
    }

    public void deleteCourseByCourseId(Context ctx) throws JsonProcessingException, SQLException {
        int courseId = Integer.parseInt(ctx.pathParam("course-id"));
        Course deletedCourse = schoolSystemService.deleteCourseByCourseId(courseId);

        if(deletedCourse == null) {
            ctx.status(204);
            ctx.json("");
        } else {
            ctx.json(mapper.writeValueAsString(deletedCourse));
        }
    }

    /*
     * return 400 error with message from database, or 500 server error if unexpected JsonProcessingException occurs
     */
    public void handleSQLException(SQLException e, Context ctx) {
        try {
            ctx.json(mapper.writeValueAsString(e.getMessage()));
            ctx.status(400);
        } catch(JsonProcessingException jpe) {
            jpe.printStackTrace();
            ctx.status(500);
        }
    }

    /*
     * return 500 server error if unexpected JsonProcessingException occurs
     */
    public void handleJsonProcessingException(JsonProcessingException e, Context ctx) {
        ctx.status(500);
    }
}
