package com.github.jackmilless.schoolmanagementsystem;

import com.github.jackmilless.schoolmanagementsystem.Controller.SchoolSystemController;
import com.github.jackmilless.schoolmanagementsystem.DAO.SchoolSystemDAO;
import com.github.jackmilless.schoolmanagementsystem.Service.SchoolSystemService;
import com.github.jackmilless.schoolmanagementsystem.Util.ConnectionUtil;

import org.h2.tools.RunScript;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;

/*
 * SchoolManagementApp
 * initalizes database and starts application 
 */
public class SchoolManagementApp {
    public static void main(String[] args) { 
        // run sql files to initialize database and insert example data
        try {
            InputStream in = SchoolManagementApp.class.getResourceAsStream("/db/create_sms_tables.sql");
            RunScript.execute(ConnectionUtil.getConnection(), new InputStreamReader(in));
            in = SchoolManagementApp.class.getResourceAsStream("/db/insert_basic_records.sql");
            RunScript.execute(ConnectionUtil.getConnection(), new InputStreamReader(in));
            in = SchoolManagementApp.class.getResourceAsStream("/db/insert_students.sql");
            RunScript.execute(ConnectionUtil.getConnection(), new InputStreamReader(in));
            in = SchoolManagementApp.class.getResourceAsStream("/db/insert_student_courses.sql");
            RunScript.execute(ConnectionUtil.getConnection(), new InputStreamReader(in));
            in = SchoolManagementApp.class.getResourceAsStream("/db/insert_attendance_records.sql");
            RunScript.execute(ConnectionUtil.getConnection(), new InputStreamReader(in));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // initialize/insert dependencies and start server
        SchoolSystemDAO schoolSystemDAO = new SchoolSystemDAO();
        SchoolSystemService schoolSystemService = new SchoolSystemService(schoolSystemDAO);
        SchoolSystemController schoolSystemController = new SchoolSystemController(schoolSystemService);    
        schoolSystemController.startAPI();
    }
}
