package main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.entity.Book;
import main.entity.BookType;
import main.entity.Client;
import main.entity.JournalRecord;
import main.service.BookService;
import main.service.BookTypeService;
import main.service.ClientService;
import main.service.JournalService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("admin")
public class Scenarios {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookTypeService bookTypeService;

    @Autowired
    private BookService bookService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private JournalService journalService;


    @Before
    public void clear() {
        bookTypeService.listBookTypes().clear();
        bookService.listBooks().clear();
        clientService.listClients().clear();
        journalService.journal().clear();
    }

    @Test
    public void addScenario() throws Exception {
        final int bookTypesNumber      = 10;
        final int booksNumber          = 30;
        final int clientsNumber        = 15;
        final int journalRecordsNumber = 50;

        for (long i = 1; i <= bookTypesNumber; i++) {
            BookType bookType = new BookType();
            bookType.setName("bookType" + i);
            bookType.setCnt(i + 1);
            bookType.setFine(i + 2);
            bookType.setDayCount(i + 3);
            bookType.setBooks(new HashSet<>());
            bookTypeService.addBookType(bookType);
        }
        List<BookType> bookTypesList = bookTypeService.listBookTypes() ;
        for (long i = 1; i <= booksNumber; i++) {
            Book book = new Book();
            book.setName("book" + i);
            book.setCnt(i + 1);
            long bookTypeId = bookTypesList.get((int)(i % bookTypesNumber)).getId();
            BookType bookType = bookTypeService.findBookType(bookTypeId);
            book.setTypeId(bookType.getId());
            book.setJournalRecords(new HashSet<>());
            bookService.addBook(book);
            bookType.getBooks().add(book);
        }

        for (long i = 1; i <= clientsNumber; i++) {
            Client client = new Client();
            client.setFirstName("firstName" + i);
            client.setLastName("lastName" + i);
            client.setPatherName("patherName" + i);
            client.setPassportSeria("qwerty" + i);
            client.setPassportNum("ABC111" + i);
            client.setJournalRecords(new HashSet<>());
            clientService.addClient(client);
        }

        List<Book> booksList = bookService.listBooks();
        List<Client> clientList = clientService.listClients();
        for (long i = 1; i <= journalRecordsNumber; i++) {
            JournalRecord journalRecord = new JournalRecord();
            long bookId = booksList.get((int)(i % booksNumber)).getId();
            long clientId = clientList.get((int)(i % clientsNumber)).getId();
            Book book = bookService.findBook(bookId);
            Client client = clientService.findClient(clientId);
            journalRecord.setBookId(book.getId());
            journalRecord.setClientId(client.getId());
            long now = System.currentTimeMillis();
            Timestamp dateBeg = new Timestamp(now);
            Timestamp dateEnd = new Timestamp(now + i * 10000);
            Timestamp dateRet = new Timestamp(now + i * 5000);
            journalRecord.setDateBeg(dateBeg);
            journalRecord.setDateEnd(dateEnd);
            journalRecord.setDateRet(dateRet);
            journalService.addJournalRecord(journalRecord);
            book.getJournalRecords().add(journalRecord);
            client.getJournalRecords().add(journalRecord);
        }
        for (long i = 0; i < bookTypesNumber; i++) {
            BookType bookType = bookTypeService.listBookTypes().get((int)i);
            String xpath = "//*[@id='table']/tbody/tr[" + (i+1) + "]";
            this.mockMvc.perform(get("/bookTypes"))
                .andExpect(xpath("//*[@id='table']/tbody/tr").exists())
                .andExpect(xpath(xpath + "/td[1]").string(Long.toString(bookType.getId())))
                .andExpect(xpath(xpath + "/td[2]").string(bookType.getName()))
                .andExpect(xpath(xpath + "/td[3]").string(Long.toString(bookType.getCnt())))
                .andExpect(xpath(xpath + "/td[4]").string(Long.toString(bookType.getFine())))
                .andExpect(xpath(xpath + "/td[5]").string(Long.toString(bookType.getDayCount())));
        }
        for (long i = 0; i < booksNumber; i++) {
            Book book = bookService.listBooks().get((int)i);
            String xpath = "//*[@id='table']/tbody/tr[" + (i+1) + "]";
            this.mockMvc.perform(get("/books"))
                .andExpect(xpath("//*[@id='table']/tbody/tr").exists())
                .andExpect(xpath(xpath + "/td[1]").string(Long.toString(book.getId())))
                .andExpect(xpath(xpath + "/td[2]").string(book.getName()))
                .andExpect(xpath(xpath + "/td[3]").string(Long.toString(book.getCnt())))
                .andExpect(xpath(xpath + "/td[4]").string(Long.toString(book.getTypeId())));
        }
        for (long i = 0; i < clientsNumber; i++) {
            Client client = clientService.listClients().get((int)i);
            String xpath = "//*[@id='table']/tbody/tr[" + (i+1) + "]";
            this.mockMvc.perform(get("/clients"))
                .andExpect(xpath("//*[@id='table']/tbody/tr").exists())
                .andExpect(xpath(xpath + "/td[1]").string(Long.toString(client.getId())))
                .andExpect(xpath(xpath + "/td[2]").string(client.getFirstName()))
                .andExpect(xpath(xpath + "/td[3]").string(client.getLastName()))
                .andExpect(xpath(xpath + "/td[4]").string(client.getPatherName()))
                .andExpect(xpath(xpath + "/td[5]").string(client.getPassportSeria()))
                .andExpect(xpath(xpath + "/td[6]").string(client.getPassportNum()));
        }
        for (long i = 0; i < journalRecordsNumber; i++) {
            JournalRecord journalRecord = journalService.journal().get((int)i);
            String xpath = "//*[@id='table']/tbody/tr[" + (i+1) + "]";
            this.mockMvc.perform(get("/journal"))
                .andExpect(xpath("//*[@id='table']/tbody/tr").exists())
                .andExpect(xpath(xpath + "/td[1]").string(Long.toString(journalRecord.getId())))
                .andExpect(xpath(xpath + "/td[2]").string((Long.toString(journalRecord.getBookId()))))
                .andExpect(xpath(xpath + "/td[3]").string((Long.toString(journalRecord.getClientId()))))
                .andExpect(xpath(xpath + "/td[4]").string(journalRecord.getDateBeg().toString()))
                .andExpect(xpath(xpath + "/td[5]").string(journalRecord.getDateEnd().toString()))
                .andExpect(xpath(xpath + "/td[6]").string(journalRecord.getDateRet() .toString()));
        }
    }

    @Test
    public void putScenario() throws Exception {
        final int bookTypesNumber      = 5;
        final int booksNumber          = 10;
        final int clientsNumber        = 10;
        final int journalRecordsNumber = 20;

        if (bookTypeService.listBookTypes().isEmpty() || bookService.listBooks().isEmpty() ||
            clientService.listClients().isEmpty() || journalService.journal().isEmpty()) {
            addScenario();
        }

        for (long i = 1; i <= bookTypesNumber; i++) {
            BookType bookTypeToUpdate = bookTypeService.listBookTypes().get((int)i);
            bookTypeToUpdate.setName("updatedBookType" + i);
            bookTypeToUpdate.setCnt(i + 11);
            bookTypeToUpdate.setFine(i + 22);
            bookTypeToUpdate.setDayCount(i + 33);
        }

        for (long i = 1; i <= booksNumber; i++) {
            Book bookToUpdate = bookService.listBooks().get((int)i);
            bookTypeService.findBookType(bookToUpdate.getTypeId()).getBooks().remove(bookToUpdate);
            bookToUpdate.setName("updatedBook" + i);
            bookToUpdate.setCnt(i + 11);
            long bookTypeId = bookTypesNumber - 1;
            BookType bookType = bookTypeService.findBookType(bookTypeId);
            bookToUpdate.setTypeId(bookType.getId());
            bookType.getBooks().add(bookToUpdate);
        }

        for (long i = 1; i <= clientsNumber; i++) {
            Client clientToUpdate = clientService.listClients().get((int)i);
            clientToUpdate.setFirstName("updatedFirstName" + i);
            clientToUpdate.setLastName("updatedLastName" + i);
            clientToUpdate.setPatherName("updatedPatherName" + i);
            clientToUpdate.setPassportSeria("updatedQwerty" + i);
            clientToUpdate.setPassportNum("updatedABC111" + i);
            clientToUpdate.setJournalRecords(new HashSet<>());
        }

        for (long i = 1; i <= journalRecordsNumber; i++) {
            JournalRecord journalRecordToUpdate = journalService.journal().get((int)i);
            bookService.findBook(journalRecordToUpdate.getBookId()).getJournalRecords().remove(journalRecordToUpdate);
            clientService.findClient(journalRecordToUpdate.getClientId()).getJournalRecords().remove(journalRecordToUpdate);
            long bookId = booksNumber - 1;
            long clientId = clientsNumber - 1;
            Book book = bookService.findBook(bookId);
            Client client = clientService.findClient(clientId);
            journalRecordToUpdate.setBookId(book.getId());
            journalRecordToUpdate.setClientId(client.getId());
            long now = System.currentTimeMillis();
            Timestamp dateBeg = new Timestamp(now);
            Timestamp dateEnd = new Timestamp(now + i * 200);
            Timestamp dateRet = new Timestamp(now + i * 100);
            journalRecordToUpdate.setDateBeg(dateBeg);
            journalRecordToUpdate.setDateEnd(dateEnd);
            journalRecordToUpdate.setDateRet(dateRet);
            book.getJournalRecords().add(journalRecordToUpdate);
            client.getJournalRecords().add(journalRecordToUpdate);
        }
        for (long i = 0; i < bookTypesNumber; i++) {
            BookType updatedBookType = bookTypeService.listBookTypes().get((int)i);
            this.mockMvc
                .perform(put("/lib/bookType/" + updatedBookType.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(updatedBookType))
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
            String xpath = "//*[@id='table']/tbody/tr[" + (i+1) + "]";
            this.mockMvc.perform(get("/bookTypes"))
                .andExpect(xpath("//*[@id='table']/tbody/tr").exists())
                .andExpect(xpath(xpath + "/td[1]").string(Long.toString(updatedBookType.getId())))
                .andExpect(xpath(xpath + "/td[2]").string(updatedBookType.getName()))
                .andExpect(xpath(xpath + "/td[3]").string(Long.toString(updatedBookType.getCnt())))
                .andExpect(xpath(xpath + "/td[4]").string(Long.toString(updatedBookType.getFine())))
                .andExpect(xpath(xpath + "/td[5]").string(Long.toString(updatedBookType.getDayCount())));
        }
        for (long i = 0; i < booksNumber; i++) {
            Book updatedBook = bookService.listBooks().get((int)i);
            this.mockMvc
                .perform(put("/lib/book/" + updatedBook.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(updatedBook))
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
            String xpath = "//*[@id='table']/tbody/tr[" + (i+1) + "]";
            this.mockMvc.perform(get("/books"))
                .andExpect(xpath("//*[@id='table']/tbody/tr").exists())
                .andExpect(xpath(xpath + "/td[1]").string(Long.toString(updatedBook.getId())))
                .andExpect(xpath(xpath + "/td[2]").string(updatedBook.getName()))
                .andExpect(xpath(xpath + "/td[3]").string(Long.toString(updatedBook.getCnt())))
                .andExpect(xpath(xpath + "/td[4]").string(Long.toString(updatedBook.getTypeId())));
        }
        for (long i = 0; i < clientsNumber; i++) {
            Client updatedClient = clientService.listClients().get((int)i);
            this.mockMvc
                .perform(put("/lib/client/" + updatedClient.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(updatedClient))
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
            String xpath = "//*[@id='table']/tbody/tr[" + (i+1) + "]";
            this.mockMvc.perform(get("/clients"))
                .andExpect(xpath("//*[@id='table']/tbody/tr").exists())
                .andExpect(xpath(xpath + "/td[1]").string(Long.toString(updatedClient.getId())))
                .andExpect(xpath(xpath + "/td[2]").string(updatedClient.getFirstName()))
                .andExpect(xpath(xpath + "/td[3]").string(updatedClient.getLastName()))
                .andExpect(xpath(xpath + "/td[4]").string(updatedClient.getPatherName()))
                .andExpect(xpath(xpath + "/td[5]").string(updatedClient.getPassportSeria()))
                .andExpect(xpath(xpath + "/td[6]").string(updatedClient.getPassportNum()));
        }
        for (long i = 0; i < journalRecordsNumber; i++) {
            JournalRecord updatedJournalRecord = journalService.journal().get((int)i);
            String xpath = "//*[@id='table']/tbody/tr[" + (i+1) + "]";
            this.mockMvc
                .perform(put("/lib/journal/" + updatedJournalRecord.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(updatedJournalRecord))
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
            this.mockMvc.perform(get("/journal"))
                .andExpect(xpath("//*[@id='table']/tbody/tr").exists())
                .andExpect(xpath(xpath + "/td[1]").string(Long.toString(updatedJournalRecord.getId())))
                .andExpect(xpath(xpath + "/td[2]").string((Long.toString(updatedJournalRecord.getBookId()))))
                .andExpect(xpath(xpath + "/td[3]").string((Long.toString(updatedJournalRecord.getClientId()))))
                .andExpect(xpath(xpath + "/td[4]").string(updatedJournalRecord.getDateBeg().toString()))
                .andExpect(xpath(xpath + "/td[5]").string(updatedJournalRecord.getDateEnd().toString()))
                .andExpect(xpath(xpath + "/td[6]").string(updatedJournalRecord.getDateRet() .toString()));
        }
    }

    private static String asJsonString(final Object object) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
