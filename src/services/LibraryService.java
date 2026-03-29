package services;

import models.Library;
import java.util.*;

public class LibraryService {
    private final Library library;
    private final BookService bookService;
    private final UserService userService;
    private static final double RATE = 5.0;

    public LibraryService(Library library, BookService bookService, UserService userService) {
        this.library = library;
        this.bookService = bookService;
        this.userService = userService;
    }

    public String issueBook(int userId, int bookId) {
        if (userService.getUserById(userId) == null)
            return "user not found";

        if (bookService.getBookById(bookId) == null)
            return "book not found";

        if (!bookService.hasStock(bookId))
            return "no copies available";

        if (!userService.belowBorrowCap(userId))
            return "borrow limit reached";

        if (userService.getBorrowedBooks(userId).contains(bookId))
            return "user already has this book";

        int entryId = library.recordLoan(userId, bookId);
        bookService.takeOne(bookId);
        userService.linkBook(userId, bookId);

        return "issued. transaction id: " + entryId + ". due in 14 days";
    }

    public String returnBook(int entryId) {
        Map<String, String> entry = library.getEntry(entryId);
        if (entry == null)
            return "transaction not found";
        if ("RETURNED".equals(entry.get("status")))
            return "already returned";

        int userId = Integer.parseInt(entry.get("userId"));
        int bookId = Integer.parseInt(entry.get("bookId"));

        long late = library.lateDays(entryId);
        double fine = 0;
        if (late > 0) {
            fine = late * RATE;
            userService.addPenalty(userId, fine);
        }

        library.closeEntry(entryId);
        bookService.returnOne(bookId);
        userService.unlinkBook(userId, bookId);

        if (fine > 0)
            return "returned. overdue by " + late + " day(s). fine: Rs." + (int) fine;
        return "returned on time. no fine";
    }

    public String payFees(int userId, double amount) {
        double owed = userService.getDueFees(userId);
        if (owed <= 0) return "no pending fees";
        if (amount > owed) amount = owed;
        userService.deductPenalty(userId, amount);
        return "paid Rs." + (int) amount + ". remaining: Rs." + (int)(owed - amount);
    }

    public List<Map<String, String>> getActiveTransactions() {
        return library.openLoans();
    }

    public Map<String, String> getTransaction(int entryId) {
        return library.getEntry(entryId);
    }
}
