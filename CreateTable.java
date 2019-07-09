import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.jsoup.select.Elements;

public class CreateTable {
	public static void main(String[] args) throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
		
		java.sql.Connection conn = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/laptop", // URL of free mysql
														// "db4free"
				"root", // user name
				"opo229"); // password

		/*
		 * Class.forName( "com.mysql.jdbc.Driver" ); java.sql.Connection conn =
		 * DriverManager.getConnection(
		 * "jdbc:mysql://mysql.dur.ac.uk/Pnhvs63_laptop", // URL of
		 * "durham mysql" "nhvs63", // user name "m33adrid" ); // password
		 */
		String createTable = "CREATE TABLE extract_data " +
				"(Flag int(1), " + // if data is existed Flag =1
				"id INTEGER NOT NULL  AUTO_INCREMENT PRIMARY KEY , " +
				"retail VARCHAR(500)," +
				"brand VARCHAR(500)," +
				"panel_size DECIMAL(3,1)," +
				"colour VARCHAR(1000)," +
				"OS VARCHAR(500)," +
				"CPU VARCHAR(1000)," +
				"memory VARCHAR(500)," +
				"HDD VARCHAR(500)," +
				"price DECIMAL(6,2),"+
				"productlink VARCHAR(10000))"
		       ;
		Statement state = conn.createStatement();
		state.executeUpdate(createTable);
		System.out.println("Created.");
	}
}
