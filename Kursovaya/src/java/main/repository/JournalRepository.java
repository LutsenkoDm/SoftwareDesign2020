package main.repository;

import main.entity.JournalRecord;
import org.springframework.data.repository.CrudRepository;

public interface JournalRepository extends CrudRepository<JournalRecord, Long> {
}
