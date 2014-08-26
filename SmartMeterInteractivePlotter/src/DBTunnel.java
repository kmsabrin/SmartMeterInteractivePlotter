import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class DBTunnel {
	
	MeterData getData(String buildingID, String startTime, String endTime, String startMonth, String endMonth, String startYear, String endYear) {
		Connection c = null;
		Statement stmt = null;
		MeterData meterData = null;
		
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:smartmeter.db");
//			System.out.println("Opened database successfully ");
		
			Map<String, Integer> monthMap = new HashMap();
			monthMap.put("Jan", 1); monthMap.put("Feb", 2); monthMap.put("Mar", 3);
			monthMap.put("Apr", 4); monthMap.put("May", 5); monthMap.put("Jun", 6);
			monthMap.put("Jul", 7); monthMap.put("Aug", 8); monthMap.put("Sep", 9);
			monthMap.put("Oct", 10); monthMap.put("Nov", 11); monthMap.put("Dec", 12);
			int sMonth = monthMap.get(startMonth);
			int eMonth = monthMap.get(endMonth);
			int sTime = Integer.parseInt(startTime) * 100;
			int eTime = Integer.parseInt(endTime) * 100;
			int sYear = Integer.parseInt(startYear);
			int eYear = Integer.parseInt(endYear);
			
//			System.out.println(sTime + "--" + eTime + "--" + sMonth + "--" + eMonth + "--" + sYear + "--" + eYear);
			
			stmt = c.createStatement();
			String sql = null;
			if (eYear > sYear) {
				sql = "SELECT P, Q, V FROM meters WHERE "
					+ "BLDG_ID == '" + buildingID + "' AND " 
					+ "time >= " + sTime + " AND " 
					+ "time <= " + eTime + " AND "
					+ "(( month >= " + sMonth + " AND " + "year == " + sYear + ") OR"
					+ " (month <= " + eMonth + " AND " + "year == " + eYear + ") OR"
					+ " (year > " + sYear + " AND " + "year < " + eYear + "));";
			}
			else {
				sql = "SELECT P, Q, V FROM meters WHERE "
						+ "BLDG_ID == '" + buildingID + "' AND " 
						+ "time >= " + sTime + " AND " 
						+ "time <= " + eTime + " AND "
						+ "month >= " + sMonth + " AND " 
						+ "month <= " + eMonth + " AND " 
						+ "year == " + eYear + ";";
			}
			
			ResultSet rs = stmt.executeQuery(sql);
			
			meterData = new MeterData();
			
			while (rs.next()) {
				meterData.P.add(rs.getFloat("P"));
				meterData.Q.add(rs.getFloat("Q"));
				meterData.V.add(rs.getFloat("V"));
			}
			
			rs.close();
			stmt.close();
			c.close();
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		
		return meterData;
	}
}
