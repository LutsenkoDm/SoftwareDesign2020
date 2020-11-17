let d = document;


async function addRow() {
    let json = await JSON.stringify({
        firstName     :  d.getElementById('FirstName').value,
        lastName      :  d.getElementById('LastName').value,
        patherName    :  d.getElementById('PatherName').value,
        passportSeria :  d.getElementById('PassportSeria').value,
        passportNum   :  d.getElementById('PassportNum').value
    });

    const response = await fetch('/lib/addClient', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: json
    });
    const newClient = await response.json();

    if (response.ok) {
        let tbody = d.getElementById('table').getElementsByTagName('TBODY')[0];
        let row = d.createElement("TR");
        tbody.appendChild(row);

        let td1 = d.createElement("TD");
        let td2 = d.createElement("TD");
        let td3 = d.createElement("TD");
        let td4 = d.createElement("TD");
        let td5 = d.createElement("TD");
        let td6 = d.createElement("TD");

        row.appendChild(td1);
        row.appendChild(td2);
        row.appendChild(td3);
        row.appendChild(td4);
        row.appendChild(td5);
        row.appendChild(td6);

        td1.innerHTML = newClient.id;
        td2.innerHTML = newClient.firstName;
        td3.innerHTML = newClient.lastName;
        td4.innerHTML = newClient.patherName;
        td5.innerHTML = newClient.passportSeria;
        td6.innerHTML = newClient.passportNum;

        d.getElementById('FirstName').value = "";
        d.getElementById('LastName').value = "";
        d.getElementById('PatherName').value = "";
        d.getElementById('PassportSeria').value = "";
        d.getElementById('PassportNum').value = "";

    } else {
        alert("Can`t add this client")
    }
}

async function getClient() {
    let clientId = document.getElementById('navSearch').value;
    let urlString = "/lib/client/" + clientId;
    const response = await fetch(urlString, {
        method: 'GET'
    });
    if (response.ok) {
        const client = await response.json();
        alert("Client firstName = " + client.firstName + " Client fastName = " + client.lastName + " Client patherName = " + client.patherName +" Client passportSeria = " + client.passportSeria +" Client passportNum = " + client.passportNum);
    } else {
        alert("Client with id " + clientId + " not Found")
    }
}

$("#navSearch").keypress(async function (event){
    if(event.keyCode === 13) {
        await getClient();
    }
})

async function deleteClient() {
    let clientId = document.getElementById('idForDelete').value;
    let table = document.getElementById("table");

    let urlString = "/lib/client/" + clientId;
    const response = await fetch(urlString, {
        method: 'DELETE'
    });

    if (response.ok) {
        $('table tr').each(async function (row) {
            if ($($(this).find('td')[0]).text() === clientId) {
                table.deleteRow(row);
                return;
            }
        })
    } else {
        alert("Client for deleting with id " + clientId + " not Found")
    }
}


async function updateClient() {
    let clientId = document.getElementById('IdOfUpdatedClient').value;
    let urlString = "/lib/client/" + clientId;

    let newFirstName     = d.getElementById("FirstNameToUpdate").value;
    let newLastName      = d.getElementById("LastNameToUpdate").value;
    let newPatherName    = d.getElementById("PatherNameToUpdate").value;
    let newPassportSeria = d.getElementById("PassportSeriaToUpdate").value;
    let newPassportNum   = d.getElementById("PassportNumToUpdate").value;

    let json = await JSON.stringify({
        firstName: newFirstName,
        lastName: newLastName,
        patherName: newPatherName,
        passportSeria: newPassportSeria,
        passportNum: newPassportNum
    });

    const response = await fetch(urlString, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: json
    });

    if (response.ok) {
        $('table tr').each(async function (row) {
            if ($($(this).find('td')[0]).text() === clientId) {
                let rowToUpdate = d.getElementById("table").getElementsByTagName('TR')[row].getElementsByTagName('TD');
                rowToUpdate[1].innerHTML = newFirstName;
                rowToUpdate[2].innerHTML = newLastName;
                rowToUpdate[3].innerHTML = newPatherName;
                rowToUpdate[4].innerHTML = newPassportSeria;
                rowToUpdate[5].innerHTML = newPassportNum;
                return;
            }
        })
    } else {
        alert("Client for updating with id " + clientId + " not found");
    }
}