<template id="attendance-records">
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
                        <th @click="order('attendance_date')">Date{{sorted('attendance_date')}}</th>
                        <th>Present</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="record in attendanceRecords" :key="record.attendanceId" @click="getAttendanceRecord(`${record.attendanceId}`)">
                        <td>{{record.attendanceDate}}</td>
                        <td> {{presentStr(record.present)}}</td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="search">
            <div class="search-padding">
                <form @submit.prevent="findByDate">
                    <div class="input-padding">
                        <label for="mindate">Earliest Date: </label>   
                        <input :style="{'background-color': color.findByDate}" type="date" v-model="minDate" id="mindate" required>
                        <label for="maxdate"> Latest Date: </label>   
                        <input :style="{'background-color': color.findByDate}" type="date" v-model="maxDate" id="maxdate" required>
                    </div>
                    <button type="submit">Find By Date</button>
                </form>
            </div>
        </div>
        <div class="add">
            <form @submit.prevent="addAttendanceRecord">
                <label for="date">Date: </label>   
                <input ref="record" :style="{'background-color': color.addRecord}" type="date" @change="validAttendanceRecord" v-model="attendanceRecord.attendanceDate" id="date" required>
                <label for="pres"> Present: </label>   
                <input type="checkbox" v-model="attendanceRecord.present" id="pres">
                <br><br><br>
                <button class="submit-record" type="submit">Add Attendance Record</button>
            </form>
        </div>
    </div>
    <div class="footer">
        <button class="home-button" @click="getHomePage">Home</button>
    </div>
</template>
<script>
    app.component("attendance-records", {
        template: "#attendance-records",
        data() {
            return {
                loading: true,
                attendanceRecords: [],
                asc: {"attendance_date":false},
                orderBy: "attendance_date",
                selected: 10,
                limit: 10,
                offset: 0,
                minDate: null,
                maxDate: null,
                attendanceRecord: {"attendanceDate":null, "present":null},
                color: {"addRecord":'white', "findByDate":'white'},
                studentId: null,
                courseId: null
            };
        },
        async created() {
            await this.init();
            this.loading = false;
        },
        computed: {
            // ensure submitted attendance record falls on a weekday
            async validAttendanceRecord() {
                const date = new Date(await this.attendanceRecord.attendanceDate);
                const validDays = [0, 1, 2, 3, 4];
                if(date instanceof Date && validDays.includes(date.getDay())) {
                    await this.$refs.record.setCustomValidity("");
                    return true;
                } else {
                    await this.$refs.record.setCustomValidity("Date must be a weekday");
                    return false;
                }
            }
        },
        methods: {
            // send request to api to initialize page
            async init() {
                this.studentId = this.$javalin.pathParams["student-id"];
                this.courseId = this.$javalin.pathParams["course-id"];
                const response = await fetch(`/api/students/${this.studentId}/courses/${this.courseId}/attendance-records`);
                this.attendanceRecords = await response.json();
            },
            // reset table and search fields to initial state (leave submission fields unaffected)
            async reset() {
                this.attendanceRecords = [];
                this.asc = {"attendance_date":false};
                this.orderBy = "attendance_date";
                this.selected = 10;
                this.limit = 10;
                this.offset = 0;
                this.minDate = null;
                this.maxDate = null;
                this.studentId = null;
                this.courseId = null;
                this.color.findByDate = 'white';
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

                if(this.minDate != null && this.maxDate != null) {
                    const min = this.minDate;
                    const max = this.maxDate;
                    url = `/api/students/${this.studentId}/courses/${this.courseId}/attendance-records?orderby=${orderBy}&ascending=${asc}&limit=${limit}&offset=${offset}&mindate=${min}&maxdate=${max}`;
                } else {
                    url = `/api/students/${this.studentId}/courses/${this.courseId}/attendance-records?orderby=${orderBy}&ascending=${asc}&limit=${limit}&offset=${offset}`;
                }

                const response = await fetch(url);
                const jsonResponse = await response.json();
                if(updateData) {
                    this.attendanceRecords = jsonResponse;
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
                    this.attendanceRecords = jsonResponse;
                } else {
                    this.offset -= this.limit;
                }
            },
            // findBy methods: filter out input in other fields before calling update
            // color inputs to indicate success
            async findByDate() {
                await this.update(true);
                this.color.findByDate = 'greenyellow';
                setTimeout(() => {
                    this.color.findByDate = 'white';
                }, 1000);
            },
            // sends post request to api
            // colors inputs green on success or red on database error,
            // sending user an alert with message indicating error 
            async addAttendanceRecord() {
                if(this.validAttendanceRecord) {
                    const request = {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify(this.attendanceRecord)
                    };
                    const response = await fetch(`/api/students/${this.studentId}/courses/${this.courseId}/attendance-records`, request);

                    if(response.status==200) {
                        this.color.addRecord = 'greenyellow';
                        setTimeout(() => {
                            this.color.addRecord = 'white';
                        }, 1000);
                        this.attendanceRecord = {"attendanceDate":null, "present":null};
                    } else if(response.status==400) {
                        const errorResponse = await response.json();
                        this.color.addRecord = 'red';
                        if(errorResponse.includes('student_course_date_unique')) {
                            alert("Attendance record for this student, course, and date already present in database");
                        }
                    }
                }
            },
            presentStr(present) {
                if(present) {
                    return "Present";
                } else {
                    return "Absent";
                }
            },
            getAttendanceRecord(attendanceId) {
                location.href = `/students/${this.studentId}/courses/${this.courseId}/attendance-records/${attendanceId}`;
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
        width: 300px;
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