package services;

import models.User;
import java.util.*;

public class UserService {
    private final User user;
    private static final int MAX_BORROW = 5;

    public UserService(User user) {
        this.user = user;
    }

    public Map<Integer, Map<String, String>> getAllUsers() {
        return user.listAll();
    }

    public Map<String, String> getUserById(int id) {
        return user.findById(id);
    }

    public List<Map<String, String>> searchByName(String query) {
        return user.matchName(query);
    }

    public boolean registerUser(String name, String className) {
        int id = user.nextAvailableId();
        List<String> row = Arrays.asList(String.valueOf(id), name, className, "[]", "0");
        return user.add(row);
    }

    public boolean updateUser(int id, String name, String className) {
        Map<String, String> existing = user.findById(id);
        if (existing == null) return false;
        List<String> fields = Arrays.asList(
            name, className,
            existing.getOrDefault("books", ""),
            existing.getOrDefault("due_fees", "0")
        );
        return user.edit(fields, id);
    }

    public boolean belowBorrowCap(int id) {
        return user.heldBookIds(id).size() < MAX_BORROW;
    }

    public boolean linkBook(int userId, int bookId) {
        return user.assignBook(userId, bookId);
    }

    public boolean unlinkBook(int userId, int bookId) {
        return user.unassignBook(userId, bookId);
    }

    public boolean addPenalty(int id, double amount) {
        return user.chargeFine(id, amount);
    }

    public boolean deductPenalty(int id, double amount) {
        return user.clearFine(id, amount);
    }

    public List<Integer> getBorrowedBooks(int id) {
        return user.heldBookIds(id);
    }

    public double getDueFees(int id) {
        Map<String, String> member = user.findById(id);
        if (member == null) return 0;
        try {
            return Double.parseDouble(member.getOrDefault("due_fees", "0"));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
