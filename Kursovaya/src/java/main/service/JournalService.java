package main.service;

import main.entity.JournalRecord;

import java.util.List;

public interface JournalService {

    List<JournalRecord> journal();

    JournalRecord findJournalRecord(long id);

    JournalRecord addJournalRecord(JournalRecord journalRecord);

    void deleteJournalRecord(long id);
}
