package main.web;

import main.entity.Book;
import main.entity.BookType;
import main.entity.Client;
import main.entity.JournalRecord;
import main.exeption.BookNotFoundExeption;
import main.exeption.BookTypeNotFoundExeption;
import main.exeption.ClientNotFoundExeption;
import main.exeption.JournalNotFoundExeption;
import main.service.BookService;
import main.service.BookTypeService;
import main.service.ClientService;
import main.service.JournalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/lib")
public class LibraryController {

    private final JournalService journalService;
    private final ClientService clientService;
    private final BookService bookService;
    private final BookTypeService bookTypeService;

    public LibraryController(JournalService journalService, ClientService clientService, BookService bookService, BookTypeService bookTypeService) {
        this.journalService = journalService;
        this.clientService = clientService;
        this.bookService = bookService;
        this.bookTypeService = bookTypeService;
    }

    @PostMapping(value = "/addJournalRecord", consumes = "application/json", produces = "application/json")
    public JournalRecord addJournalRecord(@RequestBody JournalRecord newJournalRecord) {
        return journalService.addJournalRecord(newJournalRecord);
    }

    @PostMapping(value = "/addClient", consumes = "application/json", produces = "application/json")
    public Client addClient(@RequestBody Client newClient) {
        return clientService.addClient(newClient);
    }

    @PostMapping(value = "/addBook", consumes = "application/json", produces = "application/json")
    public Book addBook(@RequestBody Book newBook) {
        return bookService.addBook(newBook);
    }

    @PostMapping(value = "/addBookType", consumes = "application/json", produces = "application/json")
    public BookType addBookType(@RequestBody BookType newBookType) {
        return bookTypeService.addBookType(newBookType);
    }

    @GetMapping("/journal")
    public ResponseEntity<List<JournalRecord>> getJournal() {
        return new ResponseEntity<>(journalService.journal(), HttpStatus.OK);
    }

    @GetMapping("/clients")
    public ResponseEntity<List<Client>> getAllClients() {
        return new ResponseEntity<>(clientService.listClients(), HttpStatus.OK);
    }

    @GetMapping("/books")
    public ResponseEntity<List<Book>> getAllBooks() {
        return new ResponseEntity<>(bookService.listBooks(), HttpStatus.OK);
    }

    @GetMapping("/bookTypes")
    public ResponseEntity<List<BookType>> getAllBookTypes() {
        return new ResponseEntity<>(bookTypeService.listBookTypes(), HttpStatus.OK);
    }

    @GetMapping("/journal/{id}")
    public ResponseEntity<JournalRecord> getJournalRecord(@PathVariable("id") long id) {
        try {
            return new ResponseEntity<>(journalService.findJournalRecord(id), HttpStatus.OK);
        }
        catch (JournalNotFoundExeption exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Journal record not found");
        }
    }

    @GetMapping("/client/{id}")
    public ResponseEntity<Client> getClient(@PathVariable("id") long id) {
        try {
            return new ResponseEntity<>(clientService.findClient(id), HttpStatus.OK);
        }
        catch (ClientNotFoundExeption exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
        }
    }

    @GetMapping("/book/{id}")
    public ResponseEntity<Book> getBook(@PathVariable("id") long id) {
        try {
            return new ResponseEntity<>(bookService.findBook(id), HttpStatus.OK);
        }
        catch (BookNotFoundExeption exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        }
    }

    @GetMapping("/bookType/{id}")
    public ResponseEntity<BookType> getBookType(@PathVariable("id") long id) {
        try {
            return new ResponseEntity<>(bookTypeService.findBookType(id), HttpStatus.OK);
        }
        catch (BookTypeNotFoundExeption exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book type not found");
        }
    }

    @DeleteMapping("/journal/{id}")
    public void deleteJournalRecord(@PathVariable("id") long id) {
        try {
            journalService.deleteJournalRecord(id);
        }
        catch (JournalNotFoundExeption exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Journal record not found");
        }
    }

    @DeleteMapping("/client/{id}")
    public void deleteClient(@PathVariable("id") long id) {
        try {
            clientService.deleteClient(id);
        }
        catch (ClientNotFoundExeption exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
        }
    }

    @DeleteMapping("/book/{id}")
    public void deleteBook(@PathVariable("id") long id) {
        try {
            bookService.deleteBook(id);
        }
        catch (BookNotFoundExeption exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        }
    }

    @DeleteMapping("/bookType/{id}")
    public void deleteBookType(@PathVariable("id") long id) {
        try {
            bookTypeService.deleteBookType(id);
        }
        catch (BookTypeNotFoundExeption exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "BookType not found");
        }
    }

    @PutMapping(value = "/journal/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<JournalRecord> updateJournalRecord(@PathVariable("id") long id, @RequestBody JournalRecord newJournalRecord) {
        try {
            JournalRecord updatedJournalRecord = journalService.findJournalRecord(id);

            updatedJournalRecord.setBookId(newJournalRecord.getBookId());
            updatedJournalRecord.setClientId(newJournalRecord.getClientId());
            updatedJournalRecord.setDateBeg(newJournalRecord.getDateBeg());
            updatedJournalRecord.setDateEnd(newJournalRecord.getDateEnd());
            updatedJournalRecord.setDateRet(newJournalRecord.getDateRet());

            return ResponseEntity.ok(journalService.addJournalRecord(updatedJournalRecord));
        }
        catch (JournalNotFoundExeption exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Journal record not found");
        }
    }

    @PutMapping(value = "/client/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Client> updateClient(@PathVariable("id") long id, @RequestBody Client newClient) {
        try {
            Client updatedClient = clientService.findClient(id);

            updatedClient.setFirstName(newClient.getFirstName());
            updatedClient.setLastName(newClient.getLastName());
            updatedClient.setPatherName(newClient.getPatherName());
            updatedClient.setPassportSeria(newClient.getPassportSeria());
            updatedClient.setPassportNum(newClient.getPassportNum());

            return ResponseEntity.ok(clientService.addClient(updatedClient));
        }
        catch (ClientNotFoundExeption exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
        }
    }

    @PutMapping(value = "/book/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Book> updateBook(@PathVariable("id") long id, @RequestBody Book newBook) {
        try {
            Book updatedBook = bookService.findBook(id);

            updatedBook.setName(newBook.getName());
            updatedBook.setCnt(newBook.getCnt());
            updatedBook.setTypeId(newBook.getTypeId());

            return ResponseEntity.ok(bookService.addBook(updatedBook));
        }
        catch (BookNotFoundExeption exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        }
    }

    @PutMapping(value = "/bookType/{id}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<BookType> updateBookType(@PathVariable("id") long id, @RequestBody BookType newBookType) {
        try {
            BookType updatedBookType = bookTypeService.findBookType(id);

            updatedBookType.setName(newBookType.getName());
            updatedBookType.setCnt(newBookType.getCnt());
            updatedBookType.setFine(newBookType.getFine());
            updatedBookType.setDayCount(newBookType.getDayCount());

            return ResponseEntity.ok(bookTypeService.addBookType(updatedBookType));
        }
        catch (BookTypeNotFoundExeption exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "BookType not found");
        }
    }

    @GetMapping("/client/{id}/books")
    public ResponseEntity<Set<Book>> getClientBooks(@PathVariable("id") long id) {
        try {
            Set<Book> clientBooks = new HashSet<>();
            for (long bookId : clientService.getClientBookIds(id)) {
                clientBooks.add(bookService.findBook(bookId));
            }
            return new ResponseEntity<>(clientBooks, HttpStatus.OK);
        }
        catch (ClientNotFoundExeption exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
        }
    }
}
