package main.service;

import main.entity.Book;

import java.util.List;

public interface BookService {

    List<Book> listBooks();

    Book findBook(long id);

    Book addBook(Book book);

    void deleteBook(long id);
}
