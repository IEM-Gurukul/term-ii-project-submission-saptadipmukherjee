import models.Book;
import models.Library;
import models.User;
import services.BookService;
import services.LibraryService;
import services.UserService;

import java.util.*;

public class Main {
    private static Scanner input = new Scanner(System.in);
    private static BookService bookSvc;
    private static UserService userSvc;
    private static LibraryService libSvc;

    public static void main(String[] args) {
        Book bookModel = new Book();
        User userModel = new User();
        Library libModel = new Library();

        bookSvc = new BookService(bookModel);
        userSvc = new UserService(userModel);
        libSvc = new LibraryService(libModel, bookSvc, userSvc);

        boolean active = true;
        while (active) {
            System.out.println("\n1 books");
            System.out.println("2 users");
            System.out.println("3 issue return");
            System.out.println("0 exit");
            System.out.print("> ");
            int choice;
            try { choice = Integer.parseInt(input.nextLine().trim()); }
            catch (NumberFormatException e) { choice = -1; }
            switch (choice) {
                case 1 -> booksMenu();
                case 2 -> usersMenu();
                case 3 -> txnMenu();
                case 0 -> active = false;
                default -> System.out.println("invalid choice");
            }
        }
    }

    static void booksMenu() {
        System.out.println("\n1 list all");
        System.out.println("2 find by id");
        System.out.println("3 find by name");
        System.out.println("4 find by genre");
        System.out.println("5 find by author");
        System.out.println("6 add");
        System.out.println("7 edit");
        System.out.println("8 delete");
        System.out.println("0 back");
        System.out.print("> ");
        int choice;
        try { choice = Integer.parseInt(input.nextLine().trim()); }
        catch (NumberFormatException e) { choice = -1; }

        switch (choice) {
            case 1 -> {
                Map<Integer, Map<String, String>> all = bookSvc.getAllBooks();
                if (all.isEmpty()) { System.out.println("no books"); break; }
                all.forEach((id, b) ->
                    System.out.println(id + " " + b.getOrDefault("name", "-") + " " + b.getOrDefault("author", "-") + " copies " + b.getOrDefault("copies", "0")));
            }
            case 2 -> {
                System.out.print("id: ");
                int id;
                try { id = Integer.parseInt(input.nextLine().trim()); }
                catch (NumberFormatException e) { System.out.println("invalid id"); break; }
                Map<String, String> b = bookSvc.getBookById(id);
                if (b == null) { System.out.println("not found"); break; }
                System.out.println("id " + id);
                System.out.println("name " + b.getOrDefault("name", "-"));
                System.out.println("genre " + b.getOrDefault("genre", "-"));
                System.out.println("author " + b.getOrDefault("author", "-"));
                System.out.println("year " + b.getOrDefault("year", "-"));
                System.out.println("copies " + b.getOrDefault("copies", "0"));
            }
            case 3 -> {
                System.out.print("name: ");
                List<Map<String, String>> res = bookSvc.searchByName(input.nextLine().trim());
                if (res.isEmpty()) { System.out.println("not found"); break; }
                for (Map<String, String> b : res) {
                    int id = Integer.parseInt(b.getOrDefault("id", "0"));
                    System.out.println("id " + id);
                    System.out.println("name " + b.getOrDefault("name", "-"));
                    System.out.println("genre " + b.getOrDefault("genre", "-"));
                    System.out.println("author " + b.getOrDefault("author", "-"));
                    System.out.println("year " + b.getOrDefault("year", "-"));
                    System.out.println("copies " + b.getOrDefault("copies", "0"));
                }
            }
            case 4 -> {
                System.out.print("genre: ");
                List<Map<String, String>> res = bookSvc.searchByGenre(input.nextLine().trim());
                if (res.isEmpty()) { System.out.println("not found"); break; }
                for (Map<String, String> b : res) {
                    int id = Integer.parseInt(b.getOrDefault("id", "0"));
                    System.out.println("id " + id);
                    System.out.println("name " + b.getOrDefault("name", "-"));
                    System.out.println("genre " + b.getOrDefault("genre", "-"));
                    System.out.println("author " + b.getOrDefault("author", "-"));
                    System.out.println("year " + b.getOrDefault("year", "-"));
                    System.out.println("copies " + b.getOrDefault("copies", "0"));
                }
            }
            case 5 -> {
                System.out.print("author: ");
                List<Map<String, String>> res = bookSvc.searchByAuthor(input.nextLine().trim());
                if (res.isEmpty()) { System.out.println("not found"); break; }
                for (Map<String, String> b : res) {
                    int id = Integer.parseInt(b.getOrDefault("id", "0"));
                    System.out.println("id " + id);
                    System.out.println("name " + b.getOrDefault("name", "-"));
                    System.out.println("genre " + b.getOrDefault("genre", "-"));
                    System.out.println("author " + b.getOrDefault("author", "-"));
                    System.out.println("year " + b.getOrDefault("year", "-"));
                    System.out.println("copies " + b.getOrDefault("copies", "0"));
                }
            }
            case 6 -> {
                System.out.print("name: ");   String name   = input.nextLine().trim();
                System.out.print("genre: ");  String genre  = input.nextLine().trim();
                System.out.print("author: "); String author = input.nextLine().trim();
                System.out.print("year: ");   String year   = input.nextLine().trim();
                System.out.print("copies: ");
                int copies;
                try { copies = Integer.parseInt(input.nextLine().trim()); }
                catch (NumberFormatException e) { System.out.println("invalid copies"); break; }
                System.out.println(bookSvc.addBook(name, genre, author, year, copies) ? "added" : "failed");
            }
            case 7 -> {
                System.out.print("id: ");
                int id;
                try { id = Integer.parseInt(input.nextLine().trim()); }
                catch (NumberFormatException e) { System.out.println("invalid id"); break; }
                Map<String, String> b = bookSvc.getBookById(id);
                if (b == null) { System.out.println("not found"); break; }

                System.out.print("name [" + b.getOrDefault("name", "") + "]: ");
                String name = input.nextLine().trim();
                if (name.isEmpty()) name = b.getOrDefault("name", "");

                System.out.print("genre [" + b.getOrDefault("genre", "") + "]: ");
                String genre = input.nextLine().trim();
                if (genre.isEmpty()) genre = b.getOrDefault("genre", "");

                System.out.print("author [" + b.getOrDefault("author", "") + "]: ");
                String author = input.nextLine().trim();
                if (author.isEmpty()) author = b.getOrDefault("author", "");

                System.out.print("year [" + b.getOrDefault("year", "") + "]: ");
                String year = input.nextLine().trim();
                if (year.isEmpty()) year = b.getOrDefault("year", "");

                System.out.print("copies [" + b.getOrDefault("copies", "0") + "]: ");
                String copiesRaw = input.nextLine().trim();
                int copies = copiesRaw.isEmpty()
                    ? Integer.parseInt(b.getOrDefault("copies", "0"))
                    : Integer.parseInt(copiesRaw);

                System.out.println(bookSvc.updateBook(id, name, genre, author, year, copies) ? "updated" : "failed");
            }
            case 8 -> {
                System.out.print("id: ");
                int id;
                try { id = Integer.parseInt(input.nextLine().trim()); }
                catch (NumberFormatException e) { System.out.println("invalid id"); break; }
                System.out.println(bookSvc.removeBook(id) ? "deleted" : "failed");
            }
            case 0 -> {}
            default -> System.out.println("invalid choice");
        }
    }

    static void usersMenu() {
        System.out.println("\n1 list all");
        System.out.println("2 find by id");
        System.out.println("3 find by name");
        System.out.println("4 register");
        System.out.println("5 edit");
        System.out.println("6 borrowed books");
        System.out.println("7 pay dues");
        System.out.println("0 back");
        System.out.print("> ");
        int choice;
        try { choice = Integer.parseInt(input.nextLine().trim()); }
        catch (NumberFormatException e) { choice = -1; }

        switch (choice) {
            case 1 -> {
                Map<Integer, Map<String, String>> all = userSvc.getAllUsers();
                if (all.isEmpty()) { System.out.println("no users"); break; }
                all.forEach((id, u) ->
                    System.out.println(id + " " + u.getOrDefault("name", "-") + " class " + u.getOrDefault("class", "-") + " dues Rs." + u.getOrDefault("due_fees", "0")));
            }
            case 2 -> {
                System.out.print("id: ");
                int id;
                try { id = Integer.parseInt(input.nextLine().trim()); }
                catch (NumberFormatException e) { System.out.println("invalid id"); break; }
                Map<String, String> u = userSvc.getUserById(id);
                if (u == null) { System.out.println("not found"); break; }
                System.out.println("id " + id);
                System.out.println("name " + u.getOrDefault("name", "-"));
                System.out.println("class " + u.getOrDefault("class", "-"));
                System.out.println("books " + u.getOrDefault("books", "none"));
                System.out.println("dues Rs." + u.getOrDefault("due_fees", "0"));
            }
            case 3 -> {
                System.out.print("name: ");
                List<Map<String, String>> res = userSvc.searchByName(input.nextLine().trim());
                if (res.isEmpty()) { System.out.println("not found"); break; }
                for (Map<String, String> u : res) {
                    int id = Integer.parseInt(u.get("id"));
                    System.out.println("id " + id);
                    System.out.println("name " + u.getOrDefault("name", "-"));
                    System.out.println("class " + u.getOrDefault("class", "-"));
                    System.out.println("books " + u.getOrDefault("books", "none"));
                    System.out.println("dues Rs." + u.getOrDefault("due_fees", "0"));
                }
            }
            case 4 -> {
                System.out.print("name: ");  String name = input.nextLine().trim();
                System.out.print("class: "); String cls  = input.nextLine().trim();
                System.out.println(userSvc.registerUser(name, cls) ? "registered" : "failed");
            }
            case 5 -> {
                System.out.print("id: ");
                int id;
                try { id = Integer.parseInt(input.nextLine().trim()); }
                catch (NumberFormatException e) { System.out.println("invalid id"); break; }
                Map<String, String> u = userSvc.getUserById(id);
                if (u == null) { System.out.println("not found"); break; }

                System.out.print("name [" + u.getOrDefault("name", "") + "]: ");
                String name = input.nextLine().trim();
                if (name.isEmpty()) name = u.getOrDefault("name", "");

                System.out.print("class [" + u.getOrDefault("class", "") + "]: ");
                String cls = input.nextLine().trim();
                if (cls.isEmpty()) cls = u.getOrDefault("class", "");

                System.out.println(userSvc.updateUser(id, name, cls) ? "updated" : "failed");
            }
            case 6 -> {
                System.out.print("id: ");
                int id;
                try { id = Integer.parseInt(input.nextLine().trim()); }
                catch (NumberFormatException e) { System.out.println("invalid id"); break; }
                if (userSvc.getUserById(id) == null) { System.out.println("not found"); break; }
                List<Integer> bookIds = userSvc.getBorrowedBooks(id);
                if (bookIds.isEmpty()) { System.out.println("none borrowed"); break; }
                for (int bid : bookIds) {
                    Map<String, String> b = bookSvc.getBookById(bid);
                    System.out.println(bid + " " + (b != null ? b.getOrDefault("name", "-") + " " + b.getOrDefault("author", "-") : "unknown"));
                }
            }
            case 7 -> {
                System.out.print("id: ");
                int id;
                try { id = Integer.parseInt(input.nextLine().trim()); }
                catch (NumberFormatException e) { System.out.println("invalid id"); break; }
                double owed = userSvc.getDueFees(id);
                System.out.println("dues Rs." + (int) owed);
                if (owed <= 0) break;
                System.out.print("amount Rs.");
                double amount;
                try { amount = Double.parseDouble(input.nextLine().trim()); }
                catch (NumberFormatException e) { System.out.println("invalid amount"); break; }
                System.out.println(libSvc.payFees(id, amount));
            }
            case 0 -> {}
            default -> System.out.println("invalid choice");
        }
    }

    static void txnMenu() {
        System.out.println("\n1 issue");
        System.out.println("2 return");
        System.out.println("3 view transaction");
        System.out.println("4 active loans");
        System.out.println("0 back");
        System.out.print("> ");
        int choice;
        try { choice = Integer.parseInt(input.nextLine().trim()); }
        catch (NumberFormatException e) { choice = -1; }

        switch (choice) {
            case 1 -> {
                System.out.print("user id: ");
                int uid;
                try { uid = Integer.parseInt(input.nextLine().trim()); }
                catch (NumberFormatException e) { System.out.println("invalid id"); break; }
                System.out.print("book id: ");
                int bid;
                try { bid = Integer.parseInt(input.nextLine().trim()); }
                catch (NumberFormatException e) { System.out.println("invalid id"); break; }
                System.out.println(libSvc.issueBook(uid, bid));
            }
            case 2 -> {
                System.out.print("transaction id: ");
                int tid;
                try { tid = Integer.parseInt(input.nextLine().trim()); }
                catch (NumberFormatException e) { System.out.println("invalid id"); break; }
                System.out.println(libSvc.returnBook(tid));
            }
            case 3 -> {
                System.out.print("transaction id: ");
                int tid;
                try { tid = Integer.parseInt(input.nextLine().trim()); }
                catch (NumberFormatException e) { System.out.println("invalid id"); break; }
                Map<String, String> t = libSvc.getTransaction(tid);
                if (t == null) { System.out.println("not found"); break; }
                System.out.println("txn " + tid);
                System.out.println("user " + t.get("userId"));
                System.out.println("book " + t.get("bookId"));
                System.out.println("issued " + t.get("issueDate"));
                System.out.println("due " + t.get("dueDate"));
                System.out.println("returned " + t.get("returnDate"));
                System.out.println("status " + t.get("status"));
            }
            case 4 -> {
                List<Map<String, String>> loans = libSvc.getActiveTransactions();
                if (loans.isEmpty()) { System.out.println("no active loans"); break; }
                for (Map<String, String> t : loans) {
                    int tid = Integer.parseInt(t.get("id"));
                    System.out.println("txn " + tid);
                    System.out.println("user " + t.get("userId"));
                    System.out.println("book " + t.get("bookId"));
                    System.out.println("issued " + t.get("issueDate"));
                    System.out.println("due " + t.get("dueDate"));
                    System.out.println("returned " + t.get("returnDate"));
                    System.out.println("status " + t.get("status"));
                }
            }
            case 0 -> {}
            default -> System.out.println("invalid choice");
        }
    }
}