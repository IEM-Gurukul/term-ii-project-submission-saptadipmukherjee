package models;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Library {
    private Map<Integer, Map<String, String>> log = new HashMap<>();
    private static final String CSV = "data/library.csv";
    private static final String[] columns = {"userId", "bookId", "issueDate", "dueDate", "returnDate", "status"};

    public Library() {
        reload();
    }

    private void reload() {
        log.clear();
        try (Scanner sc = new Scanner(new File(CSV))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (!line.isEmpty()) parseLine(line);
            }
        } catch (Exception ex) {
            // file may not exist on first run
        }
    }

    private void parseLine(String line) {
        try (Scanner sc = new Scanner(line)) {
            sc.useDelimiter(",");
            Map<String, String> fields = new HashMap<>();
            int i = 0, id = 0;
            while (sc.hasNext()) {
                String val = sc.next().trim();
                if (i == 0) id = Integer.parseInt(val);
                else if (i - 1 < columns.length) fields.put(columns[i - 1], val);
                i++;
            }
            log.put(id, fields);
        } catch (Exception ex) {
            System.out.println("bad log row: " + ex.getMessage());
        }
    }

    public int recordLoan(int userId, int bookId) {
        int id = newId();
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String due = LocalDate.now().plusDays(14).format(DateTimeFormatter.ISO_DATE);
        Map<String, String> entry = new HashMap<>();
        entry.put("userId", String.valueOf(userId));
        entry.put("bookId", String.valueOf(bookId));
        entry.put("issueDate", today);
        entry.put("dueDate", due);
        entry.put("returnDate", "-");
        entry.put("status", "ISSUED");
        log.put(id, entry);
        append(id, entry);
        return id;
    }

    public boolean closeEntry(int id) {
        Map<String, String> entry = log.get(id);
        if (entry == null || "RETURNED".equals(entry.get("status"))) return false;
        entry.put("returnDate", LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        entry.put("status", "RETURNED");
        flush();
        return true;
    }

    public Map<String, String> getEntry(int id) {
        Map<String, String> entry = log.get(id);
        if (entry == null) return null;
        Map<String, String> out = new HashMap<>(entry);
        out.put("id", String.valueOf(id));
        return out;
    }

    public List<Map<String, String>> openLoans() {
        List<Map<String, String>> out = new ArrayList<>();
        for (Map.Entry<Integer, Map<String, String>> e : log.entrySet()) {
            if ("ISSUED".equals(e.getValue().get("status"))) {
                Map<String, String> row = new HashMap<>(e.getValue());
                row.put("id", String.valueOf(e.getKey()));
                out.add(row);
            }
        }
        return out;
    }

    public long lateDays(int id) {
        Map<String, String> entry = log.get(id);
        if (entry == null) return 0;
        try {
            LocalDate due = LocalDate.parse(entry.get("dueDate"), DateTimeFormatter.ISO_DATE);
            return LocalDate.now().toEpochDay() - due.toEpochDay();
        } catch (Exception ex) {
            return 0;
        }
    }

    private int newId() {
        return log.keySet().stream().mapToInt(i -> i).max().orElse(0) + 1;
    }

    private void append(int id, Map<String, String> entry) {
        try (FileWriter fw = new FileWriter(CSV, true)) {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(id));
            for (String col : columns) row.add(entry.getOrDefault(col, ""));
            fw.append(String.join(",", row)).append("\n");
        } catch (Exception ex) {
            System.out.println("append failed: " + ex.getMessage());
        }
    }

    private void flush() {
        try (FileWriter fw = new FileWriter(CSV)) {
            for (Map.Entry<Integer, Map<String, String>> e : log.entrySet()) {
                List<String> row = new ArrayList<>();
                row.add(String.valueOf(e.getKey()));
                for (String col : columns) row.add(e.getValue().getOrDefault(col, ""));
                fw.append(String.join(",", row)).append("\n");
            }
        } catch (Exception ex) {
            System.out.println("flush failed: " + ex.getMessage());
        }
    }
}
