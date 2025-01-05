/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dbc;

import java.sql.*;

/**
 *
 * @author kasso
 */
public class connect {

    private static final String URL = "jdbc:mysql://localhost:3306/inventory_management_system";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection dbConnect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
            return con;
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }
}
