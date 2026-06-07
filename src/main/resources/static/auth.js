
async function register(){

    const user = {

        name:
        document.getElementById("name").value,

        email:
        document.getElementById("email").value,

        password:
        document.getElementById("password").value
    };

    const response = await fetch(

        "http://localhost:8080/auth/register",

        {

            method:"POST",

            headers:{
                "Content-Type":"application/json"
            },

            body:JSON.stringify(user)
        }
    );

    const data = await response.text();

    document.getElementById(
        "message"
    ).innerHTML = data;
}

async function login(){

    const user = {

        email:
        document.getElementById("email").value,

        password:
        document.getElementById("password").value
    };

    const response = await fetch(

        "http://localhost:8080/auth/login",

        {

            method:"POST",

            headers:{
                "Content-Type":"application/json"
            },

            body:JSON.stringify(user)
        }
    );

    const data = await response.text();

    document.getElementById(
        "message"
    ).innerHTML = data;

    // SUCCESS LOGIN

    if(data === "Login Successful!"){

        localStorage.setItem(
            "loggedInUser",
            user.email
        );

        window.location.href = "index.html";
    }
}

