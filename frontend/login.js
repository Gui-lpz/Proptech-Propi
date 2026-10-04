const API = "http://localhost:8080/api/auth/login";

document
    .getElementById("loginForm")
    .addEventListener("submit", async event => {

        event.preventDefault();

        const mensaje =
            document.getElementById("mensaje");

        try {

            const response = await fetch(API, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    username:
                        document.getElementById("username").value,
                    password:
                        document.getElementById("password").value
                })
            });

            const data = await response.json();

            if (!response.ok) {
                throw new Error(
                    data.message || "No fue posible iniciar sesión."
                );
            }

            localStorage.setItem("jwt_token", data.token);
            localStorage.setItem("username", data.username);
            localStorage.setItem(
                "roles",
                JSON.stringify(data.roles)
            );
            localStorage.setItem(
                "sessionId",
                data.sessionId
            );

            location.href = "./index.html";

        } catch (error) {
            mensaje.textContent = error.message;
        }
    });
