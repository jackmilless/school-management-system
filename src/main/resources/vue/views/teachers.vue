<template id="teachers">
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
                        <th @click="order('last_name')">Last Name{{sorted('last_name')}}</th>
                        <th @click="order('first_name')">First Name{{sorted('first_name')}}</th>
                        <th @click="order('email_address')">Email Address{{sorted('email_address')}}</th>
                        <th @click="order('phone_number')">Phone Number{{sorted('phone_number')}}</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="teacher in teachers" :key="teacher.teacherId" @click="getTeacher(`${teacher.teacherId}`)">
                        <td>{{teacher.lastName}}</td>
                        <td>{{teacher.firstName}}</td> 
                        <td>{{teacher.emailAddress}}</td>
                        <td>{{teacher.phoneNumber}}</td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="search">
            <div class="search-padding">
                <form @submit.prevent="findByName">
                    <div class="input-padding">
                        <label for="lname">Last Name: </label>   
                        <input class="name" :style="{'background-color': color.findByName}" maxlength="98" pattern="[A-Za-z]{1,98}" title="string of basic characters of the latin alphabet" v-model="lastName" placeholder="last name" id="lname" required>
                        <label for="fname"> First Name: </label>
                        <input class="name" :style="{'background-color': color.findByName}" maxlength="98" pattern="[A-Za-z]{1,98}" title="string of basic characters of the latin alphabet" v-model="firstName" placeholder="first name" id="fname" required>
                    </div>
                    <button type="submit">Find By Name</button>
                </form>
            </div>
            <br>
            <div class="search-padding">
                <form @submit.prevent="findByEmail">
                    <div class="input-padding">
                        <label for="addr">Email Address: </label>
                        <input :style="{'background-color': color.findByEmail}" maxlength="98" type="email" pattern=".+@school\.org" title="any email ending in @school.org" v-model="emailAddress" placeholder="name@school.org" id="addr" required>
                    </div>
                    <button type="submit">Find By Email</button>
                </form>
            </div>
        </div>
        <div class="add">
            <form @submit.prevent="addTeacher">
                <label for="lnamea">Last Name: </label>
                <input class="name" :style="{'background-color': color.addRecord}" maxlength="98" pattern="[A-Za-z]{1,98}" title="string of basic characters of the latin alphabet" v-model="teacher.lastName" placeholder="last name" id="lnamea" required>
                <label for="fnamea"> First Name: </label>
                <input class="name" :style="{'background-color': color.addRecord}" maxlength="98" pattern="[A-Za-z]{1,98}" title="string of basic characters of the latin alphabet" v-model="teacher.firstName" placeholder="first name" id="fnamea" required>
                <br><br>
                <label for="addra">Email Address: </label>
                <input :style="{'background-color': color.addRecord}" maxlength="98" type="email" pattern=".+@school\.org" title="any unique email ending in @school.org" v-model="teacher.emailAddress" placeholder="name@school.org" id="addra" required>
                <label for="pn"> Phone Number: </label>
                <input class="phone-number" :style="{'background-color': color.addRecord}" maxlength="14" type="tel" pattern="\(\d{3}\)-\d{3}-\d{4}" title="any unique phone number in the format (###)-###-####" v-model="teacher.phoneNumber" placeholder="(555)-555-5555" id="pn" required>
                <br><br>
                <label for="sal">Salary: </label>
                <input class="salary" :style="{'background-color': color.addRecord}" maxlength="11" pattern="(\d{1,8}\.\d{0,2})|(\d{1,8})?" title="a number with up to 8 digits before the decimal point and an optional decimal point with up to 2 decimal places" v-model="teacher.salary" placeholder="50000.00" id="sal" required>
                <label for="hd"> Hire Date: </label>
                <input :style="{'background-color': color.addRecord}" type="date" v-model="teacher.hireDate" id="hd" required>
                <br><br><br>
                <button class="submit-record" type="submit">Add Teacher to Records</button>
            </form>
        </div>
    </div>
    <div class="footer">
        <button class="home-button" @click="getHomePage">Home</button>
    </div>
</template>
<script>
    app.component("teachers", {
        template: "#teachers",
        data() {
            return {
                loading: true,
                teachers: [],
                asc: {"last_name":true, "first_name":false, "email_address":false, "phone_number":false},
                orderBy: "last_name",
                selected: 10,
                limit: 10,
                offset: 0,
                firstName: null,
                lastName: null,
                emailAddress: null,
                teacher: {"lastName":null, "firstName":null, "emailAddress":null, "phoneNumber":null, "salary":null, "hireDate":null},
                color: {"addRecord":'white', "findByName":'white', "findByEmail":'white'}
            };
        },
        async created() {
            await this.init();
            this.loading = false;
        },
        methods: {
            // send request to api to initialize page
            async init() {
                const response = await fetch("/api/teachers");
                this.teachers = await response.json();
            },
            // reset table and search fields to initial state (leave submission fields unaffected)
            async reset() {
                this.asc = {"last_name":true, "first_name":false, "email_address":false, "phone_number":false};
                this.orderBy = "last_name";
                this.selected = 10;
                this.limit = 10;
                this.offset = 0;
                this.firstName = null;
                this.lastName = null;
                this.emailAddress = null;
                this.color.findByName = 'white';
                this.color.findByEmail = 'white';
                this.init();
            },
            // primary method used to make get requests to api
            // uses existing state based on search input parameters, ultimately called by findBy methods
            // to determine which query parameters should be passed
            async update(updateData) {
                let url;

                if(this.firstName != null && this.lastName != null) {
                    const fn = this.firstName;
                    const ln = this.lastName;
                    url = `/api/teachers?firstname=${fn}&lastname=${ln}`;
                } else if(this.emailAddress != null) {
                    const ea = this.emailAddress;
                    url = `/api/teachers?emailaddress=${ea}`;
                } else {
                    const orderBy = this.orderBy;
                    const asc = this.asc[orderBy];
                    const limit = this.limit;
                    const offset = this.offset;
                    url = `/api/teachers?orderby=${orderBy}&ascending=${asc}&limit=${limit}&offset=${offset}`;
                }
                
                const response = await fetch(url);
                const jsonResponse = await response.json();
                if(updateData) {
                    this.teachers = jsonResponse;
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
                    this.teachers = jsonResponse;
                } else {
                    this.offset -= this.limit;
                }
            },
            // findBy methods: filter out input in other fields before calling update
            // color inputs to indicate success
            async findByName() {
                this.emailAddress = null;
                await this.update(true);
                this.color.findByName = 'greenyellow';
                setTimeout(() => {
                    this.color.findByName = 'white';
                }, 1000);
            },
            async findByEmail() {
                this.firstName = null;
                this.lastName = null;
                await this.update(true);
                this.color.findByEmail = 'greenyellow';
                setTimeout(() => {
                    this.color.findByEmail = 'white';
                }, 1000);
            },
            // sends post request to api
            // colors inputs green on success or red on database error,
            // sending user an alert with message indicating error 
            async addTeacher() {
                const request = {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(this.teacher)
                };
                const response = await fetch("/api/teachers", request);

                if(response.status==200) {
                    this.color.addRecord = 'greenyellow';
                    setTimeout(() => {
                        this.color.addRecord = 'white';
                    }, 1000);
                    this.teacher = {"lastName":null, "firstName":null, "emailAddress":null, "phoneNumber":null, "salary":null, "hireDate":null};
                } else if(response.status==400) {
                    const errorResponse = await response.json();
                    this.color.addRecord = 'red';
                    if(errorResponse.includes('teacher_email_unique')) {
                        alert("Email address already present in database");
                    } else if(errorResponse.includes('phone_unique')) {
                        alert("Phone number already present in database");
                    }
                }
            },
            getTeacher(teacherId) {
                location.href = `/teachers/${teacherId}`;
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
        width: 560px;
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

    .name { max-width: 130px }
    .phone-number { max-width: 110px }
    .salary { max-width: 90px }
    
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