package com.company;

import java.sql.*;

public class DB {

    private final String url = "jdbc:mysql://gateway01.ap-northeast-1.prod.aws.tidbcloud.com:4000/project_db?useSSL=true&requireSSL=true&verifyServerCertificate=false";
    private final String user = "8vYXXDcAfutpxei.root";
    private final String password = "KweeJRB49NizvwyK";

    private Connection connection;

    public DB() throws SQLException {
        connection = DriverManager.getConnection(url, user, password);
    }

    public ResultSet select() throws SQLException {
        String sql = "SELECT * FROM `user` LIMIT 100";
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }

    public boolean saveGameData(int randomNumber, double humanValue) {
        String sql = "INSERT INTO `user` (`number`, `human`) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, randomNumber);
            preparedStatement.setDouble(2, humanValue); // Using setDouble for exact decimals

            int rowsInserted = preparedStatement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean updateGameDataById(int userId, int randomNumber, int humanValue) {
        String sql = "UPDATE `user` SET `number` = ?, `human` = ? WHERE `id` = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, randomNumber);
            preparedStatement.setInt(2, humanValue);
            preparedStatement.setInt(3, userId);

            int rowsUpdated = preparedStatement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean insertUser(String username, String email, String password) {

        String sql = "INSERT INTO `user` (`number`, `human`) VALUES (0, 0)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsInserted = preparedStatement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}