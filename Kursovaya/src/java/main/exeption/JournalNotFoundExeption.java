package main.exeption;

public class JournalNotFoundExeption extends RuntimeException {
    public JournalNotFoundExeption(String message) {
        super(message);
    }
}
