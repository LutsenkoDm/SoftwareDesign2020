package main.service;

import main.entity.BookType;

import java.util.List;

public interface BookTypeService {
    List<BookType> listBookTypes();

    BookType findBookType(long id);

    BookType addBookType(BookType bookType);

    void deleteBookType(long id);
}
