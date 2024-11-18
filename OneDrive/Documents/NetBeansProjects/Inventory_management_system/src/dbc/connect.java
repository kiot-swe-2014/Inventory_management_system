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
public class connect{
    public static Connection dbConnect(){
          try {          
              Class.forName("com.mysql.cj.jdbc.Driver");
              Connection con = DriverManager.getConnection("jdbc:mysql://localhost/inventory_management_system","root","");
              return con;
            } catch (ClassNotFoundException | SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
            }
    }
}
