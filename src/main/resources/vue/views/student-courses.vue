<template id="student-courses">
    <div v-if="loading"></div>
    <div v-else class="table">
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
                    <th @click="order('c.course_name')">Course Title{{sorted('c.course_name')}}</th>
                    <th @click="order('c.course_code')">Course Code{{sorted('c.course_code')}}</th>
                    <th @click="order('c.subject_name')">Subject{{sorted('c.subject_name')}}</th>
                    <th>Course Hour</th>
                    <th @click="order('sc.grade')">Course Grade{{sorted('sc.grade')}}</th>
                    <th @click="order('sc.grade_percentage')">Course Percentage{{sorted('sc.grade_percentage')}}</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="c in studentCourses" :key="c.course.courseId" @click="getStudentCourse(`${c.course.courseId}`)">
                    <td>{{c.course.courseName}}</td>
                    <td>{{c.course.courseCode}}</td> 
                    <td>{{c.course.subjectName}}</td> 
                    <td>{{c.studentCourse.courseHour}}</td>
                    <td>{{c.studentCourse.grade}}</td>
                    <td>{{c.studentCourse.gradePercentage}}</td>
                </tr>
            </tbody>
        </table>
    </div>
    <div class="footer">
        <button class="home-button" @click="getHomePage">Home</button>
    </div>
</template>
<script>
    app.component("student-courses", {
        template: "#student-courses",
        data() {
            return {
                loading: true,
                studentCourses: [],
                asc: {"c.course_name":true, "c.course_code":false, "c.subject_name":false, "sc.grade":false, "sc.grade_percentage":false},
                orderBy: "c.course_name",
                selected: 10,
                limit: 10,
                offset: 0,
                studentId: null
            };
        },
        async created() {
            await this.init();
            this.loading = false;
        },
        methods: {
            // send request to api to initialize page
            async init() {
                this.studentId = this.$javalin.pathParams["student-id"];
                const response = await fetch(`/api/students/${this.studentId}/courses`);
                this.studentCourses = await response.json();
            },
            // reset table and search fields to initial state (leave submission fields unaffected)
            async reset() {
                this.studentCourses = [];
                this.asc = {"c.course_name":true, "c.course_code":false, "c.subject_name":false, "sc.grade":false, "sc.grade_percentage":false};
                this.orderBy = "c.course_name";
                this.selected = 10;
                this.limit = 10;
                this.offset = 0;
                this.studentId = null;
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
                url = `/api/students/${this.studentId}/courses?orderby=${orderBy}&ascending=${asc}&limit=${limit}&offset=${offset}`;

                const response = await fetch(url);
                const jsonResponse = await response.json();
                if(updateData) {
                    this.studentCourses = jsonResponse;
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
                    this.studentCourses = jsonResponse;
                } else {
                    this.offset -= this.limit;
                }
            },
            getStudentCourse(courseId) {
                location.href = `/students/${this.studentId}/courses/${courseId}`;
            },
            getHomePage() {
                location.href = "/";
            }
        }
    })
</script>
<style>
    * { font-family: Verdana; font-size: small }

    .table { 
        position: relative;
        margin: auto;
        height: 280px;
        width: 800px;
        overflow: scroll;
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

    .menu { padding-top: 10px; padding-bottom: 5px }
    .menu-left { position: absolute; top: 10px; left: 10px }
    .menu-right { position: absolute; top: 10px; right: 10px }

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