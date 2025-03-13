<template id="courses">
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
                        <th @click="order('course_name')">Course Title{{sorted('course_name')}}</th>
                        <th @click="order('course_code')">Course Code{{sorted('course_code')}}</th>
                        <th @click="order('subject_name')">Subject{{sorted('subject_name')}}</th>
                        <th>Course Hours</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="course in courses" :key="course.courseId" @click="getCourse(`${course.courseId}`)">
                        <td>{{course.courseName}}</td>
                        <td>{{course.courseCode}}</td> 
                        <td>{{course.subjectName}}</td> 
                        <td>{{hourLetters(course.courseHours)}}</td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="search">
            <div class="search-padding">
                <form @submit.prevent="findByName">
                    <div class="input-padding">
                        <label for="title">Course Title: </label>   
                        <input :style="{'background-color': color.findByName}" maxlength="98" v-model="courseName" placeholder="English I" id="title" required>
                    </div>
                    <button type="submit">Find By Title</button>
                </form>
            </div>
            <br>
            <div class="search-padding">
                <form @submit.prevent="findByCode">
                    <div class="input-padding">
                        <label for="code">Course Code: </label>   
                        <input class="code" :style="{'background-color': color.findByCode}" maxlength="8" pattern="\w{1,8}" title="alphanumeric code up to 8 characters long" v-model="courseCode" placeholder="E1001" id="code" required>
                    </div>
                    <button type="submit">Find By Code</button>
                </form>
            </div>
            <br>
            <div class="search-padding">
                <form @submit.prevent="findBySubject">
                    <div class="input-padding">
                        <label for="subj">Subject: </label>   
                        <input class="subject" :style="{'background-color': color.findBySubject}" maxlength="28" v-model="subjectName" placeholder="English" id="subj" required>
                    </div>
                    <button type="submit">Find By Subject</button>
                </form>
            </div>
            <br>
            <div class="search-padding">
                <form @submit.prevent="findByHours">
                    <div class="input-padding">
                        <label for="one">First</label>
                        <input class="selection" type="checkbox" v-model="hoursObj.first" id="one">
                        <label for="two"> Second</label>
                        <input class="selection" type="checkbox" v-model="hoursObj.second" id="two">
                        <label for="three"> Third</label>
                        <input class="selection" type="checkbox" v-model="hoursObj.third" id="three">
                        <label for="four"> Fourth</label>
                        <input class="selection" type="checkbox" v-model="hoursObj.fourth" id="four">
                        <label for="five"> Fifth</label>
                        <input class="selection" type="checkbox" v-model="hoursObj.fifth" id="five">
                        <label for="six"> Sixth</label>
                        <input class="selection" type="checkbox" v-model="hoursObj.sixth" id="six">
                    </div>
                    <button type="submit" :disabled="isHoursObjEmpty">Find By Course Hours</button>
                </form>
            </div>
        </div>
        <div class="add">
            <form @submit.prevent="addCourse">
                <label for="tid">Teacher ID: </label>   
                <input class="id" :style="{'background-color': color.addRecord}" type="number" min="1" v-model="course.teacherId" placeholder="1" id="tid" required>
                <label for="cid"> Classroom ID: </label>   
                <input class="id" :style="{'background-color': color.addRecord}" type="number" min="1" v-model="course.classroomId" placeholder="1" id="cid" required>
                <label for="titlea">Course Title: </label>   
                <input :style="{'background-color': color.addRecord}" maxlength="98" title="unique string describing course" v-model="course.courseName" placeholder="Advanced Calculus" id="titlea" required>
                <br><br>
                <label for="codea">Course Code: </label>   
                <input class="code" :style="{'background-color': color.addRecord}" maxlength="8" pattern="\w{1,8}" title="unique alphanumeric code up to 8 characters long" v-model="course.courseCode" placeholder="C2000" id="codea" required>
                <label for="subja"> Subject: </label>   
                <input class="subject" :style="{'background-color': color.addRecord}" maxlength="28" v-model="course.subjectName" placeholder="Mathematics" id="subja" required>
                <br>
                <p>Course Hours:
                    <label for="onea"> First</label>
                    <input type="checkbox" v-model="courseHoursObj.first" id="onea">
                    <label for="twoa"> Second</label>
                    <input type="checkbox" v-model="courseHoursObj.second" id="twoa">
                    <label for="threea"> Third</label>
                    <input type="checkbox" v-model="courseHoursObj.third" id="threea">
                    <label for="foura"> Fourth</label>
                    <input type="checkbox" v-model="courseHoursObj.fourth" id="foura">
                    <label for="fivea"> Fifth</label>
                    <input type="checkbox" v-model="courseHoursObj.fifth" id="fivea">
                    <label for="sixa"> Sixth</label>
                    <input type="checkbox" v-model="courseHoursObj.sixth" id="sixa">
                </p>
                <br>
                <button class="submit-record" type="submit" :disabled="isCourseHoursObjEmpty">Add Course to Records</button>
            </form>
        </div>
    </div>
    <div class="footer">
        <button class="home-button" @click="getHomePage">Home</button>
    </div>
</template>
<script>
    app.component("courses", {
        template: "#courses",
        data() {
            return {
                loading: true,
                courses: [],
                asc: {"course_name":true, "course_code":false, "subject_name":false},
                orderBy: "course_name",
                selected: 10,
                limit: 10,
                offset: 0,
                courseName: null,
                courseCode: null,
                subjectName: null,
                hoursObj: {"first":false, "second":false, "third":false, "fourth":false, "fifth":false, "sixth":false},
                course: {"teacherId":null, "classroomId":null, "courseName":null, "courseCode":null, "subjectName":null, "courseHours":null},
                courseHoursObj: {"first":false, "second":false, "third":false, "fourth":false, "fifth":false, "sixth":false},
                color: {"addRecord":'white', "findByName":'white', "findByCode":'white', "findBySubject":'white'}
            };
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
            courseHours() {
                hours = "";
                o = this.courseHoursObj;
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
            },
            isCourseHoursObjEmpty() {
                const h = this.courseHoursObj;
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
                const response = await fetch("/api/courses");
                this.courses = await response.json();
            },
            // reset table and search fields to initial state (leave submission fields unaffected)
            async reset() {
                this.courses = [];
                this.asc = {"course_name":true, "course_code":false, "subject_name":false};
                this.orderBy = "course_name";
                this.selected = 10;
                this.limit = 10;
                this.offset = 0;
                this.courseName = null;
                this.courseCode = null;
                this.subjectName = null;
                this.hoursObj = {"first":false, "second":false, "third":false, "fourth":false, "fifth":false, "sixth":false};
                this.color.findByName = 'white';
                this.color.findByCode = 'white';
                this.color.findBySubject = 'white';
                this.init();
            },
            // primary method used to make get requests to api
            // uses existing state based on search input parameters, ultimately called by findBy methods
            // to determine which query parameters should be passed
            async update(updateData) {
                let url;

                if(this.courseName != null) {
                    const cn = this.courseName;
                    url = `/api/courses?coursename=${cn}`;
                } else if(this.courseCode != null) {
                    const cc = this.courseCode;
                    url = `/api/courses?coursecode=${cc}`;
                } else {
                    const orderBy = this.orderBy;
                    const asc = this.asc[orderBy];
                    const limit = this.limit;
                    const offset = this.offset;

                    if(this.subjectName != null) {
                        const subj = this.subjectName;
                        url = `/api/courses?orderby=${orderBy}&ascending=${asc}&limit=${limit}&offset=${offset}&subjectname=${subj}`;
                    } else if(this.hours != null) {
                        const hs = this.hours;
                        url = `/api/courses?orderby=${orderBy}&ascending=${asc}&limit=${limit}&offset=${offset}&coursehours=${hs}`;
                    } else {
                        url = `/api/courses?orderby=${orderBy}&ascending=${asc}&limit=${limit}&offset=${offset}`;
                    }
                }

                const response = await fetch(url);
                const jsonResponse = await response.json();
                if(updateData) {
                    this.courses = jsonResponse;
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
                    this.courses = jsonResponse;
                } else {
                    this.offset -= this.limit;
                }
            },
            // findBy methods: filter out input in other fields before calling update
            // color inputs to indicate success
            async findByName() {
                this.courseCode = null;
                this.subjectName = null;
                this.hoursObj = {"first":false, "second":false, "third":false, "fourth":false, "fifth":false, "sixth":false};
                await this.update(true);
                this.color.findByName = 'greenyellow';
                setTimeout(() => {
                    this.color.findByName = 'white';
                }, 1000);
            },
            async findByCode() {
                this.courseName = null;
                this.subjectName = null;
                this.hoursObj = {"first":false, "second":false, "third":false, "fourth":false, "fifth":false, "sixth":false};
                await this.update(true);
                this.color.findByCode = 'greenyellow';
                setTimeout(() => {
                    this.color.findByCode = 'white';
                }, 1000);
            },
            async findBySubject() {
                this.courseName = null;
                this.courseCode = null;
                this.hoursObj = {"first":false, "second":false, "third":false, "fourth":false, "fifth":false, "sixth":false};
                await this.update(true);
                this.color.findBySubject = 'greenyellow';
                setTimeout(() => {
                    this.color.findBySubject = 'white';
                }, 1000);
            },
            async findByHours() {
                this.courseName = null;
                this.courseCode = null;
                this.subjectName = null;
                await this.update(true);
            },
            // sends post request to api
            // colors inputs green on success or red on database error,
            // sending user an alert with message indicating error 
            async addCourse() {
                this.course.courseHours = this.courseHours;

                const request = {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(this.course)
                };
                const response = await fetch("/api/courses", request);

                if(response.status==200) {
                    this.color.addRecord = 'greenyellow';
                    setTimeout(() => {
                        this.color.addRecord = 'white';
                    }, 1000);
                    this.course = {"teacherId":null, "classroomId":null, "courseName":null, "courseCode":null, "subjectName":null, "courseHours":null};
                    this.courseHoursObj = {"first":false, "second":false, "third":false, "fourth":false, "fifth":false, "sixth":false};
                } else if(response.status==400) {
                    const errorResponse = await response.json();
                    this.color.addRecord = 'red';
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
                        alert("Course hours overlap with other course taught by given teacher");
                    }
                    if(errorResponse.includes("taught in given classroom")) {
                        alert("Course hours overlap with other course taught in given classroom");
                    }
                }
            },
            hourLetters(hours) {
                ltrs  = "";
                if(hours[0] == "1") { ltrs += "1/"; } 
                if(hours[1] == "1") { ltrs += "2/"; }
                if(hours[2] == "1") { ltrs += "3/"; }
                if(hours[3] == "1") { ltrs += "4/"; }
                if(hours[4] == "1") { ltrs += "5/"; }
                if(hours[5] == "1") { ltrs += "6/"; }
                return ltrs.substring(0, ltrs.length-1);
            },
            getCourse(courseId) {
                location.href = `/courses/${courseId}`;
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
        grid-row-end: 2;
        margin: auto;
        height: 380px;
        width: 500px;
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
        grid-column-start: 1;
        grid-column-end: 3;
        grid-row-start: 2;
        grid-row-end: 3;
        margin: auto;
        margin-top: 50px;
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
    .code { max-width: 70px }
    .subject { max-width: 130px }

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