DROP SCHEMA IF EXISTS school CASCADE;
CREATE SCHEMA IF NOT EXISTS school;

CREATE TABLE IF NOT EXISTS school.teacher (
    teacher_id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email_address VARCHAR(100) NOT NULL, 
    phone_number CHAR(14) NOT NULL,
    salary NUMERIC(10, 2) NOT NULL,
    hire_date DATE NOT NULL,
    CHECK(phone_number LIKE '(___)-___-____'),
    CHECK(email_address LIKE '%_@school.org'),
    CONSTRAINT teacher_email_unique UNIQUE(email_address),
    CONSTRAINT phone_unique UNIQUE(phone_number)
);

CREATE TABLE IF NOT EXISTS school.classroom (
    classroom_id SERIAL PRIMARY KEY,
    classroom_number VARCHAR(10) NOT NULL,
    capacity INT NOT NULL,
    CHECK(capacity BETWEEN 1 AND 100),
    CONSTRAINT classroom_number_unique UNIQUE(classroom_number)
);

CREATE TABLE IF NOT EXISTS school.course (
    course_id SERIAL PRIMARY KEY,
    teacher_id INT,
    classroom_id INT,
    course_name VARCHAR(100) NOT NULL,
    course_code VARCHAR(10) NOT NULL,
    -- binary representation of available hours of the day. e.g. 010100 means course is taken during hours 2 and 4
    course_hours CHAR(6) NOT NULL,
    subject_name VARCHAR(30) NOT NULL,
    -- teacher/classroom of course can be changed without deleting records
    CONSTRAINT foreign_key_teacher_id FOREIGN KEY(teacher_id) REFERENCES teacher (teacher_id) ON DELETE SET NULL,
    CONSTRAINT foreign_key_classroom_id FOREIGN KEY(classroom_id) REFERENCES classroom (classroom_id) ON DELETE SET NULL,
    CONSTRAINT course_name_unique UNIQUE(course_name),
    CONSTRAINT course_code_unique UNIQUE(course_code)
);

CREATE TABLE IF NOT EXISTS school.student (
    student_id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email_address VARCHAR(100) NOT NULL, 
    grade_level INT NOT NULL,
    gpa NUMERIC(4, 3),
    CHECK(grade_level BETWEEN 9 AND 12),
    CHECK(gpa >= 0.000 AND gpa <= 5.000),
    CHECK(email_address LIKE '%_@school.org'),
    CONSTRAINT student_email_unique UNIQUE(email_address)
);

CREATE TABLE IF NOT EXISTS school.student_course (
    student_id INT NOT NULL,
    course_id INT NOT NULL, 
    grade CHAR(1),
    grade_percentage NUMERIC(5, 2),
    course_hour INT NOT NULL,
    -- delete all of students student_course records if they are deleted
    CONSTRAINT foreign_key_student_id FOREIGN KEY(student_id) REFERENCES student (student_id) ON DELETE CASCADE,
    -- can't delete course if student is still in it
    CONSTRAINT foreign_key_course_id FOREIGN KEY(course_id) REFERENCES course (course_id), 
    CONSTRAINT student_course_id PRIMARY KEY(student_id, course_id),
    CHECK(course_hour BETWEEN 1 AND 6),
    CONSTRAINT grade CHECK(grade IN ('A', 'B', 'C', 'D', 'F'))
);

CREATE TABLE IF NOT EXISTS school.attendance_record (
    attendance_id SERIAL PRIMARY KEY,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    attendance_date DATE DEFAULT CURRENT_DATE,
    present BOOLEAN NOT NULL,
    -- delete all of student's attendance_records if they are deleted
    FOREIGN KEY(student_id) REFERENCES student (student_id) ON DELETE CASCADE,
    FOREIGN KEY(course_id) REFERENCES course (course_id),
    CONSTRAINT student_course_date_unique UNIQUE(student_id, course_id, attendance_date)
);


-- check that referenced teacher and classroom in inserted/updated course has available hours for course

CREATE TRIGGER course_insert BEFORE INSERT ON school.course
    FOR EACH ROW CALL "com.github.jackmilless.schoolmanagementsystem.Triggers.InsertCourseTrigger";

CREATE TRIGGER course_update BEFORE UPDATE ON school.course 
    FOR EACH ROW CALL "com.github.jackmilless.schoolmanagementsystem.Triggers.UpdateCourseTrigger";


-- check that inserted student_course includes an available course hour for student and course,
-- and that classroom it takes place in is not at capacity

CREATE TRIGGER sc_insert BEFORE INSERT ON school.student_course
    FOR EACH ROW CALL "com.github.jackmilless.schoolmanagementsystem.Triggers.InsertStudentCourseTrigger";


-- update student gpa every time a grade is inserted or updated

CREATE TRIGGER grade_insert AFTER INSERT ON school.student_course
    FOR EACH ROW CALL "com.github.jackmilless.schoolmanagementsystem.Triggers.InsertGradeTrigger";
    
CREATE TRIGGER grade_update AFTER UPDATE ON school.student_course
    FOR EACH ROW CALL "com.github.jackmilless.schoolmanagementsystem.Triggers.UpdateGradeTrigger";