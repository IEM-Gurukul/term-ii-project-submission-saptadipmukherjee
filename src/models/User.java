package models;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class User {
    private Map<Integer, Map<String, String>> members = new HashMap<>();
    private static final String[] columns = {"name", "class", "books", "due_fees"};
    private static final String CSV = "data/users.csv";

    public User() {
        reload();
    }

    private void reload() {
        members.clear();
        try (Scanner sc = new Scanner(new File(CSV))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (!line.isEmpty()) parseLine(line);
            }
        } catch (Exception ex) {
            System.out.println("cannot load users: " + ex.getMessage());
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
                if (i == 0) {
                    id = Integer.parseInt(val);
                } else if (i - 1 < columns.length) {
                    if (columns[i - 1].equals("books")) {
                        val = val.replace("[", "").replace("]", "").replace("/", ",");
                    }
                    fields.put(columns[i - 1], val);
                }
                i++;
            }
            members.put(id, fields);
        } catch (Exception ex) {
            System.out.println("bad user row: " + ex.getMessage());
        }
    }

    public Map<Integer, Map<String, String>> listAll() {
        return members;
    }

    public Map<String, String> findById(int id) {
        Map<String, String> hit = members.get(id);
        if (hit == null) return null;
        Map<String, String> out = new HashMap<>(hit);
        out.put("id", String.valueOf(id));
        return out;
    }

    public List<Map<String, String>> matchName(String query) {
        List<Map<String, String>> out = new ArrayList<>();
        for (Map.Entry<Integer, Map<String, String>> e : members.entrySet()) {
            if (e.getValue().getOrDefault("name", "").toLowerCase().contains(query.toLowerCase())) {
                Map<String, String> row = new HashMap<>(e.getValue());
                row.put("id", String.valueOf(e.getKey()));
                out.add(row);
            }
        }
        return out;
    }

    public int nextAvailableId() {
        return members.keySet().stream().mapToInt(i -> i).max().orElse(0) + 1;
    }

    public List<Integer> heldBookIds(int id) {
        Map<String, String> member = members.get(id);
        if (member == null) return new ArrayList<>();
        String raw = member.getOrDefault("books", "").trim();
        if (raw.isEmpty()) return new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        for (String s : raw.split(",")) {
            s = s.trim();
            if (!s.isEmpty()) {
                try { ids.add(Integer.parseInt(s)); } catch (NumberFormatException ignored) {}
            }
        }
        return ids;
    }

    public boolean assignBook(int userId, int bookId) {
        Map<String, String> member = members.get(userId);
        if (member == null) return false;
        List<Integer> held = heldBookIds(userId);
        held.add(bookId);
        member.put("books", joinIds(held));
        return flush();
    }

    public boolean unassignBook(int userId, int bookId) {
        Map<String, String> member = members.get(userId);
        if (member == null) return false;
        List<Integer> held = heldBookIds(userId);
        held.remove(Integer.valueOf(bookId));
        member.put("books", joinIds(held));
        return flush();
    }

    public boolean chargeFine(int userId, double amount) {
        Map<String, String> member = members.get(userId);
        if (member == null) return false;
        double current = Double.parseDouble(member.getOrDefault("due_fees", "0"));
        member.put("due_fees", String.valueOf((int)(current + amount)));
        return flush();
    }

    public boolean clearFine(int userId, double amount) {
        Map<String, String> member = members.get(userId);
        if (member == null) return false;
        double current = Double.parseDouble(member.getOrDefault("due_fees", "0"));
        member.put("due_fees", String.valueOf((int) Math.max(0, current - amount)));
        return flush();
    }

    public boolean add(List<String> row) {
        try (FileWriter fw = new FileWriter(CSV, true)) {
            fw.append(String.join(",", row)).append("\n");
            fw.flush();
            parseLine(String.join(",", row));
            return true;
        } catch (Exception ex) {
            System.out.println("add failed: " + ex.getMessage());
            return false;
        }
    }

    public boolean edit(List<String> fields, int userId) {
        if (!members.containsKey(userId)) return false;
        Map<String, String> updated = new HashMap<>();
        for (int i = 0; i < fields.size() && i < columns.length; i++) {
            updated.put(columns[i], fields.get(i));
        }
        members.put(userId, updated);
        return flush();
    }

    private String joinIds(List<Integer> ids) {
        List<String> parts = new ArrayList<>();
        for (int i : ids) parts.add(String.valueOf(i));
        return String.join(",", parts);
    }

    private boolean flush() {
        try (FileWriter fw = new FileWriter(CSV)) {
            for (int id : members.keySet()) {
                List<String> row = new ArrayList<>();
                row.add(String.valueOf(id));
                Map<String, String> fields = members.get(id);
                for (String col : columns) {
                    String val = fields.getOrDefault(col, "");
                    if (col.equals("books")) {
                        val = val.isEmpty() ? "[]" : "[" + val.replace(",", "/") + "]";
                    }
                    row.add(val);
                }
                fw.append(String.join(",", row)).append("\n");
            }
            return true;
        } catch (Exception ex) {
            System.out.println("flush failed: " + ex.getMessage());
            return false;
        }
    }
}
