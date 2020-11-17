package main.exeption;

public class BookNotFoundExeption extends RuntimeException {
    public BookNotFoundExeption(String message) {
        super(message);
    }
}
