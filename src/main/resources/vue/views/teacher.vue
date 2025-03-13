<template id="teacher">
    <div v-if="loading"></div>
    <div v-else class="parent">
        <div>
            <table>
                <thead>
                    <tr>    
                        <th>Teacher ID</th>
                        <th>Last Name</th>
                        <th>First Name</th>
                        <th>Email Address</th>
                        <th>Phone Number</th>
                        <th>Salary</th>
                        <th>Hire Date</th>
                        <th>Courses</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>
                            {{teacher.teacherId}}
                        </td>
                        <td>
                            <input class="name" form="updateForm" :style="{'background-color': color}" maxlength="98" pattern="[A-Za-z]{1,98}" title="string of basic characters of the latin alphabet"v-model="teacher.lastName" required>
                        </td>
                        <td>
                            <input class="name" form="updateForm" :style="{'background-color': color}" maxlength="98" pattern="[A-Za-z]{1,98}" title="string of basic characters of the latin alphabet" v-model="teacher.firstName" required>
                        </td> 
                        <td>
                            <input form="updateForm" :style="{'background-color': color}" maxlength="98" type="email" pattern=".+@school\.org" title="any unique email ending in @school.org" v-model="teacher.emailAddress" required>
                        </td>
                        <td>
                            <input class="phone-number" form="updateForm" :style="{'background-color': color}" maxlength="14" type="tel" pattern="\(\d{3}\)-\d{3}-\d{4}" title="any unique phone number in the format (###)-###-####" v-model="teacher.phoneNumber" required>
                        </td>
                        <td>
                            <input class="salary" form="updateForm" :style="{'background-color': color}" maxlength="11" pattern="(\d{1,8}\.\d{0,2})|(\d{1,8})?" title="a number with up to 8 digits before the decimal point and an optional decimal point with up to 2 decimal places" v-model="teacher.salary" required>
                        </td>
                        <td>
                            {{teacher.hireDate}}
                        </td>
                        <td>
                            <button @click="getCourses">See<br>Teacher's<br>Courses</button>
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
                        <form @submit.prevent="updateTeacher" id="updateForm">
                            <button type="submit">Update Teacher</button>
                        </form>
                    </td>
                    <td>
                        <button @click="reset">Reset Unsubmitted<br>Changes</button>
                    </td>
                    <td>
                        <button @click="deleteTeacher">Delete Teacher</button>
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
    app.component("teacher", {
        template:"#teacher",
        data() {
            return {
                loading: true,
                teacher: null,
                color: 'white',
                teacherId: null
            }
        },
        async created() {
            await this.init();
            this.loading = false;
        },
        methods: {
            // send request to api to initialize page
            async init() {
                this.teacherId = this.$javalin.pathParams["teacher-id"];
                const response = await fetch(`/api/teachers/${this.teacherId}`);
                this.teacher = await response.json();
            },
            // reset input values to last persisted state in database
            async reset() {
                this.init();
                this.color = 'white';
            },
            // sends put request to api
            // colors inputs green on success or red on database error,
            // sending user an alert with message indicating error 
            async updateTeacher() {
                const request = {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(this.teacher)
                };
                const response = await fetch(`/api/teachers/${this.teacherId}`, request);

                if(response.status==200) {
                    this.teacher = await response.json();
                    this.color = 'greenyellow';
                    setTimeout(() => {
                        this.color = 'white';
                    }, 1000);
                } else if(response.status==400) {
                    const errorResponse = await response.json();
                    this.color = 'red';
                    if(errorResponse.includes('phone_unique')) {
                        alert("Phone number already present in database");
                    } else if(errorResponse.includes('teacher_email_unique')) {
                        alert("Email address already present in database");
                    }
                }
            },
            // sends delete request to api
            // returns to previous page on success
            // indicates error if teacher not found in database
            async deleteTeacher() {
                const response = await fetch(`/api/teachers/${this.teacherId}`, { method: "DELETE" });
                const jsonResponse = await response.json();
                if(jsonResponse != null) {
                    location.href = "/teachers";
                } else {
                    alert("Teacher not present in database");
                }
            },
            getCourses() {
                location.href = `/teachers/${this.teacherId}/courses`;
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
    .name { max-width: 130px }
    .phone-number { max-width: 110px }
    .salary { max-width: 90px }

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