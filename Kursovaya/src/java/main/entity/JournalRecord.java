package main.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "journal")
public class JournalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "bookId")
    private Long bookId;

    @Column(name = "clientId")
    private Long clientId;

    @Column(name = "dateBeg")
    private Timestamp dateBeg;

    @Column(name = "dateEnd")
    private Timestamp dateEnd;

    @Column(name = "dateRet")
    private Timestamp dateRet;
}
