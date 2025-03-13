<template id="course">
    <div v-if="loading"></div>
    <div v-else class="parent">
        <div>
            <table>
                <thead>
                    <tr>
                        <th>Course ID</th>
                        <th>Teacher ID</th>
                        <th>Classroom ID</th>
                        <th>Course Title</th>
                        <th>Course Code</th>
                        <th>Subject</th>
                        <th>Course Hours</th>
                        <th>Students</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>
                            {{course.courseId}}
                        </td>
                        <td>
                            <input class="id" form="updateForm" :style="{'background-color': color}" type="number" title="ID of existing teacher with available course hours" min="1" v-model="course.teacherId" required>
                            <button @click = "getTeacher(`${course.teacherId}`)">Go To</button>
                        </td>
                        <td>
                            <input class="id" form="updateForm" :style="{'background-color': color}" type="number" title="ID of existing classroom with available course hours" min="1" v-model="course.classroomId" required>
                            <button @click = "getClassroom(`${course.classroomId}`)">Go To</button>
                        </td>
                        <td>
                            <input form="updateForm" :style="{'background-color': color}" maxlength="98" title="unique string describing course" v-model="course.courseName" required>
                        </td>
                        <td>
                            <input class="code" form="updateForm" :style="{'background-color': color}" maxlength="8" pattern="\w{1,8}" title="unique alphanumeric code up to 8 characters long" v-model="course.courseCode" required>
                        </td> 
                        <td>
                            <input class="subject" form="updateForm" :style="{'background-color': color}" maxlength="28" v-model="course.subjectName" required>
                        </td> 
                        <td>
                            <label for="one">First</label>
                            <input form="updateForm" type="checkbox" v-model="hoursObj.first" id="one">
                            <label for="two">Second</label>
                            <input form="updateForm" type="checkbox" v-model="hoursObj.second" id="two"><br>
                            <label for="three">Third</label>
                            <input form="updateForm" type="checkbox" v-model="hoursObj.third" id="three">
                            <label for="four">Fourth</label>
                            <input form="updateForm" type="checkbox" v-model="hoursObj.fourth" id="four"><br>
                            <label for="five">Fifth</label>
                            <input form="updateForm" type="checkbox" v-model="hoursObj.fifth" id="five">
                            <label for="six">Sixth</label>
                            <input form="updateForm" type="checkbox" v-model="hoursObj.sixth" id="six">
                        </td> 
                        <td>
                            <button @click="getCourseStudents">See<br>Students<br>In Course</button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <br>
        <div>
            <table>
                <thead>
                    <th colspan="3">Modify Record</th>
                </thead>
                <tbody>
                    <td>
                        <form @submit.prevent="updateCourse" id="updateForm">
                            <button type="submit" :disabled="isHoursObjEmpty">Update Course</button>
                        </form>
                    </td>
                    <td>
                        <button @click="reset">Reset Unsubmitted<br>Changes</button>
                    </td>
                    <td>
                        <button @click="deleteCourse">Delete Course</button>
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
    app.component("course", {
        template:"#course",
        data() {
            return {
                loading: true,
                course: null,
                color: 'white',
                courseId: null,
                hoursObj: {"first":false, "second":false, "third":false, "fourth":false, "fifth":false, "sixth":false}
            }
        },
        computed: {
            hours() {
                hours = "";
                o = this.hoursObj;
                if(o.first) { hours += "1"; } else { hours += "0"; }
                if(o.second) { hours += "1"; } else { hours += "0"; }
                if(o.third) { hours += "1"; } else { hours += "0"; }
                if(o.fourth) { hours += "1"; } else { hours += "0"; }
                if(o.fifth) { hours += "1"; } else { hours += "0"; }
                if(o.sixth) { hours += "1"; } else { hours += "0"; }
                if(hours == "000000") {
                    return null;
                } else {
                    return hours;
                }
            },
            isHoursObjEmpty() {
                const h = this.hoursObj;
                return !(h.first || h.second || h.third || h.fourth || h.fifth || h.sixth);
            }
        },
        async created() {
            await this.init();
            this.loading = false;
        },
        methods: {
            // send request to api to initialize page
            async init() {
                this.courseId = this.$javalin.pathParams["course-id"];
                const response = await fetch(`/api/courses/${this.courseId}`);
                this.course = await response.json();
                this.setHours();
            },
            // reset input values to last persisted state in database
            async reset() {
                this.init();
                this.color = 'white';
            },
            async setHours() {
                hours = this.course.courseHours;
                if(hours[0] == "1") { this.hoursObj.first = true; } else { this.hoursObj.first = false; }
                if(hours[1] == "1") { this.hoursObj.second = true; } else { this.hoursObj.second = false; }
                if(hours[2] == "1") { this.hoursObj.third = true; } else { this.hoursObj.third = false; }
                if(hours[3] == "1") { this.hoursObj.fourth = true; } else { this.hoursObj.fourth = false; }
                if(hours[4] == "1") { this.hoursObj.fifth = true; } else { this.hoursObj.fifth = false; }
                if(hours[5] == "1") { this.hoursObj.sixth = true; } else { this.hoursObj.sixth = false; }
            },
            // sends put request to api
            // colors inputs green on success or red on database error,
            // sending user an alert with message indicating error 
            async updateCourse() {
                this.course.courseHours = this.hours;

                const request = {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(this.course)
                };
                const response = await fetch(`/api/courses/${this.courseId}`, request);

                if(response.status==200) {
                    this.course = await response.json();
                    this.setHours();
                    this.color = 'greenyellow';
                    setTimeout(() => {
                        this.color = 'white';
                    }, 1000);
                } else if(response.status==400) {
                    const errorResponse = await response.json();
                    this.color = 'red';
                    if(errorResponse.includes('foreign_key_teacher_id')) {
                        alert("Teacher id not present in database");
                    }
                    if(errorResponse.includes('foreign_key_classroom_id')) {
                        alert("Classroom id not present in database");
                    }
                    if(errorResponse.includes('course_name_unique')) {
                        alert("Course name already present in database");
                    }
                    if(errorResponse.includes('course_code_unique')) {
                        alert("Course code already present in database");
                    }
                    if(errorResponse.includes("taught by given teacher")) {
                        alert("Course hours overlap with other course taught by teacher");
                    }
                    if(errorResponse.includes("taught in given classroom")) {
                        alert("Course hours overlap with other course taught in classroom");
                    }
                }
            },
            // sends delete request to api
            // returns to previous page on success
            // indicates error if teacher not found in database
            // sends alert if course can't be deleted
            async deleteCourse() {
                const response = await fetch(`/api/courses/${this.courseId}`, { method: "DELETE" });

                if(response.status!=400) {
                    const jsonResponse = await response.json();
                    if(jsonResponse != null) {
                        location.href = "/courses";
                    } else {
                        alert("Course not present in database");
                    }
                } else if(response.status==400) {
                    const errorResponse = await response.json();
                    this.color = 'red';
                    if(errorResponse.includes('foreign_key_course_id')) {
                        alert("Can't delete course while students are still taking it");
                    }
                }
            },
            getTeacher(teacherId) {
                location.href = `/teachers/${teacherId}`;
            },
            getClassroom(classroomId) {
                location.href = `/classrooms/${classroomId}`;
            },
            getCourseStudents() {
                location.href = `/courses/${this.courseId}/students`
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
    .id { max-width: 50px }
    .code { max-width: 70px }
    .subject { max-width: 130px }

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