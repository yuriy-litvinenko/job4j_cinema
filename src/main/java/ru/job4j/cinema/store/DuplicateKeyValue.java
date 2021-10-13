package ru.job4j.cinema.store;

public class DuplicateKeyValue extends RuntimeException {
    public DuplicateKeyValue(String message) {
        super(message);
    }
}
