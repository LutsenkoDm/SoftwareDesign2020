package main.exeption;

public class ClientNotFoundExeption extends RuntimeException {
    public ClientNotFoundExeption(String message) {
        super(message);
    }
}
