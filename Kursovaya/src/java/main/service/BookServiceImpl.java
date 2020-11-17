package main.service;

import main.entity.Book;
import main.exeption.BookNotFoundExeption;
import main.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public List<Book> listBooks() {
        return (List<Book>) bookRepository.findAll();
    }

    @Override
    public Book findBook(long id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            return optionalBook.get();
        }
        else {
            throw new BookNotFoundExeption("Book not found");
        }
    }

    @Override
    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public void deleteBook(long id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            bookRepository.delete(optionalBook.get());
        }
        else {
            throw new BookNotFoundExeption("Book not found");
        }
    }
}
