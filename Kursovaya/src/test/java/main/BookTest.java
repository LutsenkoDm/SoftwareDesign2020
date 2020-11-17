package main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.entity.Book;
import main.entity.BookType;
import main.service.BookService;
import main.service.BookTypeService;
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
public class BookTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookTypeService bookTypeService;

    @Autowired
    private BookService bookService;

    @Before
    public void clear() {
        bookTypeService.listBookTypes().clear();
        bookService.listBooks().clear();
    }

    @Test
    public void bookExistTest() throws Exception {
        this.mockMvc
            .perform(get("/books"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(xpath("/html/body/div/div/main/div[1]/div[1]").exists())
            .andExpect(xpath("/html/body/div/div/main/div[1]/div[2]").exists())
            .andExpect(xpath("/html/body/div/div/main/div[1]/div[3]").exists())
            .andExpect(xpath("//*[@id='table']").exists());
    }

    @Test
    public void addBookTest() throws Exception {
        BookType bookType = new BookType();
        bookType.setName("bookType1");
        bookType.setCnt(50L);
        bookType.setFine(100L);
        bookType.setDayCount(900L);
        bookType.setBooks(new HashSet<>());
        bookTypeService.addBookType(bookType);

        Book book = new Book();
        book.setName("book1");
        book.setCnt(5L);
        book.setTypeId(bookType.getId());
        book.setJournalRecords(new HashSet<>());
        bookService.addBook(book);

        bookType.getBooks().add(book);
        String bookTypeId = Long.toString(bookType.getId());

        String xpath = "//*[@id='table']/tbody/tr[" + bookService.listBooks().size() + "]";
        this.mockMvc.perform(get("/books"))
            .andExpect(xpath("//*[@id='table']/tbody/tr").exists())
            .andExpect(xpath(xpath + "/td[1]").string(Long.toString(book.getId())))
            .andExpect(xpath(xpath + "/td[2]").string(book.getName()))
            .andExpect(xpath(xpath + "/td[3]").string(Long.toString(book.getCnt())))
            .andExpect(xpath(xpath + "/td[4]").string(bookTypeId));
    }

    @Test
    public void updateBookTest() throws Exception {
        List<Book> bookList = bookService.listBooks();
        if (bookList.isEmpty()) {
            addBookTest();
            bookList = bookService.listBooks();
        }
        Book bookToUpdate = bookList.get(0);
        bookToUpdate.setName("updatedBook");
        bookToUpdate.setCnt(20L);
        Long bookToUpdateId = bookToUpdate.getId();
        this.mockMvc
            .perform(put("/lib/book/" + bookToUpdateId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bookToUpdate))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
        this.mockMvc.perform(get("/books"))
            .andExpect(xpath("//*[@id='table']/tbody/tr").exists())
            .andExpect(xpath("//*[@id='table']/tbody/tr/td[1]").string(Long.toString(bookToUpdateId)))
            .andExpect(xpath("//*[@id='table']/tbody/tr/td[2]").string(bookToUpdate.getName()))
            .andExpect(xpath("//*[@id='table']/tbody/tr/td[3]").string(Long.toString(bookToUpdate.getCnt())))
            .andExpect(xpath("//*[@id='table']/tbody/tr/td[4]").string(Long.toString(bookToUpdate.getTypeId())));
    }

    @Test
    public void deleteBookTest() throws Exception {
        List<Book> bookList = bookService.listBooks();
        if (bookList.isEmpty()) {
            addBookTest();
            bookList = bookService.listBooks();
        }
        Book firstBookInList = bookList.get(0);
        this.mockMvc
            .perform(delete("/lib/book/" + firstBookInList.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(firstBookInList))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
        String xpath = "//*[@id='table']/tbody/tr[" + bookList.size() + "]";
        this.mockMvc.perform(get("/books"))
            .andExpect(xpath(xpath + "/td[1]").doesNotExist())
            .andExpect(xpath(xpath + "/td[2]").doesNotExist())
            .andExpect(xpath(xpath + "/td[3]").doesNotExist())
            .andExpect(xpath(xpath + "/td[4]").doesNotExist());
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
