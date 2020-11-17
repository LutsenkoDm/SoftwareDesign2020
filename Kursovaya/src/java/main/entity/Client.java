package main.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "patherName")
    private String patherName;

    @Column(name = "passportSeria")
    private String passportSeria;

    @Column(name = "passportNum")
    private String passportNum;

    @OneToMany(targetEntity = JournalRecord.class, cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "clientId", referencedColumnName = "id")
    private Set<JournalRecord> journalRecords = new HashSet<>();
}
