<template id="student">
    <div v-if="loading"></div>
    <div v-else class="parent">
        <div>
            <table>
                <thead>
                    <tr>    
                        <th>Student ID</th>
                        <th>Last Name</th>
                        <th>First Name</th>
                        <th>Email Address</th>
                        <th>Grade Level</th>
                        <th>GPA</th>
                        <th>Courses</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>
                            {{student.studentId}}
                        </td>
                        <td>
                            <input class="name" form="updateForm" :style="{'background-color': color}" maxlength="98" pattern="[A-Za-z]{1,98}" title="string of basic characters of the latin alphabet" v-model="student.lastName" required>
                        </td>
                        <td>
                            <input class="name" form="updateForm" :style="{'background-color': color}" maxlength="98" pattern="[A-Za-z]{1,98}" title="string of basic characters of the latin alphabet" v-model="student.firstName" required>
                        </td> 
                        <td>
                            <input form="updateForm" :style="{'background-color': color}" maxlength="98" type="email" pattern=".+@school\.org" title="any unique email ending in @school.org" v-model="student.emailAddress" required>
                        </td>
                        <td>
                            <input form="updateForm" :style="{'background-color': color}" type="number" min="9" max="12" v-model="student.gradeLevel" required>
                        </td>
                        <td>
                            {{student.gpa}}
                        </td>
                        <td>
                            <button @click="getStudentCourses">See<br>Student's<br>Courses</button>
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
                        <form @submit.prevent="updateStudent" id="updateForm">
                            <button type="submit">Update Student</button>
                        </form>
                    </td>
                    <td>
                        <button @click="reset">Reset Unsubmitted<br>Changes</button>
                    </td>
                    <td>
                        <button @click="deleteStudent">Delete Student</button>
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
    app.component("student", {
        template:"#student",
        data() {
            return {
                loading: true,
                student: null,
                color: 'white',
                studentId: null
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
                const response = await fetch(`/api/students/${this.studentId}`);
                this.student = await response.json();
            },
            // reset input values to last persisted state in database
            async reset() {
                this.init();
                this.color = 'white';
            },
            // sends put request to api
            // colors inputs green on success or red on database error,
            // sending user an alert with message indicating error 
            async updateStudent() {
                const request = {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(this.student)
                };
                const response = await fetch(`/api/students/${this.studentId}`, request);

                if(response.status==200) {
                    this.student = await response.json();
                    this.color = 'greenyellow';
                    setTimeout(() => {
                        this.color = 'white';
                    }, 1000);
                } else if(response.status==400) {
                    const errorResponse = await response.json();
                    this.color = 'red';
                    if(errorResponse.includes('student_email_unique')) {
                        alert("Email address already present in database");
                    }
                }
            },
            // sends delete request to api
            // returns to previous page on success
            // indicates error if teacher not found in database
            async deleteStudent() {
                const response = await fetch(`/api/students/${this.studentId}`, { method: "DELETE" });
                const jsonResponse = await response.json();
                if(jsonResponse != null) {
                    location.href = "/students";
                } else {
                    alert("Student not present in database");
                }
            },
            getStudentCourses() {
                location.href = `/students/${this.studentId}/courses`;
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
    table, tr, th, td { border: 2px solid darkslategray; padding: 10px; padding-top: 15px; padding-bottom: 15px; ; text-align: center}
    button, select { background-color: rgb(75, 255, 246); border-color: rgb(75, 255, 246); padding: 3px }
    button:hover, select:hover { background-color: rgb(181, 255, 251); border-color: rgb(181, 255, 251) }

    input { font-size: small }
    .name { max-width: 130px }

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