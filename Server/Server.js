//imports
const express = require('express'); //rest API
const http = require('http'); //http protocal
const fs = require('fs'); //accessing files
const path = require('path');
const bcrypt = require('bcrypt'); //hashing password
const crypto = require('crypto'); //for generating tokens
const { execSync } = require('child_process')

const app = express(); //start express
app.use(express.json()); //automatically parse request body as JSON
const server = http.createServer(app); //create rest API

let clients = new Set(); //clients
let userData;

const generateUserToken = () => {
    const TOKEN = Math.floor(Math.random() * 5) + 1;

    //check if token is already in use
    if (Object.values(userData).some(user => user.token === TOKEN)) {
        return generateUserToken(); //regenerate token
    }
    else {
        return TOKEN; //token is not in use
    }
}

const validate = (username, token) => {
    if ((username in userData) && userData[username]['token'] == token) return true;
    else return false;
}

app.get('/chats', (req, res) => {
    const { username, token, fromUser } = req.query;

    //check credentials
    if (validate(username, token)) {
        if (fromUser in userData) { //check to make sure user exists
            const userChats = userData[fromUser]['chats']; //get all chats user has sent

            let responseBody = '';

            //filter for chats only to requesting user
            userChats.forEach(chat => {
                if (chat['to'] == username) { //if chat is to requesting user
                    responseBody += chat['message'] + '~';
                }
            });

            res.status(200).send(responseBody);
        }
        else { //user not found
            res.status(404).send('404');
        }
    }
    else { //failed verification
        return res.status(401).send('401');
    }
});

app.post('/getToken', (req, res) => {
    try {
        const { username, password } = req.body;

        if (username in userData) {
            const USER_PASSWORD_HASH = userData[username]["password"];

            bcrypt.compare(password, USER_PASSWORD_HASH, (err, result) => {
                if (err) throw err;

                if (result) {
                    const token = crypto.randomBytes(16).toString('hex');
                    userData[username]["token"] = token;
                    res.status(200).send(token); // Login success
                } else {
                    res.status(401).send("401"); // Password mismatch
                }
            });
        } else {
            res.status(401).send("401"); // User not found
        }
    } catch (err) {
        return res.status(500).send("500"); // Server error
    }
});

app.get('/users', (req, res) => {
    const { username, token } = req.query;

    //verify token
    if (validate(username, token)) {
        const users = Object.keys(userData); //list of users

        //format list into a string
        let responseString = "";
        users.forEach(user => {
            if(user != username) responseString += user + "~"
        });

        res.status(200).send(responseString);
    }
    else { //failed verification
        return res.status(401).send('401');
    }
});

app.post('/send', (req, res) => {
    try {
        const { username, token, to, message } = req.body;

        //validate credentials
        if (validate(username, token)) {
            const newChat = {
                "to": to,
                "message": message
            }
            userData[username]['chats'].push(newChat);

            res.status(200).send('200');
        }
        else {
            res.status(401).send('401');
        }
    } catch (err) {
        return res.status(500).send("500"); // Server error
    }
})

// Start server - load files before starting
fs.readFile('Users.json', 'utf-8', (err, data) => {
    if (err) {
        console.error('Error reading file:', err);
    }
    else {
        userData = JSON.parse(data);
        //start server
        server.listen(3000, () => {
            console.log('Server running on http://localhost:3000');
        });
    }
});