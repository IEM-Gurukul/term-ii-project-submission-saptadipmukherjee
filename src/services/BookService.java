package services;

import models.Book;
import java.util.*;

public class BookService {
    private final Book book;

    public BookService(Book book) {
        this.book = book;
    }

    public Map<Integer, Map<String, String>> getAllBooks() {
        return book.listAll();
    }

    public Map<String, String> getBookById(int id) {
        return book.findById(id);
    }

    public List<Map<String, String>> searchByName(String query) {
        return book.matchTitle(query);
    }

    public List<Map<String, String>> searchByGenre(String query) {
        return book.matchGenre(query);
    }

    public List<Map<String, String>> searchByAuthor(String query) {
        return book.matchAuthor(query);
    }

    public boolean addBook(String name, String genre, String author, String year, int copies) {
        int id = book.nextAvailableId();
        List<String> row = Arrays.asList(String.valueOf(id), name, genre, author, year, String.valueOf(copies));
        return book.save(row);
    }

    public boolean removeBook(int id) {
        return book.drop(id);
    }

    public boolean updateBook(int id, String name, String genre, String author, String year, int copies) {
        return book.edit(id, Arrays.asList(name, genre, author, year, String.valueOf(copies)));
    }

    public boolean hasStock(int id) {
        Map<String, String> row = book.findById(id);
        if (row == null) return false;
        return Integer.parseInt(row.getOrDefault("copies", "0")) > 0;
    }

    public boolean takeOne(int id) {
        return book.lowerStock(id);
    }

    public boolean returnOne(int id) {
        return book.raiseStock(id);
    }
}
