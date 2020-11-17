let d = document;

async function addRow() {
    let json = await JSON.stringify({
        bookId   : d.getElementById('BookId').value,
        clientId : d.getElementById('ClientId').value,
        dateBeg  : d.getElementById('DateBeg').value,
        dateEnd  : d.getElementById('DateEnd').value,
        dateRet  : d.getElementById('DateRet').value
    });

    const response = await fetch('/lib/addJournalRecord', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: json
    });
    const newJournalRecord = await response.json();

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

        td1.innerHTML = newJournalRecord.id;
        td2.innerHTML = newJournalRecord.bookId;
        td3.innerHTML = newJournalRecord.clientId;
        td4.innerHTML = newJournalRecord.dateBeg;
        td5.innerHTML = newJournalRecord.dateEnd;
        td6.innerHTML = newJournalRecord.dateRet;

        d.getElementById('BookId').value = "";
        d.getElementById('ClientId').value = "";
        d.getElementById('DateBeg').value = "";
        d.getElementById('DateEnd').value = "";
        d.getElementById('DateRet').value = "";

    } else {
        alert("Can`t add this journal record or BookId/Client does not exist")
    }
}

async function getJournalRecord() {
    let JournalRecordId = document.getElementById('navSearch').value;
    let urlString = "/lib/journal/" + JournalRecordId;
    const response = await fetch(urlString, {
        method: 'GET'
    });
    if (response.ok) {
        const journalRecord = await response.json();
        alert("bookId = " + journalRecord.bookId + " clientId = " + journalRecord.clientId + " DateBeg = " + Date.parse(journalRecord.dateBeg) +" DateEnd = " + Date.parse(journalRecord.dateEnd) +"  DateRet = " + Date.parse(journalRecord.dateRet));
    } else {
        alert("JournalRecordId with id " + JournalRecordId + " not Found")
    }
}

$("#navSearch").keypress(async function (event){
    if(event.keyCode === 13) {
        await getJournalRecord();
    }
})

async function deleteJournalRecord() {
    let journalRecordId = document.getElementById('idForDelete').value;
    let table = document.getElementById("table");

    let urlString = "/lib/journal/" + journalRecordId;
    const response = await fetch(urlString, {
        method: 'DELETE'
    });

    if (response.ok) {
        $('table tr').each(async function (row) {
            if ($($(this).find('td')[0]).text() === journalRecordId) {
                table.deleteRow(row);
                return;
            }
        })
    } else {
        alert("JournalRecord for deleting with id " + journalRecordId + " not Found")
    }
}


async function updateJournalRecord() {
    let journalRecordID = document.getElementById('IdOfUpdatedJournalRecord').value;
    let urlString = "/lib/journal/" + journalRecordID;

    let newBookId   = d.getElementById("BookIdToUpdate").value;
    let newClientId = d.getElementById("ClientIdToUpdate").value;
    let newDateBeg  = d.getElementById("DateBegToUpdate").value;
    let newDateEnd  = d.getElementById("DateEndToUpdate").value;
    let newDateRet  = d.getElementById("DateRetToUpdate").value;

    let json = await JSON.stringify({
        bookId:   newBookId,
        clientId: newClientId,
        dateBeg:  newDateBeg,
        dateEnd:  newDateEnd,
        dateRet:  newDateRet
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
            if ($($(this).find('td')[0]).text() === journalRecordID) {
                let rowToUpdate = d.getElementById("table").getElementsByTagName('TR')[row].getElementsByTagName('TD');
                rowToUpdate[1].innerHTML = newBookId;
                rowToUpdate[2].innerHTML = newClientId;
                rowToUpdate[3].innerHTML = newDateBeg;
                rowToUpdate[4].innerHTML = newDateEnd;
                rowToUpdate[5].innerHTML = newDateRet;
                return;
            }
        })
    } else {
        alert("JournalRecord for updating with id " + journalRecordID + " not found or BookId/Client does not exist");
    }
}