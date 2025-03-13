package com.github.jackmilless.schoolmanagementsystem.Triggers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.h2.tools.TriggerAdapter;

/*
 * InsertGradeTrigger:
 * update student gpa when a new StudentCourse is inserted with grade given
 */
public class InsertGradeTrigger extends TriggerAdapter {
    @Override 
    public void fire(Connection conn, ResultSet oldRow, ResultSet newRow) throws SQLException {
        Map<Character, Double> gradeGpas = new HashMap<>();
        gradeGpas.put('A', 4.000);
        gradeGpas.put('B', 3.000);
        gradeGpas.put('C', 2.000);
        gradeGpas.put('D', 1.000);
        gradeGpas.put('F', 0.000);

        // no need to update if no grade is provided
        if(newRow.getString("grade") != null) {
            int studentId = newRow.getInt("student_id");
            char newGrade = newRow.getString("grade").charAt(0);
            double gradeGpa = gradeGpas.get(newGrade);

            String sql = "SELECT COUNT(*) AS count FROM school.student_course WHERE student_id=? AND grade IS NOT NULL";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                double numGrades = rs.getInt("count");

                sql = "SELECT gpa FROM school.student WHERE student_id=?";
                ps = conn.prepareStatement(sql);
                ps.setInt(1, studentId);
                rs = ps.executeQuery();

                if(rs.next()) {
                    String checkGpa = rs.getString("gpa");
                    
                    // update gpa if grades are recorded
                    if(numGrades != 0) {
                        if(checkGpa != null) {
                            sql = "UPDATE school.student SET gpa=(gpa * (? - 1.0) + ?)/(CAST (? AS NUMERIC(6, 3))) WHERE student_id=?";
                            ps = conn.prepareStatement(sql);
                            ps.setDouble(1, numGrades);
                            ps.setDouble(2, gradeGpa);
                            ps.setDouble(3, numGrades);
                            ps.setInt(4, studentId);
                            ps.executeUpdate();
                        } else {
                            sql = "UPDATE school.student SET gpa=? WHERE student_id=?";
                            ps = conn.prepareStatement(sql);
                            ps.setDouble(1, gradeGpa);
                            ps.setInt(2, studentId);
                            ps.executeUpdate();
                        }
                    } 
                }
            }
        }
    }
}
