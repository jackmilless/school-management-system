<template id="classroom-courses">
    <div v-if="loading"></div>
    <div v-else class="table">
        <table>
            <thead>
                <tr>
                    <th>Course Title</th>
                    <th>Course Code</th>
                    <th>Subject</th>
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
    <div class="footer">
        <button class="home-button" @click="getHomePage">Home</button>
    </div>
</template>
<script>
    app.component("classroom-courses", {
        template: "#classroom-courses",
        data() {
            return {
                loading: true,
                courses: [],
                classroomId: null
            };
        },
        async created() {
            await this.init();
            this.loading = false;
        },
        methods: {
            // send request to api to initialize page
            async init() {
                this.classroomId = this.$javalin.pathParams["classroom-id"];
                const response = await fetch(`/api/classrooms/${this.classroomId}/courses`);
                this.courses = await response.json();
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
    * { font-family: Verdana; font-size: 14px}

    .table { 
        margin: auto;
        width: 500px;
        border: 4px solid rgb(0, 55, 0);  
        padding: 20px;
        background-color: azure
    }

    table { border-collapse: collapse; margin: auto }
    table thead { background-color: lightsteelblue }
    table tbody { background-color: lightcyan }
    table tbody tr:hover { background-color: lightseagreen }
    table, tr, th, td { border: 2px solid darkslategray; padding: 6px }
    button, select { background-color: rgb(75, 255, 246); border-color: rgb(75, 255, 246); padding: 3px }
    button:hover, select:hover { background-color: rgb(181, 255, 251); border-color: rgb(181, 255, 251) }

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