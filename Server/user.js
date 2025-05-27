//FOR DEVELOPER USE ONLY
//edit/modify user database

const fs = require("fs");
const bcrypt = require('bcrypt');

const username = process.argv[2];
const password = process.argv[3];

fs.readFile('Users.json', 'utf-8', (err, data) => {
    if (err) {
        console.error('Error reading file:', err);
    }
    else {
        userData = JSON.parse(data);

        bcrypt.hash(password, 10, (err, hash) => {
            if (err) throw err;

            if (username in userData) { //edit password
                userData[username]["password"] = hash;
            }
            else { //create new user
                userData[username] = {
                    "password": hash,
                    "token": null,
                    "chats": [],
                }
            }

            const str = JSON.stringify(userData, null, 2);
            fs.writeFile('Users.json', str, err => {
                if (err) throw err;
                else console.log('User created');
            })
        });
    }
});