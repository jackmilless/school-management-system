<template id="classroom">
    <div v-if="loading"></div>
    <div v-else class="parent">
        <div>
            <table>
                <thead>
                    <tr>    
                        <th>Classroom ID</th>
                        <th>Classroom Name</th>
                        <th>Capacity</th>
                        <th>Courses</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>
                            {{classroom.classroomId}}
                        </td>
                        <td>
                            <input class="number" form="updateForm" :style="{'background-color': color}" maxlength="8" pattern="\w{1,8}" title="unique alphanumeric code up to 8 characters long" v-model="classroom.classroomNumber" required>
                        </td>
                        <td>
                            {{classroom.capacity}}
                        </td> 
                        <td>
                            <button @click="getCourses">See<br>Classroom's<br>Courses</button>
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
                        <form @submit.prevent="updateClassroom" id="updateForm">
                            <button type="submit">Update Classroom</button>
                        </form>
                    </td>
                    <td>
                        <button @click="reset">Reset Unsubmitted<br>Changes</button>
                    </td>
                    <td>
                        <button @click="deleteClassroom">Delete Classroom</button>
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
    app.component("classroom", {
        template:"#classroom",
        data() {
            return {
                loading: true,
                classroom: null,
                color: 'white',
                classroomId: null
            }
        },
        async created() {
            await this.init();
            this.loading = false;
        },
        methods: {
            // send request to api to initialize page
            async init() {
                this.classroomId = this.$javalin.pathParams["classroom-id"];
                const response = await fetch(`/api/classrooms/${this.classroomId}`);
                this.classroom = await response.json();
            },
            // reset input values to last persisted state in database
            async reset() {
                this.init();
                this.color = 'white';
            },
            // sends put request to api
            // colors inputs green on success or red on database error,
            // sending user an alert with message indicating error 
            async updateClassroom() {
                const request = {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(this.classroom)
                };
                const response = await fetch(`/api/classrooms/${this.classroomId}`, request);
                if(response.status==200) {
                    this.classroom = await response.json();
                    this.color = 'greenyellow';
                    setTimeout(() => {
                        this.color = 'white';
                    }, 1000);
                } else if(response.status==400) {
                    const errorResponse = await response.json();
                    this.color = 'red';
                    if(errorResponse.includes('classroom_number_unique')) {
                        alert("Classroom number already present in database");
                    }
                }
            },
            // sends delete request to api
            // returns to previous page on success
            // indicates error if teacher not found in database
            async deleteClassroom() {
                const response = await fetch(`/api/classrooms/${this.classroomId}`, { method: "DELETE" });
                const jsonResponse = await response.json();
                if(jsonResponse != null) {
                    location.href = "/classrooms";
                } else {
                    alert("Classroom not present in database");
                }
            },
            getCourses() {
                location.href = `/classrooms/${this.classroomId}/courses`;
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
    .number { max-width: 70px }

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