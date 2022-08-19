import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DatabaseConnection {

    private static final Connection connection;

    static {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("/home/ubuntu/SystemsSulisBot/config.properties"));

            String URL = properties.getProperty("Database");
            String user = properties.getProperty("DatabaseUser");
            String password = properties.getProperty("DatabasePassword");
            connection = DriverManager.getConnection(URL, user, password);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void insert(DatabaseEntity entity) {
        Map<String, String> keyValues = entity.getKeyValues();
        String values = String.format("'%s', false, false", keyValues.values().toString().replace("[", "").replace("]", "").replace(", ", "', '")).replace("'s", "\\'s");
        StringBuilder duplicateInsert = new StringBuilder();
        keyValues.forEach((key, value) -> duplicateInsert.append(String.format("%s = '%s', ", key, value)));
        duplicateInsert.delete(duplicateInsert.length() - 2, duplicateInsert.length());

        try {
            connection.createStatement().execute(String.format("""
                                                               INSERT INTO %s
                                                               VALUES(%s)
                                                               ON DUPLICATE KEY UPDATE
                                                               %s;
                                                               """, entity.getName(), values, duplicateInsert.toString().replace("'s", "\\'s")));

            ResultSet resultSet = connection.createStatement().executeQuery("SELECT ROW_COUNT()");
            resultSet.next();
            if (Integer.parseInt(resultSet.getString("ROW_COUNT()")) == 2) {
                update(entity, Map.of("updated", "true"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void update(DatabaseEntity entity, Map<String, String> keyValues) {
        StringBuilder updatedValues = new StringBuilder();
        keyValues.forEach((key, value) -> updatedValues.append(String.format("%s = %s, ", key, value)));
        updatedValues.delete(updatedValues.length() - 2, updatedValues.length());
        try {
            connection.createStatement().execute(String.format("""
                                                               UPDATE %s
                                                               SET %s
                                                               WHERE link = '%s';
                                                               """, entity.getName(), updatedValues, entity.getLink()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Assignment> selectAssignments() {
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("""
                                                                            SELECT *
                                                                            FROM assignment
                                                                            """);
            return getAssignments(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Assignment> selectAssignments(List<String> conditions) {
        try {
            ResultSet resultSet = connection.createStatement().executeQuery(String.format("""
                                                                                          SELECT *
                                                                                          FROM assignment
                                                                                          WHERE %s
                                                                                          """, whereFormat(conditions)));
            return getAssignments(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Announcement> selectAnnouncements() {
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("""
                                                                            SELECT *
                                                                            FROM announcement
                                                                            """);
            return getAnnouncements(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Announcement> selectAnnouncements(List<String> conditions) {
        try {
            ResultSet resultSet = connection.createStatement().executeQuery(String.format("""
                                                                                          SELECT *
                                                                                          FROM announcement
                                                                                          WHERE %s
                                                                                          """, whereFormat(conditions)));
            return getAnnouncements(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void delete(String table, List<String> conditions) {
        try {
            connection.createStatement().execute(String.format("""
                                                               DELETE FROM %s
                                                               WHERE %s;
                                                               """, table, whereFormat(conditions)));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Assignment> getAssignments(ResultSet resultSet) {
        List<Assignment> assignments = new ArrayList<>();
        try {
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String module = resultSet.getString("module");
                String link = resultSet.getString("link");
                String openDate = resultSet.getString("openDate");
                String dueDate = resultSet.getString("dueDate");

                assignments.add(new Assignment(title, module, link, openDate, dueDate));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return assignments;
    }

    private static List<Announcement> getAnnouncements(ResultSet resultSet) {
        List<Announcement> announcements = new ArrayList<>();
        try {
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String module = resultSet.getString("module");
                String link = resultSet.getString("link");
                String author = resultSet.getString("author");
                String postedAt = resultSet.getString("postedAt");

                announcements.add(new Announcement(title, module, link, author, postedAt));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return announcements;
    }

    private static String whereFormat(List<String> conditions) {
        StringBuilder identifier = new StringBuilder();
        conditions.forEach((condition) -> identifier.append(String.format("%s AND ", condition)));
        return identifier.delete(identifier.length() - 5, identifier.length()).toString();
    }
}
