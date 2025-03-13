package com.github.jackmilless.schoolmanagementsystem.Triggers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;

import org.h2.tools.TriggerAdapter;

/*
 * InsertCourseTrigger:
 * check that referenced teacher and classroom in inserted course has available hours for course
 * throw SQLException if teacher or classroom has overlapping hours with course user is attempting to insert
 */
public class InsertCourseTrigger extends TriggerAdapter {
    @Override 
    public void fire(Connection conn, ResultSet oldRow, ResultSet newRow) throws SQLException {
        int teacherId = newRow.getInt("teacher_id");
        int classroomId = newRow.getInt("classroom_id");
        String courseHours = newRow.getString("course_hours");

        // check all existing course_hours from associated teacher to see if there are overlaps
        String sql = "SELECT course_hours FROM school.course WHERE teacher_id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, teacherId);
        ResultSet rs = ps.executeQuery();
        List<String> courseHoursList = new ArrayList<>(); 

        while(rs.next()) {
            courseHoursList.add(rs.getString("course_hours"));
        }

        for(String otherCourseHours : courseHoursList) {
            for(int j = 0; j < 6; j++) {
                if(otherCourseHours.charAt(j) == '1' && courseHours.charAt(j) == '1') {
                    throw new SQLException("Course hours overlap with other course taught by given teacher");
                }
            }
        }

        // check all existing course_hours from associated classroom to see if there are overlaps
        sql = "SELECT course_hours FROM school.course WHERE classroom_id=?";
        ps = conn.prepareStatement(sql);
        ps.setInt(1, classroomId);
        rs = ps.executeQuery();
        courseHoursList.clear();

        while(rs.next()) {
            courseHoursList.add(rs.getString("course_hours"));
        }

        for(String otherCourseHours : courseHoursList) {
            for(int j = 0; j < 6; j++) {
                if(otherCourseHours.charAt(j) == '1' && courseHours.charAt(j) == '1') {
                    throw new SQLException("Course hours overlap with other course taught in given classroom");
                }
            }
        }
    }
}
