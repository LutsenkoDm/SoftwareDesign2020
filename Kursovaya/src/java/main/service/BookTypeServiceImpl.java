package main.service;

import main.entity.BookType;
import main.exeption.BookTypeNotFoundExeption;
import main.repository.BookTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookTypeServiceImpl implements BookTypeService {

    @Autowired
    private BookTypeRepository bookTypeRepository;

    @Override
    public List<BookType> listBookTypes() {
        return (List<BookType>) bookTypeRepository.findAll();
    }

    @Override
    public BookType findBookType(long id) {
        Optional<BookType> optionalBookType = bookTypeRepository.findById(id);
        if (optionalBookType.isPresent()) {
            return optionalBookType.get();
        }
        else {
            throw new BookTypeNotFoundExeption("Book type  not found");
        }
    }

    @Override
    public BookType addBookType(BookType bookType) {
        return bookTypeRepository.save(bookType);
    }

    @Override
    public void deleteBookType(long id) {
        Optional<BookType> optionalBookType = bookTypeRepository.findById(id);
        if (optionalBookType.isPresent()) {
            bookTypeRepository.delete(optionalBookType.get());
        }
        else {
            throw new BookTypeNotFoundExeption("BookType not found");
        }
    }
}
