package models;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class Book {
    private Map<Integer, Map<String, String>> shelf = new HashMap<>();
    private Map<String, ArrayList<Integer>> byTitle = new HashMap<>();
    private static final String[] columns = {"name", "genre", "author", "year", "copies"};
    private static final String CSV = "data/books.csv";

    public Book() {
        reload();
    }

    private void reload() {
        shelf.clear();
        byTitle.clear();
        try (Scanner sc = new Scanner(new File(CSV))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (!line.isEmpty()) parseLine(line);
            }
        } catch (Exception ex) {
            System.out.println("cannot load books: " + ex.getMessage());
        }
    }

    private void parseLine(String line) {
        HashMap<String, String> fields = new HashMap<>();
        int id = 0;
        try (Scanner sc = new Scanner(line)) {
            sc.useDelimiter(",");
            int i = 0;
            while (sc.hasNext()) {
                String val = sc.next().trim();
                if (i == 0) id = Integer.parseInt(val);
                else if (i - 1 < columns.length) fields.put(columns[i - 1], val);
                i++;
            }
            shelf.put(id, fields);
            byTitle.computeIfAbsent(fields.getOrDefault("name", ""), k -> new ArrayList<>()).add(id);
        } catch (Exception ex) {
            System.out.println("bad book row: " + ex.getMessage());
        }
    }

    public Map<Integer, Map<String, String>> listAll() {
        return shelf;
    }

    public Map<String, String> findById(int id) {
        Map<String, String> hit = shelf.get(id);
        if (hit == null) return null;
        Map<String, String> out = new HashMap<>(hit);
        out.put("id", String.valueOf(id));
        return out;
    }

    public List<Map<String, String>> matchTitle(String query) {
        List<Map<String, String>> out = new ArrayList<>();
        for (Map.Entry<String, ArrayList<Integer>> e : byTitle.entrySet()) {
            if (e.getKey().toLowerCase().contains(query.toLowerCase())) {
                for (int id : e.getValue()) {
                    Map<String, String> row = new HashMap<>(shelf.get(id));
                    row.put("id", String.valueOf(id));
                    out.add(row);
                }
            }
        }
        return out;
    }

    public List<Map<String, String>> matchGenre(String query) {
        List<Map<String, String>> out = new ArrayList<>();
        for (Map.Entry<Integer, Map<String, String>> e : shelf.entrySet()) {
            if (e.getValue().getOrDefault("genre", "").toLowerCase().contains(query.toLowerCase())) {
                Map<String, String> row = new HashMap<>(e.getValue());
                row.put("id", String.valueOf(e.getKey()));
                out.add(row);
            }
        }
        return out;
    }

    public List<Map<String, String>> matchAuthor(String query) {
        List<Map<String, String>> out = new ArrayList<>();
        for (Map.Entry<Integer, Map<String, String>> e : shelf.entrySet()) {
            if (e.getValue().getOrDefault("author", "").toLowerCase().contains(query.toLowerCase())) {
                Map<String, String> row = new HashMap<>(e.getValue());
                row.put("id", String.valueOf(e.getKey()));
                out.add(row);
            }
        }
        return out;
    }

    public int nextAvailableId() {
        return shelf.keySet().stream().mapToInt(i -> i).max().orElse(0) + 1;
    }

    public boolean save(List<String> row) {
        try (FileWriter fw = new FileWriter(CSV, true)) {
            fw.append(String.join(",", row)).append("\n");
            fw.flush();
            parseLine(String.join(",", row));
            return true;
        } catch (Exception ex) {
            System.out.println("save failed: " + ex.getMessage());
            return false;
        }
    }

    public boolean drop(int id) {
        if (!shelf.containsKey(id)) return false;
        String title = shelf.get(id).getOrDefault("name", "");
        shelf.remove(id);
        if (byTitle.containsKey(title)) {
            byTitle.get(title).remove(Integer.valueOf(id));
            if (byTitle.get(title).isEmpty()) byTitle.remove(title);
        }
        return flush();
    }

    public boolean edit(int id, List<String> newValues) {
        if (!shelf.containsKey(id)) return false;
        Map<String, String> updated = new HashMap<>();
        for (int i = 0; i < newValues.size() && i < columns.length; i++) {
            updated.put(columns[i], newValues.get(i));
        }
        shelf.put(id, updated);
        return flush();
    }

    public boolean lowerStock(int id) {
        Map<String, String> row = shelf.get(id);
        if (row == null) return false;
        int n = Integer.parseInt(row.getOrDefault("copies", "0"));
        if (n <= 0) return false;
        row.put("copies", String.valueOf(n - 1));
        return flush();
    }

    public boolean raiseStock(int id) {
        Map<String, String> row = shelf.get(id);
        if (row == null) return false;
        int n = Integer.parseInt(row.getOrDefault("copies", "0"));
        row.put("copies", String.valueOf(n + 1));
        return flush();
    }

    private boolean flush() {
        try (FileWriter fw = new FileWriter(CSV)) {
            for (int id : shelf.keySet()) {
                List<String> row = new ArrayList<>();
                row.add(String.valueOf(id));
                Map<String, String> fields = shelf.get(id);
                for (String col : columns) row.add(fields.getOrDefault(col, ""));
                fw.append(String.join(",", row)).append("\n");
            }
            return true;
        } catch (Exception ex) {
            System.out.println("flush failed: " + ex.getMessage());
            return false;
        }
    }
}
