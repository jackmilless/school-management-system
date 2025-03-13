<template id="student-course">
    <div v-if="loading"></div>
    <div v-else class="parent">
        <div>
            <table>
                <thead>
                    <tr>    
                        <th>Student ID</th>
                        <th>Course ID</th>
                        <th>Student Name</th>
                        <th>Course Title</th>
                        <th>Grade</th>
                        <th>Grade Percentage</th>
                        <th>Course Hour</th>
                        <th>Attendance</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>
                            <button @click="getStudent">{{studentCourse.studentId}}</button>
                        </td>
                        <td>
                            <button @click="getCourse">{{studentCourse.courseId}}</button>
                        </td>
                        <td>
                            {{student.lastName}}, {{student.firstName}} 
                        </td>
                        <td>
                            {{course.courseName}}
                        </td>
                        <td>
                            <input class="grade" form="updateForm" :style="{'background-color': color}" pattern="[A-DF]" title="single capitalized character from A-D or F" v-model="studentCourse.grade" maxlength="1">
                        </td>
                        <td>
                            <input class="grade-percentage" form="updateForm" :style="{'background-color': color}" maxlength="6" pattern="(\d{0,2}\.\d{1,2})|(\d{1,2}\.?)|(100\.0{0,2})|(100)" title="a number representing a percentage between 0.00 and 100.00, with up to 2 optional decimal places" v-model="studentCourse.gradePercentage">
                        </td> 
                        <td>
                            {{studentCourse.courseHour}}
                        </td>
                        <td>
                            <button @click="getAttendanceRecords">See<br>Attendance<br>Records</button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <br>
        <div>
            <table>
                <thead>
                    <th colspan="2">Modify Record</th>
                </thead>
                <tbody>
                    <td>
                        <form @submit.prevent="updateStudentCourse" id="updateForm">
                            <button type="submit">Update Grade</button>
                        </form>
                    </td>
                    <td>
                        <button @click="reset">Reset Unsubmitted<br>Changes</button>
                    </td>
                </tbody>
            </table>
        </div>
    </div>
    <div class="footer">
        <button class="home-button" @click="getHomePage">Home</button>
    </div>
</template>
<script>
    app.component("student-course", {
        template:"#student-course",
        data() {
            return {
                loading: true,
                studentCourse: null,
                student: null,
                course: null,
                color: 'white',
                studentId: null,
                courseId: null
            }
        },
        async created() {
            await this.init();
            this.loading = false;
        },
        methods: {
            // send request to api to initialize page
            async init() {
                this.studentId = this.$javalin.pathParams["student-id"];
                this.courseId = this.$javalin.pathParams["course-id"];
                const studentCourseResponse = await fetch(`/api/students/${this.studentId}/courses/${this.courseId}`);
                const studentResponse = await fetch(`/api/students/${this.studentId}`);
                const courseResponse = await fetch(`/api/courses/${this.courseId}`);
                this.studentCourse = await studentCourseResponse.json();
                this.student = await studentResponse.json();
                this.course = await courseResponse.json();
            },
            // reset input values to last persisted state in database
            async reset() {
                this.init();
                this.color = 'white';
            },
            // sends put request to api
            // colors inputs green on success
            async updateStudentCourse() {
                const request = {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(this.studentCourse)
                };
                const response = await fetch(`/api/students/${this.studentId}/courses/${this.courseId}`, request);

                this.studentCourse = await response.json();
                this.color = 'greenyellow';
                setTimeout(() => {
                    this.color = 'white';
                }, 1000);
            },
            getAttendanceRecords() {
                location.href = `/students/${this.studentId}/courses/${this.courseId}/attendance-records`;
            },
            getStudent() {
                location.href = `/students/${this.studentId}`;
            },
            getCourse() {
                location.href = `/courses/${this.courseId}`;
            },
            getHomePage() {
                location.href = "/";
            }
        }
    })
</script>
<style>
    * { font-family: Verdana; font-size: 14px }

    div.parent {
        position: relative;
        left: 50%;
        transform: translate(-50%, 50%);
    }

    table { border-collapse: collapse; margin: auto }
    table thead { background-color: lightsteelblue }
    table tbody { background-color: lightcyan }
    table, tr, th, td { border: 2px solid darkslategray; padding: 10px; padding-top: 15px; padding-bottom: 15px; text-align: center}
    button, select { background-color: rgb(75, 255, 246); border-color: rgb(75, 255, 246); padding: 3px }
    button:hover, select:hover { background-color: rgb(181, 255, 251); border-color: rgb(181, 255, 251) }

    input { font-size: small }
    .grade { max-width: 10px }
    .grade-percentage { max-width: 50px }

    .footer {
        position: fixed;
        bottom: 0px;
        right: 0px;
        width: 110px;
        height: 62px;
        border: 4px solid rgb(0, 55, 0); 
        background-color: azure
    }
    .home-button {
        font-size: x-large;
        position: relative;
        margin: auto;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
    }
</style>