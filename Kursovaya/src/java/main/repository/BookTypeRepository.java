package main.repository;

import main.entity.BookType;
import org.springframework.data.repository.CrudRepository;

public interface BookTypeRepository extends CrudRepository<BookType, Long> {
}
