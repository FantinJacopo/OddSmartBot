import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DataBase {
    public Connection connection;
    public DataBase(String database, String port, String username, String password) {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:" + port + "/" + database, username, password);
            if(connection != null) {
                System.out.println("Connessione al database riuscita.");
            }else {
                System.out.println("Connessione al database non riuscita.");
            }
        } catch (SQLException e) {
            System.out.println("Connessione al database non riuscita.");
        }
    }

    public void insertRecord(String table, ArrayList<String> record) {
        String query = "INSERT INTO " + table + " VALUES (";
        for (int i = 0; i < record.size(); i++) {
            if (i == record.size() - 1) {
                query += "'" + record.get(i) + "');";
            } else {
                query += "'" + record.get(i) + "', ";
            }
        }
        try {
            connection.createStatement().execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet selectAll(String table) {
        String query = "SELECT * FROM " + table;
        try {
            return connection.createStatement().executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
