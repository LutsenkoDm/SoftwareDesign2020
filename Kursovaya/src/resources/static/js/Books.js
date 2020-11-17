"use strict"

let d = document;
let bookTypes = Array();

const bookTypesSelectAdd = d.querySelector("#bookTypesSelectorAdd");
const bookTypesSelectUpdate = d.querySelector("#bookTypesSelectorUpdate");

async function getBookTypes() {
    const response = await fetch('/lib/bookTypes', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    });
    if (response.ok) {
        bookTypes = Array.from(await response.json());
        renderBookTypesSelectAdd();
        renderBookTypesSelectUpdate();
    }
}

function renderBookTypesSelectAdd() {
    bookTypesSelectAdd.innerHTML = '';
    bookTypes.forEach(bookType => {
        bookTypesSelectAdd.innerHTML += `<option value="${bookType.id}">${bookType.name}</option>`
    });
}

function renderBookTypesSelectUpdate() {
    bookTypesSelectUpdate.innerHTML = '';
    bookTypes.forEach(bookType => {
        bookTypesSelectUpdate.innerHTML += `<option value="${bookType.id}">${bookType.name}</option>`
    });
}

async function addRow()
{
    let json = await JSON.stringify({
        name   : d.getElementById('Name').value,
        cnt    : d.getElementById('Cnt').value ,
        typeId : d.getElementById('bookTypesSelectorAdd').value
    });

    const response = await fetch('/lib/addBook', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: json
    });
    const newBook = await response.json();

    if (response.ok) {

        let tbody = d.getElementById('table').getElementsByTagName('TBODY')[0];

        let row = d.createElement("TR");
        tbody.appendChild(row);

        let td1 = d.createElement("TD");
        let td2 = d.createElement("TD");
        let td3 = d.createElement("TD");
        let td4 = d.createElement("TD");

        row.appendChild(td1);
        row.appendChild(td2);
        row.appendChild(td3);
        row.appendChild(td4);

        td1.innerHTML = newBook.id;
        td2.innerHTML = newBook.name;
        td3.innerHTML = newBook.cnt;
        td4.innerHTML = newBook.typeId;

        d.getElementById('Name').value = "";
        d.getElementById('Cnt').value = "";

    } else {
        alert("Can`t add this book");
    }
}

async function getBook() {
    let bookId = document.getElementById('navSearch').value;
    let urlString = "/lib/book/" + bookId;
    const response = await fetch(urlString, {
        method: 'GET'
    });
    if (response.ok) {
        const book = await response.json();
        alert("Book name = " + book.name + " Book cnt = " + book.cnt + " Book typeId = " + book.typeId);
    } else {
        alert("Book with id " + bookId + " not Found")
    }
}

$("#navSearch").keypress(async function (event){
    if(event.keyCode === 13) {
        await getBook();
    }
})

async function deleteBook() {
    let bookId = document.getElementById('idForDelete').value;
    let table = document.getElementById("table");

    let urlString = "/lib/book/" + bookId;
    const response = await fetch(urlString, {
        method: 'DELETE'
    });

    if (response.ok) {
        $('table tr').each(async function (row) {
            if ($($(this).find('td')[0]).text() === bookId) {
                table.deleteRow(row);
                return;
            }
        })
    } else {
        alert("Book for deleting with id " + bookId + " not Found")
    }
}


async function updateBook() {
    let bookId = document.getElementById('IdOfUpdatedBook').value;
    let urlString = "/lib/book/" + bookId;

    let newName = d.getElementById("NameToUpdate").value;
    let newCnt = d.getElementById("CntToUpdate").value;
    let newTypeId = d.getElementById("bookTypesSelectorUpdate").value;

    let json = await JSON.stringify({
        name: newName,
        cnt: newCnt,
        typeId: newTypeId
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
            if ($($(this).find('td')[0]).text() === bookId) {
                let rowToUpdate = d.getElementById("table").getElementsByTagName('TR')[row].getElementsByTagName('TD');
                rowToUpdate[1].innerHTML = newName;
                rowToUpdate[2].innerHTML = newCnt;
                rowToUpdate[3].innerHTML = newTypeId;
                return;
            }
        })
    } else {
        alert("Book for updating with id " + bookId + " not found");
    }
}
