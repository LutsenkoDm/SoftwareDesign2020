package main.service;

import main.entity.JournalRecord;
import main.exeption.JournalNotFoundExeption;
import main.repository.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JournalServiceImpl implements JournalService {

    @Autowired
    private JournalRepository journalRepository;

    @Override
    public List<JournalRecord> journal() {
        return (List<JournalRecord>) journalRepository.findAll();
    }

    @Override
    public JournalRecord findJournalRecord(long id) {
        Optional<JournalRecord> optionalJournalRecord = journalRepository.findById(id);
        if (optionalJournalRecord.isPresent()) {
            return optionalJournalRecord.get();
        }
        else {
            throw new JournalNotFoundExeption("Journal record not found");
        }
    }

    @Override
    public JournalRecord addJournalRecord(JournalRecord journalRecord) {
        return journalRepository.save(journalRecord);
    }

    @Override
    public void deleteJournalRecord(long id) {
        Optional<JournalRecord> optionalJournalRecord = journalRepository.findById(id);
        if (optionalJournalRecord.isPresent()) {
            journalRepository.delete(optionalJournalRecord.get());
        }
        else {
            throw new JournalNotFoundExeption("Journal record not found");
        }
    }
}
