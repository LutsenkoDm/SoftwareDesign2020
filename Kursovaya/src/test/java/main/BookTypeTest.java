package main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.entity.BookType;
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
public class BookTypeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookTypeService bookTypeService;

    @Before
    public void clear() {
        bookTypeService.listBookTypes().clear();
    }

    @Test
    public void bookTypesExistTest() throws Exception {
        this.mockMvc
            .perform(get("/bookTypes"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(xpath("/html/body/div/div/main/div[1]/div[1]").exists())
            .andExpect(xpath("/html/body/div/div/main/div[1]/div[2]").exists())
            .andExpect(xpath("/html/body/div/div/main/div[1]/div[3]").exists())
            .andExpect(xpath("//*[@id='table']").exists());
    }

    @Test
    public void addBookTypeTest() throws Exception {
        BookType bookType = new BookType();
        bookType.setName("bookType");
        bookType.setCnt(5L);
        bookType.setFine(10L);
        bookType.setDayCount(90L);
        bookType.setBooks(new HashSet<>());
        bookTypeService.addBookType(bookType);

        String xpath = "//*[@id='table']/tbody/tr[" + bookTypeService.listBookTypes().size() + "]";
        this.mockMvc.perform(get("/bookTypes"))
            .andExpect(xpath("//*[@id='table']/tbody/tr").exists())
            .andExpect(xpath(xpath + "/td[1]").string(Long.toString(bookType.getId())))
            .andExpect(xpath(xpath + "/td[2]").string(bookType.getName()))
            .andExpect(xpath(xpath + "/td[3]").string(Long.toString(bookType.getCnt())))
            .andExpect(xpath(xpath + "/td[4]").string(Long.toString(bookType.getFine())))
            .andExpect(xpath(xpath + "/td[5]").string(Long.toString(bookType.getDayCount())));
    }

    @Test
    public void updateBookTypeTest() throws Exception {
        List<BookType> bookTypesList = bookTypeService.listBookTypes();
        if (bookTypesList.isEmpty()) {
            addBookTypeTest();
            bookTypesList = bookTypeService.listBookTypes();
        }
        BookType bookTypeToUpdate = bookTypesList.get(0);
        bookTypeToUpdate.setName("updatedBookType");
        bookTypeToUpdate.setCnt(10L);
        bookTypeToUpdate.setFine(50L);
        bookTypeToUpdate.setDayCount(88L);
        this.mockMvc
            .perform(put("/lib/bookType/" + bookTypeToUpdate.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bookTypeToUpdate))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
        this.mockMvc.perform(get("/bookTypes"))
            .andExpect(xpath("//*[@id='table']/tbody/tr").exists())
            .andExpect(xpath("//*[@id='table']/tbody/tr/td[1]").string(Long.toString(bookTypeToUpdate.getId())))
            .andExpect(xpath("//*[@id='table']/tbody/tr/td[2]").string(bookTypeToUpdate.getName()))
            .andExpect(xpath("//*[@id='table']/tbody/tr/td[3]").string(Long.toString(bookTypeToUpdate.getCnt())))
            .andExpect(xpath("//*[@id='table']/tbody/tr/td[4]").string(Long.toString(bookTypeToUpdate.getFine())))
            .andExpect(xpath("//*[@id='table']/tbody/tr/td[5]").string(Long.toString(bookTypeToUpdate.getDayCount())));
    }

    @Test
    public void deleteBookTypeTest() throws Exception {
        List<BookType> bookTypesList = bookTypeService.listBookTypes();
        if (bookTypesList.isEmpty()) {
            addBookTypeTest();
            bookTypesList = bookTypeService.listBookTypes();
        }
        this.mockMvc
            .perform(delete("/lib/bookType/" + bookTypesList.get(0).getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bookTypesList.get(0)))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
       String xpath = "//*[@id='table']/tbody/tr[" + bookTypesList.size() + "]";
        this.mockMvc.perform(get("/bookTypes"))
            .andExpect(xpath(xpath + "/td[1]").doesNotExist())
            .andExpect(xpath(xpath + "/td[2]").doesNotExist())
            .andExpect(xpath(xpath + "/td[3]").doesNotExist())
            .andExpect(xpath(xpath + "/td[4]").doesNotExist())
            .andExpect(xpath(xpath + "/td[5]").doesNotExist());
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
