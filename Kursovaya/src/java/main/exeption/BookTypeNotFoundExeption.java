package main.exeption;

public class BookTypeNotFoundExeption extends RuntimeException {
    public BookTypeNotFoundExeption(String message) {
        super(message);
    }
}
