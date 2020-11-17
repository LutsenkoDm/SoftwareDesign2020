package main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.entity.Client;
import main.service.ClientService;
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
public class ClientTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientService clientService;

    @Before
    public void clear() {
        clientService.listClients().clear();
    }

    @Test
    public void clientExistTest() throws Exception {
        this.mockMvc
            .perform(get("/clients"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(xpath("/html/body/div/div/main/div[1]/div[1]").exists())
            .andExpect(xpath("/html/body/div/div/main/div[1]/div[2]").exists())
            .andExpect(xpath("/html/body/div/div/main/div[1]/div[3]").exists())
            .andExpect(xpath("//*[@id='table']").exists());
    }

    @Test
    public void addClientTest() throws Exception {
        Client client = new Client();
        client.setFirstName("firstName");
        client.setLastName("lastName");
        client.setPatherName("patherName");
        client.setPassportSeria("qwerty");
        client.setPassportNum("ABC111");
        client.setJournalRecords(new HashSet<>());
        clientService.addClient(client);

        String xpath = "//*[@id='table']/tbody/tr[" + clientService.listClients().size() + "]";
        this.mockMvc.perform(get("/clients"))
            .andExpect(xpath("//*[@id='table']/tbody/tr").exists())
            .andExpect(xpath(xpath + "/td[1]").string(Long.toString(client.getId())))
            .andExpect(xpath(xpath + "/td[2]").string(client.getFirstName()))
            .andExpect(xpath(xpath + "/td[3]").string(client.getLastName()))
            .andExpect(xpath(xpath + "/td[4]").string(client.getPatherName()))
            .andExpect(xpath(xpath + "/td[5]").string(client.getPassportSeria()))
            .andExpect(xpath(xpath + "/td[6]").string(client.getPassportNum()));
    }

    @Test
    public void updateClientTest() throws Exception {
        List<Client> clientList = clientService.listClients();
        if (clientList.isEmpty()) {
            addClientTest();
            clientList = clientService.listClients();
        }
        Client clientToUpdate = clientList.get(0);
        clientToUpdate.setFirstName("updatedFirstName");
        clientToUpdate.setLastName("updatedLastName");
        clientToUpdate.setPatherName("updatedPatherName");
        clientToUpdate.setPassportNum("updatedABC111");
        clientToUpdate.setPassportSeria("updatedQwerty");
        Long clientToUpdateId = clientToUpdate.getId();
        this.mockMvc
            .perform(put("/lib/client/" + clientToUpdateId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(clientToUpdate))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
        this.mockMvc.perform(get("/clients"))
            .andExpect(xpath("//*[@id='table']/tbody/tr").exists())
            .andExpect(xpath("//*[@id='table']/tbody/tr/td[1]").string(Long.toString(clientToUpdateId)))
            .andExpect(xpath("//*[@id='table']/tbody/tr/td[2]").string(clientToUpdate.getFirstName()))
            .andExpect(xpath("//*[@id='table']/tbody/tr/td[3]").string(clientToUpdate.getLastName()))
            .andExpect(xpath("//*[@id='table']/tbody/tr/td[4]").string(clientToUpdate.getPatherName()))
            .andExpect(xpath("//*[@id='table']/tbody/tr/td[5]").string(clientToUpdate.getPassportSeria()))
            .andExpect(xpath("//*[@id='table']/tbody/tr/td[6]").string(clientToUpdate.getPassportNum()));
    }

    @Test
    public void deleteClientTest() throws Exception {
        List<Client> clientList = clientService.listClients();
        if (clientList.isEmpty()) {
            addClientTest();
            clientList = clientService.listClients();
        }
        Client firstClientInList = clientList.get(0);
        this.mockMvc
            .perform(delete("/lib/client/" + firstClientInList.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(firstClientInList))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
        String xpath = "//*[@id='table']/tbody/tr[" + clientList.size() + "]";
        this.mockMvc.perform(get("/clients"))
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
