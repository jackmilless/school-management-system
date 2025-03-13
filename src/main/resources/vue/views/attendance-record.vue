<template id="attendance-record">
    <div v-if="loading"></div>
    <div v-else class="parent">
        <div>
            <table>
                <thead>
                    <tr>    
                        <th>Date</th>
                        <th>Present</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>
                            {{attendanceRecord.attendanceDate}}
                        </td>
                        <td :style="{'background-color': color}">
                            <input type="checkbox" v-model="attendanceRecord.present">
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
                        <form @submit.prevent="updateAttendanceRecord" id="updateForm">
                            <button type="submit">Update Attendance Record</button>
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
    app.component("attendance-record", {
        template:"#attendance-record",
        data() {
            return {
                loading: true,
                attendanceRecord: null,
                color: 'lightcyan',
                studentId: null,
                courseId: null,
                attendanceId: null
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
                this.attendanceId = this.$javalin.pathParams["attendance-id"];
                const response = await fetch(`/api/students/${this.studentId}/courses/${this.courseId}/attendance-records/${this.attendanceId}`);
                this.attendanceRecord = await response.json();
            },
            // reset input values to last persisted state in database
            async reset() {
                this.init();
                this.color = 'lightcyan';
            },
            // sends put request to api
            // colors inputs green on success
            async updateAttendanceRecord() {
                const request = {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(this.attendanceRecord)
                };
                const response = await fetch(`/api/students/${this.studentId}/courses/${this.courseId}/attendance-records/${this.attendanceId}`, request);
                    
                this.attendanceRecord = await response.json();
                this.color = 'greenyellow';
                setTimeout(() => {
                    this.color = 'lightcyan';
                }, 1000);
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
    table, tr, th, td { border: 2px solid darkslategray; padding: 10px; padding-top: 15px; padding-bottom: 15px; text-align: center }
    button, select { background-color: rgb(75, 255, 246); border-color: rgb(75, 255, 246); padding: 3px }
    button:hover, select:hover { background-color: rgb(181, 255, 251); border-color: rgb(181, 255, 251) }

    input { font-size: small }

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