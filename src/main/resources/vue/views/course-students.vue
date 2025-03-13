<template id="course-students">
    <div v-if="loading"></div>
    <div v-else class="parent">
        <div class="table">
            <div class="menu">
                <div class="menu-left">
                    <label for="amount-select">Records per page: </label>
                    <select v-model="selected" id="amount-select">
                        <option @click="setLimit(10)">10</option>
                        <option @click="setLimit(50)">50</option>
                    </select> 
                </div>
                <div class="menu-right">
                    <button @click="left"><</button>
                    <button @click="right">></button>
                    <button @click="reset">Reset</button>
                </div>
            </div>
            <br>
            <table>
                <thead>
                    <tr>
                        <th @click="order('s.last_name')">Last Name{{sorted('s.last_name')}}</th>
                        <th @click="order('s.first_name')">First Name{{sorted('s.first_name')}}</th>
                        <th @click="order('s.email_address')">Email Address{{sorted('s.email_address')}}</th>
                        <th @click="order('s.grade_level')">Grade Level{{sorted('s.grade_level')}}</th>
                        <th @click="order('s.gpa')">Gpa{{sorted('s.gpa')}}</th>
                        <th @click="order('sc.grade')">Course Grade{{sorted('sc.grade')}}</th>
                        <th @click="order('sc.grade_percentage')">Course Percentage{{sorted('sc.grade_percentage')}}</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="s in courseStudents" :key="s.student.studentId" @click="getStudentCourse(`${s.student.studentId}`)">
                        <td>{{s.student.lastName}}</td>
                        <td>{{s.student.firstName}}</td> 
                        <td>{{s.student.emailAddress}}</td>
                        <td>{{s.student.gradeLevel}}</td>
                        <td>{{s.student.gpa}}</td>
                        <td>{{s.studentCourse.grade}}</td>
                        <td>{{s.studentCourse.gradePercentage}}</td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="search">
            <div class="search-padding">
                <form @submit.prevent="findByGrade">
                    <div class="input-padding">
                        <label for="min">Minimum Grade: </label>   
                        <input class="grade" :style="{'background-color': color.findByGrade}" pattern="[A-DF]" title="single capitalized character from A-D or F" v-model="minGrade" maxlength="1" placeholder="F" id="min" required>
                        <label for="max"> Maximum Grade: </label>
                        <input class="grade" :style="{'background-color': color.findByGrade}" pattern="[A-DF]" title="single capitalized character from A-D or F" v-model="maxGrade" maxlength="1" placeholder="A" id="max" required>
                    </div>
                    <button type="submit">Find By Grade</button>
                </form>
            </div>
        </div>
        <div class="add">
            <form @submit.prevent="addStudentCourse">
                <label for="sid">Student ID: </label>
                <input class="id" :style="{'background-color': color.addRecord}" type="number" min="1" title="ID of existing student not already present in course" v-model="studentCourse.studentId" placeholder="1" id="sid" required>
                <label for="cp"> Course Hour: </label>
                <input :style="{'background-color': color.addRecord}" type="number" min="1" max="6" title="course hour 1-6 valid for course that is not already present in given student's courses" v-model="studentCourse.courseHour" placeholder="1" id="cp" required>
                <br><br>
                <label for="cg">Course Grade: </label>
                <input class="grade" :style="{'background-color': color.addRecord}" pattern="[A-DF]" title="single capitalized character from A-D or F" v-model="studentCourse.grade" maxlength="1" placeholder="A" id="cg">
                <label for="gp"> Grade Percentage: </label>
                <input class="grade-percentage" :style="{'background-color': color.addRecord}" maxlength="6" pattern="(\d{0,2}\.\d{1,2})|(\d{1,2}\.?)|(100\.\d{0,2})|(100)" title="a number representing a percentage between 0.00 and 100.00, with up to 2 optional decimal places" v-model="studentCourse.gradePercentage" placeholder="95.00" id="gp">
                <br><br><br>
                <button class="submit-record" type="submit">Add Student to Course</button>
            </form>
        </div>
    </div>
    <div class="footer">
        <button class="home-button" @click="getHomePage">Home</button>
    </div>
</template>
<script>
    app.component("course-students", {
        template: "#course-students",
        data() {
            return {
                loading: true,
                courseStudents: [],
                asc: {"s.last_name":true, "s.first_name":true, "s.email_address":true, "s.grade_level":true, "s.gpa":true, "sc.grade":true, "sc.grade_percentage":true},
                orderBy: "s.last_name",
                selected: 10,
                limit: 10,
                offset: 0,
                minGrade: null,
                maxGrade: null,
                studentCourse: {"studentId":null, "grade":null, "gradePercentage":null,  "courseHour":null},
                color: {"addRecord":'white', "findByGrade":'white'},
                courseId: null
            };
        },
        async created() {
            await this.init();
            this.loading = false;
        },
        methods: {
            // send request to api to initialize page
            async init() {
                this.courseId = this.$javalin.pathParams["course-id"];
                const response = await fetch(`/api/courses/${this.courseId}/students`);
                this.courseStudents = await response.json();
            },
            // reset table and search fields to initial state (leave submission fields unaffected)
            async reset() {
                this.courseStudents = [];
                this.asc = {"s.last_name":true, "s.first_name":false, "s.email_address":false, "s.grade_level":false, "s.gpa":false, "sc.grade":false, "sc.grade_percentage":false};
                this.orderBy = "s.last_name";
                this.selected = 10;
                this.limit = 10;
                this.offset = 0;
                this.minGrade = null;
                this.maxGrade = null;
                this.courseId = null;
                this.color.findByGrade = 'white';
                this.init();
            },
            // primary method used to make get requests to api
            // uses existing state based on search input parameters, ultimately called by findBy methods
            // to determine which query parameters should be passed
            async update(updateData) {
                let url;
    
                const orderBy = this.orderBy;
                const asc = this.asc[orderBy];
                const limit = this.limit;
                const offset = this.offset;

                if(this.minGrade != null && this.maxGrade != null) {
                    const min = this.minGrade;
                    const max = this.maxGrade;
                    url = `/api/courses/${this.courseId}/students?orderby=${orderBy}&ascending=${asc}&limit=${limit}&offset=${offset}&mingrade=${min}&maxgrade=${max}`;
                } else {
                    url = `/api/courses/${this.courseId}/students?orderby=${orderBy}&ascending=${asc}&limit=${limit}&offset=${offset}`;
                }
                
                const response = await fetch(url);
                const jsonResponse = await response.json();
                if(updateData) {
                    this.courseStudents = jsonResponse;
                } else {
                    return jsonResponse;
                }
            },
            async order(orderBy) {
                this.orderBy = orderBy;
                this.asc[orderBy] = !this.asc[orderBy];
                this.update(true);
            },
            // uses orderBy + asc to place sorted arrow in correct column and direction
            sorted(orderBy) {
                if(this.orderBy == orderBy) {
                    if(this.asc[orderBy]) {
                        return ' \u25b4';
                    } else {
                        return ' \u25be';
                    }
                } else {
                    return '';
                }
            },
            async setLimit(amount) {
                this.offset = 0;
                this.limit = amount;
                this.update(true);
            },
            async left() {
                if(this.offset > 0) {
                    this.offset -= this.limit;
                    this.update(true);
                }
            },
            async right() {
                this.offset += this.limit;
                const jsonResponse = await this.update(false);

                if(jsonResponse.length != 0) {
                    this.courseStudents = jsonResponse;
                } else {
                    this.offset -= this.limit;
                }
            },
            // findBy methods: filter out input in other fields before calling update
            // color inputs to indicate success
            async findByGrade() {
                await this.update(true);
                this.color.findByGrade = 'greenyellow';
                setTimeout(() => {
                    this.color.findByGrade = 'white';
                }, 1000);
            },
            // sends post request to api
            // colors inputs green on success or red on database error,
            // sending user an alert with message indicating error 
            async addStudentCourse() {
                const request = {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(this.studentCourse)
                };
                const response = await fetch(`/api/courses/${this.courseId}/students`, request);

                if(response.status==200) {
                    this.color.addRecord = 'greenyellow';
                    setTimeout(() => {
                        this.color.addRecord = 'white';
                    }, 1000);
                    this.studentCourse = {"studentId":null, "grade":null, "gradePercentage":null,  "courseHour":null};
                } else if(response.status==400) {
                    const errorResponse = await response.json();
                    this.color.addRecord = 'red';
                    if(errorResponse.includes('foreign_key_student_id')) {
                        alert("Student id not present in database");
                    }
                    if(errorResponse.includes('PRIMARY_KEY_B')) {
                        alert("Student already present in course");
                    }
                    if(errorResponse.includes('hour invalid')) {
                        alert("Course hour unavailable for this course");
                    }
                    if(errorResponse.includes('full capacity')) {
                        alert("Classroom with this course is at full capacity for this hour");
                    }
                    if(errorResponse.includes('hour already exists')) {
                        alert("Course hour already filled for student");
                    }
                }
            },
            getStudentCourse(studentId) {
                location.href = `/courses/${this.courseId}/students/${studentId}`;
            },
            getHomePage() {
                location.href = "/";
            }
        }
    })
</script>
<style>
    * { font-family: Verdana; font-size: small }

    .parent { 
        display: grid; 
        grid-template-columns: 1fr, 1fr; 
    }
    .table { 
        position: relative;
        grid-column-start: 1;
        grid-column-end: 2;
        grid-row-start: 1;
        grid-row-end: 3;
        margin: auto;
        height: 380px;
        width: 880px;
        overflow: scroll;
        border: 4px solid rgb(0, 55, 0);  
        padding: 20px;
        background-color: azure
    }
    .search {
        grid-column-start: 2;
        grid-column-end: 3;
        grid-row-start: 1;
        grid-row-end: 2;
        margin: auto;
        border: 4px solid rgb(0, 55, 0); 
        padding: 20px;
        background-color: azure
    }
    .add {
        position: relative;
        grid-column-start: 2;
        grid-column-end: 3;
        grid-row-start: 2;
        grid-row-end: 3;
        margin: auto;
        border: 4px solid rgb(0, 55, 0); 
        padding: 20px;
        background-color: azure
    }

    table { border-collapse: collapse; margin: auto }
    table thead { background-color: lightsteelblue }
    table tbody { background-color: lightcyan }
    table thead th:hover { background-color: rgb(230, 241, 255) }
    table tbody tr:hover { background-color: lightseagreen }
    table, tr, th, td { border: 2px solid darkslategray; padding: 6px }
    button, select { background-color: rgb(75, 255, 246); border-color: rgb(75, 255, 246); padding: 3px }
    button:hover, select:hover { background-color: rgb(181, 255, 251); border-color: rgb(181, 255, 251) }

    .id { max-width: 50px }
    .grade { max-width: 10px }
    .grade-percentage { max-width: 50px }

    .menu { padding-top: 10px; padding-bottom: 5px }
    .menu-left { position: absolute; top: 10px; left: 10px }
    .menu-right { position: absolute; top: 10px; right: 10px }
    .submit-record { position: absolute; bottom: 10px; left: 10px }
    .search-padding { padding: 8px; border:2px solid darkslategray }
    .input-padding { padding: 10px }

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