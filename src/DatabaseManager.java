import java.sql.*;

public class DatabaseManager {

    private static String dbPath = "resources/ChatDb.db";
    private static Connection connection = null;

    private static void connect()
    {
        if(connection == null)
        {
            try {
                connection = DriverManager.getConnection(
                        "jdbc:sqlite:" + dbPath);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean check(String login, String password)
    {
        connect();
        String query =  "SELECT login, password "+
                        "FROM Client " +
                        "WHERE login == ? and password == ?";
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next())
                return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
