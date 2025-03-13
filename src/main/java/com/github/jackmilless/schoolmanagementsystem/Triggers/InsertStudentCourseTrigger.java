package com.github.jackmilless.schoolmanagementsystem.Triggers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.h2.tools.TriggerAdapter;

/*
 * InsertStudentCourseTrigger:
 * check that inserted studentCourse includes an available course hour for student and course,
 * and that classroom it takes place in is not at capacity
 */
public class InsertStudentCourseTrigger extends TriggerAdapter {
    @Override 
    public void fire(Connection conn, ResultSet oldRow, ResultSet newRow) throws SQLException {
        int courseId = newRow.getInt("course_id");
        int studentId = newRow.getInt("student_id");
        int courseHour = newRow.getInt("course_hour");
        char[] chCourseHours = {'_', '_', '_', '_', '_', '_'};
        chCourseHours[courseHour - 1] = '1';
        String courseHours = new String(chCourseHours);

        // check whether courseHour is listed in the course
        String sql = "SELECT COUNT(*) AS count FROM school.course WHERE course_id=? AND course_hours LIKE ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, courseId);
        ps.setString(2, courseHours);
        ResultSet rs = ps.executeQuery();

        if(rs.next()) {
            if(rs.getInt("count") == 0) {
                throw new SQLException("Course hour invalid for given course");
            }
        }

        // check whether classroom associated with course has space for student at this hour
        sql = "SELECT COUNT(*) AS count FROM school.student_course WHERE course_id=? AND course_hour=?";
        ps = conn.prepareStatement(sql);
        ps.setInt(1, courseId);
        ps.setInt(2, courseHour);
        rs = ps.executeQuery();

        if(rs.next()) {
            int count = rs.getInt("count");

            sql = "SELECT cl.capacity AS capacity FROM school.classroom cl " + 
                                                 "JOIN school.course co ON cl.classroom_id = co.classroom_id " +
                                                 "WHERE co.course_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, courseId);
            rs = ps.executeQuery();

            if(rs.next()) {
                if(count >= rs.getInt("capacity")) {
                    throw new SQLException("Classroom with given course at given hour is at full capacity");
                }
            }
        }

        // check whether student is already taking a course at this hour
        sql = "SELECT COUNT(*) AS count FROM school.student_course WHERE course_hour=? AND student_id=?";
        ps = conn.prepareStatement(sql);
        ps.setInt(1, courseHour);
        ps.setInt(2, studentId);
        rs = ps.executeQuery();

        if(rs.next()) {
            if(rs.getInt("count") > 0) {
                throw new SQLException("Course hour already exists for given student");
            }
        }
    }
}