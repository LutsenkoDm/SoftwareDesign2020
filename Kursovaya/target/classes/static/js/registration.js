async function myregister(e) {
    const form = document.querySelector("#regForm");

    const user = await JSON.stringify({
        username: form.username.value.trim(),
        password: form.password.value,
        roles: ['USER']
    })

    await fetch('/user/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: user
    }).then(() => {
        console.log(`success!`);
    })
}