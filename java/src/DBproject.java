/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class DBproject{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	public DBproject(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");

			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}

	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 *
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException {
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 *
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;

		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 *
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 * obtains the metadata object for the returned result set.  The metadata
		 * contains row and column info.
		*/
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;

		//iterates through the result set and saves the data returned by the query.
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>();
		while (rs.next()){
			List<String> record = new ArrayList<String>();
			for (int i=1; i<=numCol; ++i)
				record.add(rs.getString (i));
			result.add(record);
		}//end while
		stmt.close ();
		return result;
	}//end executeQueryAndReturnResult

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 *
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}

	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current
	 * value of sequence used for autogenerated keys
	 *
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */

	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();

		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 *
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + DBproject.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if

		DBproject esql = null;

		try{
			System.out.println("(1)");

			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}

			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];

			esql = new DBproject (dbname, dbport, user, "");

			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add Doctor");
				System.out.println("2. Add Patient");
				System.out.println("3. Add Appointment");
				System.out.println("4. Make an Appointment");
				System.out.println("5. List appointments of a given doctor");
				System.out.println("6. List all available appointments of a given department");
				System.out.println("7. List total number of different types of appointments per doctor in descending order");
				System.out.println("8. Find total number of patients per doctor with a given status");
				System.out.println("9. < EXIT");

				switch (readChoice()){
					case 1: AddDoctor(esql); break;
					case 2: AddPatient(esql); break;
					case 3: AddAppointment(esql); break;
					case 4: MakeAppointment(esql); break;
					case 5: ListAppointmentsOfDoctor(esql); break;
					case 6: ListAvailableAppointmentsOfDepartment(esql); break;
					case 7: ListStatusNumberOfAppointmentsPerDoctor(esql); break;
					case 8: FindPatientsCountWithStatus(esql); break;
					case 9: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice

	public static void AddDoctor(DBproject esql) {//1
		int maxid;
		int newid;
		try{
			maxid = esql.getCurrSeqVal("SELECT MAX(doctor_ID) FROM Doctor");
			if(maxid != null){
				newid = maxid + 1;
			}
			System.out.print("\tEnter the doctor's name: $");
			String name = in.readLine();

			System.out.print("\tEnter the doctor's specialty: $");
			String spec = in.readLine();

			System.out.print("\tEnter the doctor's department ID: $");
			String dept = in.readLine();

			String query = "INSERT INTO Doctor SELECT "
				+ newid + ", '" + name + "', '" + spec + "', " + dept
				+ "WHERE NOT EXISTS(SELECT * FROM Doctor WHERE doctor_ID = "
				+ newid + ");";

			int rowCount = esql.executeQuery(query);
			System.out.println ("total row(s): " + rowCount);
		}catch(Exception e){
			 System.err.println (e.getMessage());
		}
	}

	public static void AddPatient(DBproject esql) {//2
		int maxid;
		int newid;
		try{
			maxid = esql.getCurrSeqVal("SELECT MAX(patient_ID) FROM Patient");
			if(maxid != null){
				newid = maxid + 1;
			}

			System.out.print("\tEnter the patient's name: $");
			String name = in.readLine();

			System.out.print("\tEnter the patient's birth gender: $");
			String gender = in.readLine();

			System.out.print("\tEnter the patient's age: $");
			String age = in.readLine();

			System.out.print("\tEnter the patient's address: $");
			String address = in.readLine();

			System.out.print("\tEnter the number of patient's appointments: $");
			String appts = in.readLine();

			String query = "INSERT INTO Patient SELECT " + newid + ", '" + name + "', '"
				+ gender + "', " + age + ", '" + address + "', " + appts
				+ "WHERE NOT EXISTS(SELECT * FROM Patient WHERE patient_ID = "
				+ newid + ");";

			int rowCount = esql.executeQuery(query);
			System.out.println ("total row(s): " + rowCount);
		}catch(Exception e){
			 System.err.println (e.getMessage());
		}
	}

	public static void AddAppointment(DBproject esql) {//3
		int maxid;
		int newid;
		try{
			maxid = esql.getCurrSeqVal("SELECT MAX(appnt_ID) FROM Appointment");
			if(maxid != null){
				newid = maxid + 1;
			}

			System.out.print("\tEnter the appointment's date (YYYY-MM-DD): $");
			String date = in.readLine();

			System.out.print("\tEnter the appointment's time slot (H:MM-H:MM): $");
			String slot = in.readLine();

			System.out.print("\tEnter the appointment's status (PA, AC, AV, WL): $");
			String status = in.readLine();

			String query = "INSERT INTO Doctor SELECT "
				+ newid + ", '" + date + "', '" + slot + "', ''" + status
				+ ", '" + date + "', '" + slot + "', ''" + status
				+ "' WHERE NOT EXISTS(SELECT * FROM Appointment WHERE appointment_ID = "
				+ newid + ");";

			int rowCount = esql.executeQuery(query);
			System.out.println ("total row(s): " + rowCount);
		}catch(Exception e){
			 System.err.println (e.getMessage());
		}
	}


	public static void MakeAppointment(DBproject esql) {//4
		// Given a patient, a doctor and an appointment of the doctor that s/he wants to take, add an appointment to the DB
		try{
			System.out.print("\tEnter patient ID: ");
			String pid = in.readLine();
			System.out.print("\tEnter patient name: ");
			String pname = in.readLine();
			System.out.print("\tEnter patient's gender: ");
			String pgender = in.readLine();

		}catch(Exception e){
			System.err.println(e.getMessage());
		}
	}

	public static void ListAppointmentsOfDoctor(DBproject esql) {//5
		// For a doctor ID and a date range, find the list of active and available appointments of the doctor
		try{
			System.out.print("\tEnter the earliest date (YYYY-MM-DD): $");
			String date1 = in.readLine();

			System.out.print("\tEnter the latest date (YYYY-MM-DD): $");
			String date2 = in.readLine();

			System.out.print("\tEnter the ID of a doctor: $");
			String id = in.readLine();

			String query = "SELECT A.appnt_ID, A.adate, A.time_slot, A.status FROM Appointment A WHERE A.adate >= '"
			+ date1 + "' AND A.adate <= '" + date2 + "' AND A.appnt_ID IN (SELECT P.appnt_ID FROM Appointment P, has_appointment H WHERE H.doctor_ID = "
			+ id + " AND H.appt_ID = P.appnt_ID);";

			int rowCount = esql.executeQueryAndPrintResult(query);
			System.out.println ("total row(s): " + rowCount);
		}catch(Exception e){
			System.err.println (e.getMessage());
		}
	}

	public static void ListAvailableAppointmentsOfDepartment(DBproject esql) {//6
		// For a department name and a specific date, find the list of available appointments of the department
		try{
			System.out.print("\tEnter the name of a department: $");
			String dept = in.readLine();

			System.out.print("\tEnter a date (YYYY-MM-DD): $");
			String date = in.readLine();

			String query = "SELECT A.appnt_ID, A.time_slot, A.status FROM Appointment A, searches S WHERE A.adate = "
				+ date +  " AND A.appnt_ID = S.aid AND S.hid IN (SELECT S.hid FROM Hospital H, searches S WHERE H.name = "
				+ dept + " AND H.hospital_ID = S.hid);";

			int rowCount = esql.executeQueryAndPrintResult(query);
			System.out.println ("total row(s): " + rowCount);
		}catch(Exception e){
			System.err.println (e.getMessage());
		}
	}

	public static void ListStatusNumberOfAppointmentsPerDoctor(DBproject esql) {//7
		// Count number of different types of appointments per doctors and list them in descending order
		try{
			String query = "SELECT D.doctor_ID, D.name, D.specialty, A.status, count(A.status) AS C"
			+ " FROM Doctor D, Appointment A, has_appointment H"
			+ " WHERE A.appnt_ID = H.appt_ID AND H.doctor_ID = D.doctor_ID"
			+ " GROUP BY D.doctor_ID, D.name, D.specialty, A.status"
			+ " ORDER BY C Desc;";

			int rowCount = esql.executeQueryAndPrintResult(query);
			System.out.println ("total row(s): " + rowCount);
		}catch(Exception e){
			System.err.println (e.getMessage());
		}
	}


	public static void FindPatientsCountWithStatus(DBproject esql) {//8
		// Find how many patients per doctor there are with a given status (i.e. PA, AC, AV, WL) and list that number per doctor.
		try{
			System.out.print("\tEnter an appointment status: $");
			String status = in.readLine();

			String query = "SELECT D.doctor_ID, D.name, D.specialty, count(S.pid) AS C"
			+ " FROM Doctor D, Searches S, has_appointment H, Appointment A" + " WHERE A.status = "
			+ status + " AND A.appnt_ID = S.aid AND S.aid = H.appt_id AND H.doctor_id = D.doctor_id"
			+ " GROUP BY D.doctor_ID, D.name, D.specialty"
			+ " ORDER BY C Desc;";

			int rowCount = esql.executeQueryAndPrintResult(query);
			System.out.println ("total row(s): " + rowCount);
		}catch(Exception e){
			System.err.println (e.getMessage());
		}
	}
}
