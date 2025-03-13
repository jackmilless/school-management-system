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
 * update student gpa when a StudentCourse's grade is updated (including if it is removed)
 */
public class UpdateGradeTrigger extends TriggerAdapter {
    @Override 
    public void fire(Connection conn, ResultSet oldRow, ResultSet newRow) throws SQLException {
        Map<Character, Double> gradeGpas = new HashMap<>();
        gradeGpas.put('A', 4.000);
        gradeGpas.put('B', 3.000);
        gradeGpas.put('C', 2.000);
        gradeGpas.put('D', 1.000);
        gradeGpas.put('F', 0.000);

        // no need to update if grade is unchanged
        if(oldRow.getString("grade") != newRow.getString("grade")) {
            int studentId = newRow.getInt("student_id");
            Character oldGrade = null;
            Character newGrade = null;
            if(oldRow.getString("grade") != null) {
                oldGrade = oldRow.getString("grade").charAt(0);
            }
            if(newRow.getString("grade") != null) {
                newGrade = newRow.getString("grade").charAt(0);
            }
            double oldGradeGpa = oldGrade != null ? gradeGpas.get(oldGrade) : 0;
            double newGradeGpa = newGrade != null ? gradeGpas.get(newGrade) : 0;

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
                            sql = "UPDATE school.student SET gpa=(gpa * (? + ?) - ? + ?)/(CAST (? AS NUMERIC(6, 3))) WHERE student_id=?";
                            ps = conn.prepareStatement(sql);
                            ps.setDouble(1, numGrades);
                            int adjustment = 0;
                            if(oldGrade == null) {
                                adjustment = -1;
                            } else if(newGrade == null) {
                                adjustment = 1;
                            }
                            ps.setInt(2, adjustment);
                            ps.setDouble(3, oldGradeGpa);
                            ps.setDouble(4, newGradeGpa);
                            ps.setDouble(5, numGrades);
                            ps.setInt(6, studentId);
                            ps.executeUpdate();
                        } else {
                            sql = "UPDATE school.student SET gpa=? WHERE student_id=?";
                            ps = conn.prepareStatement(sql);
                            ps.setDouble(1, newGradeGpa);
                            ps.setInt(2, studentId);
                            ps.executeUpdate();
                        }
                    } else { // ensure gpa is null if no grades are recorded after update 
                        sql = "UPDATE school.student SET gpa=NULL WHERE student_id=?";
                        ps.setInt(1, studentId);
                        ps.executeUpdate();
                    }   
                }
            }
        }
    }
}
