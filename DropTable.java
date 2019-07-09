import java.sql.DriverManager;
import java.sql.Statement;


public class DropTable {
	public static void main(String[] args) throws Exception {	
		Class.forName( "com.mysql.jdbc.Driver" );
		
		java.sql.Connection conn = DriverManager.getConnection( 
			      "jdbc:mysql://localhost:3306/laptop", // URL of free mysql "db4free"
			      "root", // user name
			      "opo229" ); // password
		
		/*java.sql.Connection conn = DriverManager.getConnection( 
			      "jdbc:mysql://mysql.dur.ac.uk/Pnhvs63_laptop", // URL of  "durham mysql"
			      "nhvs63", // user name
			      "m33adrid" ); // password
		*/
		 String drop = "DROP TABLE extract_data "; 
		 Statement state = conn.createStatement();
			state.executeUpdate(drop);
			System.out.println("Dropped.");
			 
	}
}
