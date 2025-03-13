<template id="classrooms">
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
                        <th @click="order('classroom_number')">Classroom Number{{sorted('classroom_number')}}</th>
                        <th @click="order('capacity')">Capacity{{sorted('capacity')}}</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="classroom in classrooms" :key="classroom.classroomId" @click="getClassroom(`${classroom.classroomId}`)">
                        <td>{{classroom.classroomNumber}}</td>
                        <td>{{classroom.capacity}}</td> 
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="search">
            <div class="search-padding">
                <form @submit.prevent="findByNumber">
                    <div class="input-padding">
                        <label for="cnum">Classroom Number: </label>   
                        <input class="number" :style="{'background-color': color.findByNumber}" title="alphanumeric code up to 8 characters long" v-model="classroomNumber" placeholder="A100" id="cnum" required>
                    </div>
                    <button type="submit">Find By Name</button>
                </form>
            </div>
            <br>
            <div class="search-padding">
                <form @submit.prevent="findByCapacity">
                    <div class="input-padding">
                        <label for="minc">Minimum Capacity: </label>   
                        <input :style="{'background-color': color.findByCapacity}" type="number" min="0" max="100" v-model="minCapacity" placeholder="0" id="minc" required>
                        <label for="maxc"> Maximum Capacity: </label>   
                        <input :style="{'background-color': color.findByCapacity}" type="number" min="0" max="100" v-model="maxCapacity" placeholder="50" id="maxc" required>
                    </div>
                    <button type="submit">Find By Capacity</button>
                </form>
            </div>
        </div>
        <div class="add">
            <form @submit.prevent="addClassroom">
                <label for="cnuma">Classroom Number: </label>   
                <input class="number" :style="{'background-color': color.addRecord}" maxlength="8" pattern="\w{1,8}" title="unique alphanumeric code up to 8 characters long" v-model="classroom.classroomNumber" placeholder="A100" id="cnuma" required>
                <label for="cap"> Capacity: </label>   
                <input :style="{'background-color': color.addRecord}" type="number" min="1" max="100" v-model="classroom.capacity" placeholder="30" id="cap" required>
                <br><br><br>
                <button class="submit-record" type="submit">Add Classroom to Records</button>
            </form>
        </div>
    </div>
    <div class="footer">
        <button class="home-button" @click="getHomePage">Home</button>
    </div>
</template>
<script>
    app.component("classrooms", {
        template: "#classrooms",
        data() {
            return {
                loading: true,
                classrooms: [],
                asc: {"classroom_number":true, "capacity":false},
                orderBy: "classroom_number",
                selected: 10,
                limit: 10,
                offset: 0,
                classroomNumber: null,
                minCapacity: null,
                maxCapacity: null,
                classroom: {"classroomNumber":null, "capacity":null},
                color: {"addRecord":'white', "findByNumber":'white', "findByCapacity":'white'}
            };
        },
        async created() {
            await this.init();
            this.loading = false;
        },
        methods: {
            // send request to api to initialize page
            async init() {
                const response = await fetch("/api/classrooms");
                this.classrooms = await response.json();
            },
            // reset table and search fields to initial state (leave submission fields unaffected)
            async reset() {
                this.asc = {"classroom_number":true, "capacity":false};
                this.orderBy = "classroom_number";
                this.selected = 10;
                this.limit = 10;
                this.offset = 0;
                this.classroomNumber = null;
                this.minCapacity = null;
                this.maxCapacity = null;
                this.color.findByNumber = 'white';
                this.color.findByCapacity = 'white';
                this.init();
            },
            // primary method used to make get requests to api
            // uses existing state based on search input parameters, ultimately called by findBy methods
            // to determine which query parameters should be passed
            async update(updateData) {
                let url;

                if(this.classroomNumber != null) {
                    const cn = this.classroomNumber;
                    url = `/api/classrooms?classroomnumber=${cn}`;
                } else {
                    const orderBy = this.orderBy;
                    const asc = this.asc[orderBy];
                    const limit = this.limit;
                    const offset = this.offset;

                    if(this.minCapacity != null && this.maxCapacity != null) {
                        const min = this.minCapacity;
                        const max = this.maxCapacity;
                        url = `/api/classrooms?orderby=${orderBy}&ascending=${asc}&limit=${limit}&offset=${offset}&mincapacity=${min}&maxcapacity=${max}`;
                    } else {
                        url = `/api/classrooms?orderby=${orderBy}&ascending=${asc}&limit=${limit}&offset=${offset}`;
                    }
                }

                const response = await fetch(url);
                const jsonResponse = await response.json();
                if(updateData) {
                    this.classrooms = jsonResponse;
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
                    this.classrooms = jsonResponse;
                } else {
                    this.offset -= this.limit;
                }
            },
            // findBy methods: filter out input in other fields before calling update
            // color inputs to indicate success
            async findByNumber() {
                this.minCapacity = null;
                this.maxCapacity = null;
                await this.update(true);
                this.color.findByNumber = 'greenyellow';
                setTimeout(() => {
                    this.color.findByNumber = 'white';
                }, 1000);
            },
            async findByCapacity() {
                this.classroomNumber = null;
                await this.update(true);
                this.color.findByCapacity = 'greenyellow';
                setTimeout(() => {
                    this.color.findByCapacity = 'white';
                }, 1000);
            },
            // sends post request to api
            // colors inputs green on success or red on database error,
            // sending user an alert with message indicating error 
            async addClassroom() {
                const request = {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(this.classroom)
                };
                const response = await fetch("/api/classrooms", request);

                if(response.status==200) {
                    this.color.addRecord = 'greenyellow';
                    setTimeout(() => {
                        this.color.addRecord = 'white';
                    }, 1000);
                    this.classroom = {"classroomNumber":null, "capacity":null};
                } else if(response.status==400) {
                    const errorResponse = await response.json();
                    this.color.addRecord = 'red';
                    if(errorResponse.includes('classroom_number_unique')) {
                        alert("Classroom number already present in database");
                    }
                }
            },
            getClassroom(classroomId) {
                location.href = `/classrooms/${classroomId}`;
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
        width: 310px;
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

    .number { max-width: 70px }
    
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