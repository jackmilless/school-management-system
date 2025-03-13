<template id="home-page"> 
    <div class="menu">
        <h2 class="menu">Access, add, or modify database records</h2>
        <br>
        <table class="menu">
            <tr>
                <th class="menu" @click="getTeachers">Teachers</th>
                <th class="menu" @click="getClassrooms">Classrooms</th>
                <th class="menu" @click="getCourses">Courses</th>
                <th class="menu" @click="getStudents">Students</th>
            </tr>
        </table>
    </div>
    <div class="footer">
        <button class="stop-button" @click="stopServer">Stop Server</button>
    </div>
</template>
<script>
    app.component("home-page", {
        template: "#home-page",
        methods: {
            getTeachers() {
                location.href="/teachers";
            },
            getClassrooms() {
                location.href="/classrooms";
            },
            getCourses() {
                location.href="/courses";
            },
            getStudents() {
                location.href="/students";
            },
            stopServer() {
                fetch("/api/stop")
            }
        }
    })
</script>
<style>
    div.menu {
        position: absolute;
        top: 43%;
        left: 50%;
        transform: translate(-50%, -50%);
        background-color: lightblue;
        border: 2px solid darkslategray;
        padding-bottom: 50px;
        padding-top: 50px;
        padding-left: 30px;
        padding-right: 30px;
    }
    h2.menu {
        font-family: Verdana;
        text-align: center;
        margin: auto;
        padding-bottom: 20px
    }
    table.menu, th.menu {
        border-collapse: collapse;
        margin: auto;
        border: 2px solid darkslategray;
        padding: 10px;
        background-color: lightcyan; 
        font-family: Verdana;
        font-size: large
    }
    table.menu th:hover {
        background-color: lightseagreen
    }

    .footer {
        position: fixed;
        bottom: 0px;
        left: 0px;
        width: 180px;
        height: 62px;
        border: 4px solid rgb(0, 55, 0); 
        background-color: azure
    }
    .stop-button {
        font-size: x-large;
        position: relative;
        margin: auto;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
    }
    button, select { background-color: rgb(75, 255, 246); border-color: rgb(75, 255, 246); padding: 3px }
    button:hover, select:hover { background-color: rgb(181, 255, 251); border-color: rgb(181, 255, 251) }
</style>