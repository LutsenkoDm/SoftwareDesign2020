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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("admin")
public class JournalTest {

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
    public void journalRecordExistTest() throws Exception {
        this.mockMvc
            .perform(get("/journal"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(xpath("/html/body/div/div/main/div[1]/div[1]").exists())
            .andExpect(xpath("/html/body/div/div/main/div[1]/div[2]").exists())
            .andExpect(xpath("/html/body/div/div/main/div[1]/div[3]").exists())
            .andExpect(xpath("//*[@id='table']").exists());
    }

    @Test
    public void addJournalRecordTest() throws Exception {

        BookType bookType = new BookType();
        bookType.setName("bookType1");
        bookType.setCnt(1L);
        bookType.setFine(2L);
        bookType.setDayCount(3L);
        bookType.setBooks(new HashSet<>());
        bookTypeService.addBookType(bookType);

        Book book = new Book();
        book.setName("book1");
        book.setCnt(5L);
        book.setTypeId(bookType.getId());
        book.setJournalRecords(new HashSet<>());
        bookService.addBook(book);

        bookType.getBooks().add(book);

        Client client = new Client();
        client.setFirstName("firstName");
        client.setLastName("lastName");
        client.setPatherName("patherName");
        client.setPassportSeria("qwerty");
        client.setPassportNum("ABC111");
        client.setJournalRecords(new HashSet<>());
        clientService.addClient(client);

        JournalRecord journalRecord = new JournalRecord();
        journalRecord.setBookId(book.getId());
        journalRecord.setClientId(client.getId());
        long now = System.currentTimeMillis();
        Timestamp dateBeg = new Timestamp(now);
        Timestamp dateEnd = new Timestamp(now + 10000);
        Timestamp dateRet = new Timestamp(now + 5000);
        journalRecord.setDateBeg(dateBeg);
        journalRecord.setDateEnd(dateEnd);
        journalRecord.setDateRet(dateRet);
        journalService.addJournalRecord(journalRecord);

        book.getJournalRecords().add(journalRecord);
        client.getJournalRecords().add(journalRecord);

        String xpath = "//*[@id='table']/tbody/tr[" + journalService.journal().size() + "]";
        this.mockMvc.perform(get("/journal"))
            .andExpect(xpath("//*[@id='table']/tbody/tr").exists())
            .andExpect(xpath(xpath + "/td[1]").string(Long.toString(journalRecord.getId())))
            .andExpect(xpath(xpath + "/td[2]").string((Long.toString(journalRecord.getBookId()))))
            .andExpect(xpath(xpath + "/td[3]").string((Long.toString(journalRecord.getClientId()))))
            .andExpect(xpath(xpath + "/td[4]").string(journalRecord.getDateBeg().toString()))
            .andExpect(xpath(xpath + "/td[5]").string(journalRecord.getDateEnd().toString()))
            .andExpect(xpath(xpath + "/td[6]").string(journalRecord.getDateRet() .toString()));
    }

    @Test
    public void updateJournalRecordTest() throws Exception {
        List<JournalRecord> journalRecordList = journalService.journal();
        if (journalRecordList.isEmpty()) {
            addJournalRecordTest();
            journalRecordList = journalService.journal();
        }
        JournalRecord journalRecordToUpdate = journalRecordList.get(0);
        long now = System.currentTimeMillis();
        Timestamp dateBeg = new Timestamp(now);
        Timestamp dateEnd = new Timestamp(now + 8000);
        Timestamp dateRet = new Timestamp(now + 4000);
        journalRecordToUpdate.setDateBeg(dateBeg);
        journalRecordToUpdate.setDateEnd(dateEnd);
        journalRecordToUpdate.setDateRet(dateRet);
        Long journalRecordToUpdateId = journalRecordToUpdate.getId();
        this.mockMvc
            .perform(put("/lib/journal/" + journalRecordToUpdateId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(journalRecordToUpdate))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
        this.mockMvc.perform(get("/journal"))
            .andExpect(xpath("//*[@id='table']/tbody/tr").exists())
            .andExpect(xpath("//*[@id='table']/tbody/tr/td[1]").string(Long.toString(journalRecordToUpdateId)))
            .andExpect(xpath("//*[@id='table']/tbody/tr/td[2]").string(Long.toString(journalRecordToUpdate.getBookId())))
            .andExpect(xpath("//*[@id='table']/tbody/tr/td[3]").string(Long.toString(journalRecordToUpdate.getClientId())))
            .andExpect(xpath("//*[@id='table']/tbody/tr/td[4]").string(journalRecordToUpdate.getDateBeg().toString()))
            .andExpect(xpath("//*[@id='table']/tbody/tr/td[5]").string(journalRecordToUpdate.getDateEnd().toString()))
            .andExpect(xpath("//*[@id='table']/tbody/tr/td[6]").string(journalRecordToUpdate.getDateRet().toString()));
    }

    @Test
    public void deleteJournalRecordTest() throws Exception {
        List<JournalRecord> journalRecordList = journalService.journal();
        if (journalRecordList.isEmpty()) {
            addJournalRecordTest();
            journalRecordList = journalService.journal();
        }
        JournalRecord firstJournalRecordInList = journalRecordList.get(0);
        this.mockMvc
            .perform(delete("/lib/journal/" + firstJournalRecordInList.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(firstJournalRecordInList))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
        String xpath = "//*[@id='table']/tbody/tr[" + journalRecordList.size() + "]";
        this.mockMvc.perform(get("/journal"))
            .andExpect(xpath(xpath + "/td[1]").doesNotExist())
            .andExpect(xpath(xpath + "/td[2]").doesNotExist())
            .andExpect(xpath(xpath + "/td[3]").doesNotExist())
            .andExpect(xpath(xpath + "/td[4]").doesNotExist())
            .andExpect(xpath(xpath + "/td[5]").doesNotExist())
            .andExpect(xpath(xpath + "/td[6]").doesNotExist());
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
