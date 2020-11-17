package main.web;

import lombok.RequiredArgsConstructor;
import main.repository.BookRepository;
import main.repository.BookTypeRepository;
import main.repository.ClientRepository;
import main.repository.JournalRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class TemplateController {
    private final BookTypeRepository bookTypeRepository;
    private final ClientRepository clientRepository;
    private final BookRepository bookRepository;
    private final JournalRepository journalRepository;

    @GetMapping("/journal")
    public String journalPage(Model model) {
        model.addAttribute("journalList", journalRepository.findAll());

        return "journal";
    }

    @GetMapping("/books")
    public String booksPage(Model model) {
        model.addAttribute("bookList", bookRepository.findAll());

        return "books";
    }

    @GetMapping("/clients")
    public String clientsPage(Model model) {
        model.addAttribute("clientList", clientRepository.findAll());

        return "clients";
    }

    @GetMapping("/bookTypes")
    public String bookTypesPage(Model model) {
        model.addAttribute("bookTypesList", bookTypeRepository.findAll());

        return "bookTypes";
    }

    @GetMapping("/registration")
    public String registrationPage() {

        return "registration";
    }
}
